package org.iatoki.judgels.sandalphon.services.impls;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.sealtiel.SealtielClientAPI;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.SubmissionSource;
import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.sandalphon.Grading;
import org.iatoki.judgels.sandalphon.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.ProgrammingSubmissionException;
import org.iatoki.judgels.sandalphon.ProgrammingSubmissionNotFoundException;
import org.iatoki.judgels.sandalphon.models.daos.BaseProgrammingGradingDao;
import org.iatoki.judgels.sandalphon.models.daos.BaseProgrammingSubmissionDao;
import org.iatoki.judgels.sandalphon.models.entities.AbstractProgrammingGradingModel;
import org.iatoki.judgels.sandalphon.models.entities.AbstractProgrammingGradingModel_;
import org.iatoki.judgels.sandalphon.models.entities.AbstractProgrammingSubmissionModel;
import org.iatoki.judgels.sandalphon.models.entities.AbstractProgrammingSubmissionModel_;
import org.iatoki.judgels.sandalphon.services.ProgrammingSubmissionService;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractProgrammingSubmissionServiceImpl<SM extends AbstractProgrammingSubmissionModel, GM extends AbstractProgrammingGradingModel> implements ProgrammingSubmissionService {

    private final BaseProgrammingSubmissionDao<SM> submissionDao;
    private final BaseProgrammingGradingDao<GM> gradingDao;
    private final SealtielClientAPI sealtielClientAPI;
    private final String gabrielClientJid;

    protected AbstractProgrammingSubmissionServiceImpl(BaseProgrammingSubmissionDao<SM> submissionDao, BaseProgrammingGradingDao<GM> gradingDao, SealtielClientAPI sealtielClientAPI, String gabrielClientJid) {
        this.submissionDao = submissionDao;
        this.gradingDao = gradingDao;
        this.sealtielClientAPI = sealtielClientAPI;
        this.gabrielClientJid = gabrielClientJid;
    }

    @Override
    public ProgrammingSubmission findProgrammingSubmissionById(long programmingSubmissionId) throws ProgrammingSubmissionNotFoundException {
        SM submissionModel = submissionDao.findById(programmingSubmissionId);
        List<GM> gradingModels = gradingDao.findSortedByFilters("id", "asc", "", ImmutableMap.of(AbstractProgrammingGradingModel_.submissionJid, submissionModel.jid), ImmutableMap.of(), 0, -1);

        return createSubmissionFromModels(submissionModel, gradingModels);
    }

    @Override
    public long countProgrammingSubmissionsByUserJid(String containerJid, String problemJid, String userJid) {
        return submissionDao.countByContainerJidAndUserJidAndProblemJid(containerJid, userJid, problemJid);
    }

    @Override
    public List<ProgrammingSubmission> getProgrammingSubmissionsWithGradingsByContainerJid(String containerJid) {
        List<SM> submissionModels = submissionDao.findSortedByFilters("id", "asc", "", ImmutableMap.of(AbstractProgrammingSubmissionModel_.containerJid, containerJid), ImmutableMap.of(), 0, -1);
        Map<String, List<GM>> gradingModelsMap = gradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<ProgrammingSubmission> getProgrammingSubmissionsWithGradingsByContainerJidAndProblemJidAndUserJid(String containerJid, String problemJid, String userJid) {
        List<SM> submissionModels = submissionDao.findSortedByFilters("id", "asc", "", ImmutableMap.<SingularAttribute<? super SM, String>, String>of(AbstractProgrammingSubmissionModel_.containerJid, containerJid, AbstractProgrammingSubmissionModel_.problemJid, problemJid, AbstractProgrammingSubmissionModel_.userCreate, userJid), ImmutableMap.of(), 0, -1);
        Map<String, List<GM>> gradingModelsMap = gradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<ProgrammingSubmission> getProgrammingSubmissionsWithGradingsByContainerJidBeforeTime(String containerJid, long time) {
        List<SM> submissionModels = submissionDao.getByContainerJidSinceTime(containerJid, time);
        Map<String, List<GM>> gradingModelsMap = gradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<ProgrammingSubmission> getProgrammingSubmissionsByJids(List<String> programmingSubmissionJids) {
        List<SM> submissionModels = submissionDao.getByJids(programmingSubmissionJids);

        return Lists.transform(submissionModels, m -> createSubmissionFromModel(m));
    }

    @Override
    public List<ProgrammingSubmission> getProgrammingSubmissionsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
        ImmutableMap.Builder<SingularAttribute<? super SM, String>, String> filterColumnsBuilder = ImmutableMap.builder();
        if (authorJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.userCreate, authorJid);
        }
        if (problemJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.problemJid, problemJid);
        }
        if (containerJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.containerJid, containerJid);
        }

        Map<SingularAttribute<? super SM, String>, String> filterColumns = filterColumnsBuilder.build();

        long totalRowsCount = submissionDao.countByFilters("", filterColumns, ImmutableMap.of());
        List<SM> submissionModels = submissionDao.findSortedByFilters(orderBy, orderDir, "", filterColumns, ImmutableMap.of(), 0, -1);

        return Lists.transform(submissionModels, m -> createSubmissionFromModel(m));
    }

    @Override
    public Page<ProgrammingSubmission> getPageOfProgrammingSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String containerJid) {
        ImmutableMap.Builder<SingularAttribute<? super SM, String>, String> filterColumnsBuilder = ImmutableMap.builder();
        if (authorJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.userCreate, authorJid);
        }
        if (problemJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.problemJid, problemJid);
        }
        if (containerJid != null) {
            filterColumnsBuilder.put(AbstractProgrammingSubmissionModel_.containerJid, containerJid);
        }

        Map<SingularAttribute<? super SM, String>, String> filterColumns = filterColumnsBuilder.build();

        long totalRowsCount = submissionDao.countByFiltersEq("", filterColumns);
        List<SM> submissionModels = submissionDao.findSortedByFiltersEq(orderBy, orderDir, "", filterColumns, pageIndex * pageSize, pageSize);
        Map<String, List<GM>> gradingModelsMap = gradingDao.getBySubmissionJids(Lists.transform(submissionModels, m -> m.jid));

        List<ProgrammingSubmission> submissions = Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));

        return new Page<>(submissions, totalRowsCount, pageIndex, pageSize);
    }

    @Override
    public final String submit(String problemJid, String containerJid, String gradingEngine, String gradingLanguage, Set<String> allowedLanguageNames, SubmissionSource submissionSource, String userJid, String userIpAddress) throws ProgrammingSubmissionException {
        if (allowedLanguageNames != null && !allowedLanguageNames.contains(gradingLanguage)) {
            throw new ProgrammingSubmissionException("Language " + gradingLanguage + " is not allowed ");
        }

        SM submissionModel = submissionDao.createSubmissionModel();

        submissionModel.problemJid = problemJid;
        submissionModel.containerJid = containerJid;
        submissionModel.gradingEngine = gradingEngine;
        submissionModel.gradingLanguage = gradingLanguage;

        submissionDao.persist(submissionModel, userJid, userIpAddress);

        requestGrading(submissionModel, submissionSource, false, userJid, userIpAddress);

        return submissionModel.jid;
    }

    @Override
    public void regrade(String submissionJid, SubmissionSource submissionSource, String userJid, String userIpAddress) {
        SM submissionModel = submissionDao.findByJid(submissionJid);

        requestGrading(submissionModel, submissionSource, true, userJid, userIpAddress);
    }

    @Override
    public final void grade(String gradingJid, GradingResult result, String grader, String graderIpAddress) {
        GM gradingModel = gradingDao.findByJid(gradingJid);

        gradingModel.verdictCode = result.getVerdict().getCode();
        gradingModel.verdictName = result.getVerdict().getName();
        gradingModel.score = result.getScore();
        gradingModel.details = result.getDetails();

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

    private ProgrammingSubmission createSubmissionFromModel(SM submissionModel) {
        return createSubmissionFromModels(submissionModel, ImmutableList.of());
    }

    private ProgrammingSubmission createSubmissionFromModels(SM submissionModel, List<GM> gradingModels) {
        return new ProgrammingSubmission(submissionModel.id, submissionModel.jid, submissionModel.problemJid, submissionModel.containerJid, submissionModel.userCreate, submissionModel.gradingEngine, submissionModel.gradingLanguage, new Date(submissionModel.timeCreate),
                Lists.transform(gradingModels, m -> createGradingFromModel(m))
        );
    }

    private Grading createGradingFromModel(GM gradingModel) {
        return new Grading(gradingModel.id, gradingModel.jid, new Verdict(gradingModel.verdictCode, gradingModel.verdictName), gradingModel.score, gradingModel.details);
    }

    private void requestGrading(SM submissionModel, SubmissionSource submissionSource, boolean isRegrading, String userJid, String userIpAddress) {
        GM gradingModel = gradingDao.createGradingModel();

        gradingModel.submissionJid = submissionModel.jid;
        gradingModel.verdictCode = "?";
        gradingModel.verdictName = "Pending";
        gradingModel.score = 0;

        gradingDao.persist(gradingModel, userJid, userIpAddress);

        GradingRequest request = new GradingRequest(gradingModel.jid, submissionModel.problemJid, submissionModel.gradingEngine, submissionModel.gradingLanguage, submissionSource);


        try {
            if (isRegrading) {
                sealtielClientAPI.sendLowPriorityMessage(gabrielClientJid, request.getClass().getSimpleName(), new Gson().toJson(request));
            } else {
                sealtielClientAPI.sendMessage(gabrielClientJid, request.getClass().getSimpleName(), new Gson().toJson(request));
            }
        } catch (JudgelsAPIClientException e) {
            // log later
        }
    }
}
