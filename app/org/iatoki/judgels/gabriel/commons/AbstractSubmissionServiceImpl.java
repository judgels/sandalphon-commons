package org.iatoki.judgels.gabriel.commons;

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
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        List<GM> gradingModels = gradingDao.findSortedByFilters("id", "desc", "", ImmutableMap.of("submissionJid", submissionModel.jid), 0, -1);

        return createSubmissionFromModels(submissionModel, gradingModels);
    }

    @Override
    public List<Submission> findAllSubmissionsByContestJid(String contestJid) {
        List<SM> submissionModels = submissionDao.findSortedByFilters("id", "asc", "", ImmutableMap.of("contestJid", contestJid), 0, -1);
        Map<String, List<GM>> gradingModelsMap = gradingDao.findGradingsForSubmissions(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
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
    public final String submit(String problemJid, String contestJid, String gradingEngine, String gradingLanguage, Date gradingLastUpdateTime, GradingSource gradingSource) {
        SM submissionModel = submissionDao.createSubmissionModel();

        submissionModel.problemJid = problemJid;
        submissionModel.contestJid = contestJid;
        submissionModel.gradingEngine = gradingEngine;
        submissionModel.gradingLanguage = gradingLanguage;

        submissionDao.persist(submissionModel, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        GM gradingModel = gradingDao.createGradingModel();

        gradingModel.submissionJid = submissionModel.jid;
        gradingModel.verdictCode = "?";
        gradingModel.verdictName = "Pending";
        gradingModel.score = 0;

        gradingDao.persist(gradingModel, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        GradingRequest request = SubmissionAdapters.fromGradingEngine(gradingEngine).createGradingRequest(gradingModel.jid, problemJid, gradingLastUpdateTime, gradingEngine, gradingLanguage, gradingSource);

        try {
            sealtiel.sendMessage(new ClientMessage(gabrielClientJid, request.getClass().getSimpleName(), new Gson().toJson(request)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return submissionModel.jid;
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

    private Submission createSubmissionFromModels(SM submissionModel, List<GM> gradingModels) {
        return new Submission(submissionModel.id, submissionModel.jid, submissionModel.problemJid, submissionModel.contestJid, submissionModel.userCreate, submissionModel.gradingEngine, submissionModel.gradingLanguage, new Date(submissionModel.timeCreate),
                Lists.transform(gradingModels, m -> createGradingFromModel(m))
        );
    }

    private Grading createGradingFromModel(GM gradingModel) {
        return new Grading(gradingModel.id, gradingModel.jid, new Verdict(gradingModel.verdictCode, gradingModel.verdictName), gradingModel.score, gradingModel.details);
    }
}
