package org.iatoki.judgels.sandalphon;

import org.iatoki.judgels.commons.FileSystemProvider;
import org.iatoki.judgels.commons.Page;
import play.data.DynamicForm;

import java.io.IOException;
import java.util.List;

public interface BundleSubmissionService {
    BundleSubmission findSubmissionById(long submissionId) throws BundleSubmissionNotFoundException;

    long countSubmissionsByContestJidByUser(String contestJid, String problemJid, String userJid);

    List<BundleSubmission> findAllSubmissionsByContestJid(String contestJid);

    List<BundleSubmission> findAllSubmissionsByContestJidAndProblemJid(String contestJid, String problemJid);

    List<BundleSubmission> findAllSubmissionsByContestJidBeforeTime(String contestJid, long time);

    List<BundleSubmission> findSubmissionsWithoutGradingsByJids(List<String> submissionJids);

    List<BundleSubmission> findSubmissionsWithoutGradingsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String contestJid);

    Page<BundleSubmission> pageSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String contestJid);

    String submit(String problemJid, String contestJid, BundleAnswer answer, String userJid, String userIpAddress);

    void regrade(String submissionJid, BundleAnswer answer, String userJid, String userIpAddress);

    void afterGrade(String gradingJid, BundleAnswer answer);

    void storeSubmissionFiles(FileSystemProvider localFileSystemProvider, FileSystemProvider remoteFileSystemProvider, String submissionJid, BundleAnswer answer);

    BundleAnswer createBundleAnswerFromNewSubmission(DynamicForm data, String languageCode);

    BundleAnswer createBundleAnswerFromPastSubmission(FileSystemProvider localFileSystemProvider, FileSystemProvider remoteFileSystemProvider, String submissionJid) throws IOException;
}
