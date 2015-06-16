package org.iatoki.judgels.sandalphon.commons;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.iatoki.judgels.commons.FileSystemProvider;
import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.sandalphon.commons.models.daos.interfaces.BaseBundleGradingDao;
import org.iatoki.judgels.sandalphon.commons.models.daos.interfaces.BaseBundleSubmissionDao;
import org.iatoki.judgels.sandalphon.commons.models.domains.AbstractBundleGradingModel;
import org.iatoki.judgels.sandalphon.commons.models.domains.AbstractBundleSubmissionModel;
import play.data.DynamicForm;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class AbstractBundleSubmissionServiceImpl<SM extends AbstractBundleSubmissionModel, GM extends AbstractBundleGradingModel> implements BundleSubmissionService {
    private final BaseBundleSubmissionDao<SM> submissionDao;
    private final BaseBundleGradingDao<GM> gradingDao;
    private final BundleProblemGrader bundleProblemGrader;

    protected AbstractBundleSubmissionServiceImpl(BaseBundleSubmissionDao<SM> submissionDao, BaseBundleGradingDao<GM> gradingDao, BundleProblemGrader bundleProblemGrader) {
        this.submissionDao = submissionDao;
        this.gradingDao = gradingDao;
        this.bundleProblemGrader = bundleProblemGrader;
    }

    @Override
    public BundleSubmission findSubmissionById(long submissionId) throws BundleSubmissionNotFoundException {
        SM submissionModel = submissionDao.findById(submissionId);
        List<GM> gradingModels = gradingDao.findSortedByFilters("id", "asc", "", ImmutableMap.of("submissionJid", submissionModel.jid), 0, -1);

        return createSubmissionFromModels(submissionModel, gradingModels);
    }

    @Override
    public long countSubmissionsByContestJidByUser(String contestJid, String problemJid, String userJid) {
        return submissionDao.countByContestJidAndUserJidAndProblemJid(contestJid, userJid, problemJid);
    }

    @Override
    public List<BundleSubmission> findAllSubmissionsByContestJid(String contestJid) {
        List<SM> submissionModels = submissionDao.findSortedByFilters("id", "asc", "", ImmutableMap.of("contestJid", contestJid), 0, -1);
        Map<String, List<GM>> gradingModelsMap = gradingDao.findGradingsForSubmissions(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<BundleSubmission> findAllSubmissionsByContestJidAndProblemJid(String contestJid, String problemJid) {
        List<SM> submissionModels = submissionDao.findSortedByFilters("id", "asc", "", ImmutableMap.of("contestJid", contestJid, "problemJid", problemJid), 0, -1);
        Map<String, List<GM>> gradingModelsMap = gradingDao.findGradingsForSubmissions(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<BundleSubmission> findAllSubmissionsByContestJidBeforeTime(String contestJid, long time) {
        List<SM> submissionModels = submissionDao.findByContestJidSinceTime(contestJid, time);
        Map<String, List<GM>> gradingModelsMap = gradingDao.findGradingsForSubmissions(Lists.transform(submissionModels, m -> m.jid));

        return Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));
    }

    @Override
    public List<BundleSubmission> findSubmissionsWithoutGradingsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String contestJid) {
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

        List<SM> submissionModels = submissionDao.findSortedByFilters(orderBy, orderDir, "", filterColumns, 0, -1);

        return Lists.transform(submissionModels, m -> createSubmissionFromModel(m));
    }

    @Override
    public List<BundleSubmission> findSubmissionsWithoutGradingsByJids(List<String> submissionJids) {
        List<SM> submissionModels = submissionDao.findByJids(submissionJids);

        return Lists.transform(submissionModels, m -> createSubmissionFromModel(m));
    }

    @Override
    public Page<BundleSubmission> pageSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String contestJid) {
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

        List<BundleSubmission> submissions = Lists.transform(submissionModels, m -> createSubmissionFromModels(m, gradingModelsMap.get(m.jid)));

        return new Page<>(submissions, totalRowsCount, pageIndex, pageSize);
    }

    @Override
    public final String submit(String problemJid, String contestJid, BundleAnswer answer, String userJid, String userIpAddress) {
        SM submissionModel = submissionDao.createSubmissionModel();

        submissionModel.problemJid = problemJid;
        submissionModel.contestJid = contestJid;

        submissionDao.persist(submissionModel, userJid, userIpAddress);

        grade(submissionModel, answer, userJid, userIpAddress);

        return submissionModel.jid;
    }

    @Override
    public void regrade(String submissionJid, BundleAnswer answer, String userJid, String userIpAddress) {
        SM submissionModel = submissionDao.findByJid(submissionJid);

        grade(submissionModel, answer, userJid, userIpAddress);
    }

    @Override
    public void afterGrade(String gradingJid, BundleAnswer answer) {
        // To be overridden if needed
    }

    @Override
    public void storeSubmissionFiles(FileSystemProvider localFileSystemProvider, FileSystemProvider remoteFileSystemProvider, String submissionJid, BundleAnswer answer) {
        List<FileSystemProvider> fileSystemProviders = Lists.newArrayList(localFileSystemProvider);
        if (remoteFileSystemProvider != null) {
            fileSystemProviders.add(remoteFileSystemProvider);
        }

        for (FileSystemProvider fileSystemProvider : fileSystemProviders) {
            try {
                fileSystemProvider.createDirectory(ImmutableList.of(submissionJid));

                fileSystemProvider.writeToFile(ImmutableList.of(submissionJid, "answer.json"), new Gson().toJson(answer));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public BundleAnswer createBundleAnswerFromNewSubmission(DynamicForm data, String languageCode){
        return new BundleAnswer(data.data(), languageCode);
    }

    @Override
    public BundleAnswer createBundleAnswerFromPastSubmission(FileSystemProvider localFileSystemProvider, FileSystemProvider remoteFileSystemProvider, String submissionJid) throws IOException {
        FileSystemProvider fileSystemProvider;

        if (localFileSystemProvider.directoryExists(ImmutableList.of(submissionJid))) {
            fileSystemProvider = localFileSystemProvider;
        } else {
            fileSystemProvider = remoteFileSystemProvider;
        }

        return new Gson().fromJson(fileSystemProvider.readFromFile(ImmutableList.of(submissionJid, "answer.json")), BundleAnswer.class);
    }

    private BundleSubmission createSubmissionFromModel(SM submissionModel) {
        return createSubmissionFromModels(submissionModel, ImmutableList.of());
    }

    private BundleSubmission createSubmissionFromModels(SM submissionModel, List<GM> gradingModels) {
        return new BundleSubmission(submissionModel.id, submissionModel.jid, submissionModel.problemJid, submissionModel.contestJid, submissionModel.userCreate, new Date(submissionModel.timeCreate),
                Lists.transform(gradingModels, m -> createGradingFromModel(m))
        );
    }

    private BundleGrading createGradingFromModel(GM gradingModel) {
        return new BundleGrading(gradingModel.id, gradingModel.jid, gradingModel.score, gradingModel.details);
    }

    private void grade(SM submissionModel, BundleAnswer answer, String userJid, String userIpAddress) {
        try {
            BundleGradingResult bundleGradingResult = bundleProblemGrader.gradeBundleProblem(submissionModel.problemJid, answer);

            if (bundleGradingResult != null) {
                GM gradingModel = gradingDao.createGradingModel();

                gradingModel.submissionJid = submissionModel.jid;
                gradingModel.score = bundleGradingResult.getScore();
                gradingModel.details = bundleGradingResult.getDetailsAsJson();

                gradingDao.persist(gradingModel, userJid, userIpAddress);

                afterGrade(gradingModel.jid, answer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
