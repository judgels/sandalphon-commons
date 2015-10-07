package org.iatoki.judgels.sandalphon.services;

import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.sandalphon.BundleAnswer;
import org.iatoki.judgels.sandalphon.BundleSubmission;
import org.iatoki.judgels.sandalphon.BundleSubmissionNotFoundException;
import play.data.DynamicForm;

import java.io.IOException;
import java.util.List;

public interface BundleSubmissionService {

    BundleSubmission findBundleSubmissionById(long submissionId) throws BundleSubmissionNotFoundException;

    BundleSubmission findBundleSubmissionByJid(String submissionJid);

    List<Long> getAllBundleSubmissionsSubmitTime();

    List<BundleSubmission> getAllBundleSubmissions();

    List<BundleSubmission> getBundleSubmissionsWithGradingsByContainerJidAndProblemJidAndUserJid(String containerJid, String problemJid, String userJid);

    List<BundleSubmission> getBundleSubmissionsByJids(List<String> submissionJids);

    List<BundleSubmission> getBundleSubmissionsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String containerJid);

    Page<BundleSubmission> getPageOfBundleSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String containerJid);

    String submit(String problemJid, String containerJid, BundleAnswer answer, String userJid, String userIpAddress);

    void regrade(String submissionJid, BundleAnswer answer, String userJid, String userIpAddress);

    void afterGrade(String gradingJid, BundleAnswer answer);

    void storeSubmissionFiles(FileSystemProvider localFileSystemProvider, FileSystemProvider remoteFileSystemProvider, String submissionJid, BundleAnswer answer);

    BundleAnswer createBundleAnswerFromNewSubmission(DynamicForm data, String languageCode);

    BundleAnswer createBundleAnswerFromPastSubmission(FileSystemProvider localFileSystemProvider, FileSystemProvider remoteFileSystemProvider, String submissionJid) throws IOException;
}
