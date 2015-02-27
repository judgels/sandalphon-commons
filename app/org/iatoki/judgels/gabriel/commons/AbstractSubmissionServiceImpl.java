package org.iatoki.judgels.gabriel.commons;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.iatoki.judgels.commons.IdentityUtils;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.GradingSource;
import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.gabriel.commons.models.daos.interfaces.BaseGradingDao;
import org.iatoki.judgels.gabriel.commons.models.daos.interfaces.BaseSubmissionDao;
import org.iatoki.judgels.gabriel.commons.models.domains.AbstractGradingModel;
import org.iatoki.judgels.gabriel.commons.models.domains.AbstractSubmissionModel;
import org.iatoki.judgels.sealtiel.client.ClientMessage;
import org.iatoki.judgels.sealtiel.client.Sealtiel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractSubmissionServiceImpl<SM extends AbstractSubmissionModel, GM extends AbstractGradingModel> implements SubmissionService {
    private final BaseSubmissionDao<SM> submissionDao;
    private final BaseGradingDao<GM> gradingDao;
    private final Sealtiel sealtiel;
    private final String gabrielClientJid;

    protected AbstractSubmissionServiceImpl(BaseSubmissionDao<SM> submissionDao, BaseGradingDao<GM> gradingDao, Sealtiel sealtiel, String gabrielClientJid) {
        this.submissionDao = submissionDao;
        this.gradingDao = gradingDao;
        this.sealtiel = sealtiel;
        this.gabrielClientJid = gabrielClientJid;
    }

    @Override
    public Submission findSubmissionById(long submissionId) {
        SM submissionModel = submissionDao.findById(submissionId);
        List<GM> gradingModels = gradingDao.findSortedByFilters("id", "asc", "", ImmutableMap.of("submissionJid", submissionModel.jid), 0, -1);

        return createSubmissionFromModels(submissionModel, gradingModels);
    }

    @Override
    public long countSubmissionsByContestJidByUser(String contestJid, String problemJid, String userJid) {
        return submissionDao.countByContestJidAndUserJidAndProblemJid(contestJid, userJid, problemJid);
    }

    @Override
    public List<Submission> findAllSubmissionsByContestJid(String contestJid) {
        List<SM> submissionModels = submissionDao.findSortedByFilters("id", "asc", "", ImmutableMap.of("contestJid", contestJid), 0, -1);
        Map<String, List<GM>> gradingModelsMap = gradingDao.findGradingsForSubmissions(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<Submission> findNewSubmissionsByContestJidByUsers(String contestJid, List<String> problemJids, List<String> userJids, long lastTime) {
        List<SM> allSubmissionModels = submissionDao.findByContestJidInUserJidsAndProblemJids(contestJid, userJids, problemJids);
        Map<String, List<GM>> gradingModelsMap = gradingDao.findGradingsForSubmissionsSinceLastTime(Lists.transform(allSubmissionModels, m -> m.jid), lastTime);
        Map<String, List<GM>> finalGradingModelsMap = gradingDao.findGradingsForSubmissions(gradingModelsMap.keySet().stream().collect(Collectors.toList()));
        List<SM> submissionModels = allSubmissionModels.stream().filter(s -> finalGradingModelsMap.containsKey(s.jid)).collect(Collectors.toList());

        List<Submission> tempResult = Lists.transform(submissionModels, m -> createSubmissionFromModels(m, finalGradingModelsMap.get(m.jid)));

        Map<String, Map<String, List<SM>>> groupedSubmissionModels = new HashMap<>();
        for (SM sM : allSubmissionModels) {
            if (groupedSubmissionModels.containsKey(sM.problemJid)) {
                if (groupedSubmissionModels.get(sM.problemJid).containsKey(sM.userCreate)) {
                    groupedSubmissionModels.get(sM.problemJid).get(sM.userCreate).add(sM);
                } else {
                    List<SM> newList = new ArrayList<>();
                    newList.add(sM);
                    groupedSubmissionModels.get(sM.problemJid).put(sM.userCreate, newList);
                }
            } else {
                Map<String, List<SM>> newMap = new HashMap<>();
                List<SM> newList = new ArrayList<>();
                newList.add(sM);
                newMap.put(sM.userCreate, newList);
                groupedSubmissionModels.put(sM.problemJid, newMap);
            }
        }

        Map<String, Submission> groupedSubmissions = new HashMap<>();
        for (Submission submission : tempResult) {
            groupedSubmissions.put(submission.getJid(), submission);
        }

        ImmutableList.Builder<Submission> submissionBuilder = ImmutableList.builder();
        ImmutableList.Builder<SM> emptyGradingSubmissionModelBuilder = ImmutableList.builder();

        for (Submission submission : tempResult) {
            submissionBuilder.add(submission);
            if (submission.getGradings().size() > 1) {
                for (SM sM : groupedSubmissionModels.get(submission.getProblemJid()).get(submission.getAuthorJid())) {
                    if (!groupedSubmissions.containsKey(sM.jid)) {
                        emptyGradingSubmissionModelBuilder.add(sM);
                    }
                }
            }
        }

        Map<String, List<GM>> additionalGradingModelsMap = gradingDao.findGradingsForSubmissions(Lists.transform(emptyGradingSubmissionModelBuilder.build(), m -> m.jid));

        List<SM> toBeAddedSubmissions = allSubmissionModels.stream().filter(s -> additionalGradingModelsMap.containsKey(s.jid)).collect(Collectors.toList());

        submissionBuilder.addAll(Lists.transform(toBeAddedSubmissions, m -> createSubmissionFromModels(m, additionalGradingModelsMap.get(m.jid))));

        return submissionBuilder.build();
    }

    @Override
    public List<Submission> findSubmissionsWithoutGradingsByJids(List<String> submissionJids) {
        List<SM> submissionModels = submissionDao.findByJids(submissionJids);

        return Lists.transform(submissionModels, m -> createSubmissionFromModel(m));
    }

    @Override
    public List<Submission> findSubmissionsWithoutGradingsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String contestJid) {
        ImmutableMap.Builder<String, String> filterColumnsBuilder = ImmutableMap.builder();
        if (authorJid != null) {
            filterColumnsBuilder.put("userCreate", authorJid);
        }
        if (problemJid != null) {
            filterColumnsBuilder.put("problemJid", problemJid);
        }
        if (contestJid != null) {
            filterColumnsBuilder.put("contestJid", contestJid);
        }

        Map<String, String> filterColumns = filterColumnsBuilder.build();

        long totalRowsCount = submissionDao.countByFilters("", filterColumns);
        List<SM> submissionModels = submissionDao.findSortedByFilters(orderBy, orderDir, "", filterColumns, 0, -1);

        return Lists.transform(submissionModels, m -> createSubmissionFromModel(m));
    }

    @Override
    public Page<Submission> pageSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String contestJid) {
        ImmutableMap.Builder<String, String> filterColumnsBuilder = ImmutableMap.builder();
        if (authorJid != null) {
            filterColumnsBuilder.put("userCreate", authorJid);
        }
        if (problemJid != null) {
            filterColumnsBuilder.put("problemJid", problemJid);
        }
        if (contestJid != null) {
            filterColumnsBuilder.put("contestJid", contestJid);
        }

        Map<String, String> filterColumns = filterColumnsBuilder.build();

        long totalRowsCount = submissionDao.countByFilters("", filterColumns);
        List<SM> submissionModels = submissionDao.findSortedByFilters(orderBy, orderDir, "", filterColumns, pageIndex * pageSize, pageSize);
        Map<String, List<GM>> gradingModelsMap = gradingDao.findGradingsForSubmissions(Lists.transform(submissionModels, m -> m.jid));

        List<Submission> submissions = Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));

        return new Page<>(submissions, totalRowsCount, pageIndex, pageSize);
    }

    @Override
    public final String submit(String problemJid, String contestJid, String gradingEngine, String gradingLanguage, GradingSource gradingSource) {
        SM submissionModel = submissionDao.createSubmissionModel();

        submissionModel.problemJid = problemJid;
        submissionModel.contestJid = contestJid;
        submissionModel.gradingEngine = gradingEngine;
        submissionModel.gradingLanguage = gradingLanguage;

        submissionDao.persist(submissionModel, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        requestGrading(submissionModel, gradingSource, false);

        return submissionModel.jid;
    }

    @Override
    public void regrade(String submissionJid, GradingSource gradingSource) {
        SM submissionModel = submissionDao.findByJid(submissionJid);

        requestGrading(submissionModel, gradingSource, true);
    }

    @Override
    public final void grade(String gradingJid, GradingResult result, String grader, String graderIpAddress) {
        GM gradingModel = gradingDao.findByJid(gradingJid);

        gradingModel.verdictCode = result.getVerdict().getCode();
        gradingModel.verdictName = result.getVerdict().getName();
        gradingModel.score = result.getScore();
        gradingModel.details = result.getDetailsAsJson();

        gradingDao.edit(gradingModel, grader, graderIpAddress);
    }

    private Submission createSubmissionFromModel(SM submissionModel) {
        return createSubmissionFromModels(submissionModel, ImmutableList.of());
    }

    private Submission createSubmissionFromModels(SM submissionModel, List<GM> gradingModels) {
        return new Submission(submissionModel.id, submissionModel.jid, submissionModel.problemJid, submissionModel.contestJid, submissionModel.userCreate, submissionModel.gradingEngine, submissionModel.gradingLanguage, new Date(submissionModel.timeCreate),
                Lists.transform(gradingModels, m -> createGradingFromModel(m))
        );
    }

    private Grading createGradingFromModel(GM gradingModel) {
        return new Grading(gradingModel.id, gradingModel.jid, new Verdict(gradingModel.verdictCode, gradingModel.verdictName), gradingModel.score, gradingModel.details);
    }

    private void requestGrading(SM submissionModel, GradingSource gradingSource, boolean isRegrading) {
        GM gradingModel = gradingDao.createGradingModel();

        gradingModel.submissionJid = submissionModel.jid;
        gradingModel.verdictCode = "?";
        gradingModel.verdictName = "Pending";
        gradingModel.score = 0;

        gradingDao.persist(gradingModel, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SubmissionAdapter adapter = SubmissionAdapters.fromGradingEngine(submissionModel.gradingEngine);

        GradingRequest request = adapter.createGradingRequest(gradingModel.jid, submissionModel.problemJid, submissionModel.gradingEngine, submissionModel.gradingLanguage, gradingSource);

        try {
            if (isRegrading) {
                sealtiel.sendLowPriorityMessage(new ClientMessage(gabrielClientJid, request.getClass().getSimpleName(), new Gson().toJson(request)));
            } else {
                sealtiel.sendMessage(new ClientMessage(gabrielClientJid, request.getClass().getSimpleName(), new Gson().toJson(request)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
