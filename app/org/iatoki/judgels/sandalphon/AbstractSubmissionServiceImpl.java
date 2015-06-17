package org.iatoki.judgels.sandalphon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.GradingSource;
import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.sandalphon.models.daos.interfaces.BaseGradingDao;
import org.iatoki.judgels.sandalphon.models.daos.interfaces.BaseSubmissionDao;
import org.iatoki.judgels.sandalphon.models.domains.AbstractGradingModel;
import org.iatoki.judgels.sandalphon.models.domains.AbstractSubmissionModel;
import org.iatoki.judgels.sealtiel.ClientMessage;
import org.iatoki.judgels.sealtiel.Sealtiel;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public Submission findSubmissionById(long submissionId) throws SubmissionNotFoundException {
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
    public List<Submission> findAllSubmissionsByContestJidAndProblemJid(String contestJid, String problemJid) {
        List<SM> submissionModels = submissionDao.findSortedByFilters("id", "asc", "", ImmutableMap.of("contestJid", contestJid, "problemJid", problemJid), 0, -1);
        Map<String, List<GM>> gradingModelsMap = gradingDao.findGradingsForSubmissions(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<Submission> findAllSubmissionsByContestJidBeforeTime(String contestJid, long time) {
        List<SM> submissionModels = submissionDao.findByContestJidSinceTime(contestJid, time);
        Map<String, List<GM>> gradingModelsMap = gradingDao.findGradingsForSubmissions(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
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
    public final String submit(String problemJid, String contestJid, String gradingEngine, String gradingLanguage, Set<String> allowedLanguageNames, GradingSource gradingSource, String userJid, String userIpAddress) throws SubmissionException {
        if (allowedLanguageNames != null && !allowedLanguageNames.contains(gradingLanguage)) {
            throw new SubmissionException("Language " + gradingLanguage + " is not allowed ");
        }

        SM submissionModel = submissionDao.createSubmissionModel();

        submissionModel.problemJid = problemJid;
        submissionModel.contestJid = contestJid;
        submissionModel.gradingEngine = gradingEngine;
        submissionModel.gradingLanguage = gradingLanguage;

        submissionDao.persist(submissionModel, userJid, userIpAddress);

        requestGrading(submissionModel, gradingSource, false, userJid, userIpAddress);

        return submissionModel.jid;
    }

    @Override
    public void regrade(String submissionJid, GradingSource gradingSource, String userJid, String userIpAddress) {
        SM submissionModel = submissionDao.findByJid(submissionJid);

        requestGrading(submissionModel, gradingSource, true, userJid, userIpAddress);
    }

    @Override
    public final void grade(String gradingJid, GradingResult result, String grader, String graderIpAddress) {
        GM gradingModel = gradingDao.findByJid(gradingJid);

        gradingModel.verdictCode = result.getVerdict().getCode();
        gradingModel.verdictName = result.getVerdict().getName();
        gradingModel.score = result.getScore();
        gradingModel.details = result.getDetailsAsJson();

        gradingDao.edit(gradingModel, grader, graderIpAddress);

        afterGrade(gradingJid, result);
    }

    @Override
    public void afterGrade(String gradingJid, GradingResult result) {
        // To be overridden if needed
    }

    @Override
    public boolean gradingExists(String gradingJid) {
        return gradingDao.existsByJid(gradingJid);
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

    private void requestGrading(SM submissionModel, GradingSource gradingSource, boolean isRegrading, String userJid, String userIpAddress) {
        GM gradingModel = gradingDao.createGradingModel();

        gradingModel.submissionJid = submissionModel.jid;
        gradingModel.verdictCode = "?";
        gradingModel.verdictName = "Pending";
        gradingModel.score = 0;

        gradingDao.persist(gradingModel, userJid, userIpAddress);

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
