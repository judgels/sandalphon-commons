package org.iatoki.judgels.sandalphon.services;

import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.GradingSource;
import org.iatoki.judgels.sandalphon.Submission;
import org.iatoki.judgels.sandalphon.SubmissionException;
import org.iatoki.judgels.sandalphon.SubmissionNotFoundException;

import java.util.List;
import java.util.Set;

public interface SubmissionService {
    Submission findSubmissionById(long submissionId) throws SubmissionNotFoundException;

    long countSubmissionsByContestJidByUser(String contestJid, String problemJid, String userJid);

    List<Submission> findAllSubmissionsByContestJid(String contestJid);

    List<Submission> findAllSubmissionsByContestJidProblemJidAndUserJid(String contestJid, String problemJid, String userJid);

    List<Submission> findAllSubmissionsByContestJidBeforeTime(String contestJid, long time);

    List<Submission> findSubmissionsWithoutGradingsByJids(List<String> submissionJids);

    List<Submission> findSubmissionsWithoutGradingsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String contestJid);

    Page<Submission> pageSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String contestJid);

    String submit(String problemJid, String contestJid, String gradingEngine, String gradingLanguage, Set<String> allowedLanguageNames, GradingSource gradingSource, String userJid, String userIpAddress) throws SubmissionException;

    void regrade(String submissionJid, GradingSource gradingSource, String userJid, String userIpAddress);

    void grade(String gradingJid, GradingResult result, String grader, String graderIpAddress);

    void afterGrade(String gradingJid, GradingResult result);

    boolean gradingExists(String gradingJid);
}
