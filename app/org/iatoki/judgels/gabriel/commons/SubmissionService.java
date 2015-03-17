package org.iatoki.judgels.gabriel.commons;

import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.GradingSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SubmissionService {
    Submission findSubmissionById(long submissionId);

    long countSubmissionsByContestJidByUser(String contestJid, String problemJid, String userJid);

    List<Submission> findAllSubmissionsByContestJid(String contestJid);

    List<Submission> findNewSubmissionsByContestJidByUsers(String contestJid, List<String> problemJids, List<String> userJids, long lastTime);

    List<Submission> findSubmissionsWithoutGradingsByJids(List<String> submissionJids);

    List<Submission> findSubmissionsWithoutGradingsByFilters(String orderBy, String orderDir, String authorJid, String problemJid, String contestJid);

    Page<Submission> pageSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String contestJid);

    String submit(String problemJid, String contestJid, String gradingEngine, String gradingLanguage, Set<String> allowedLanguageNames, GradingSource gradingSource);

    void regrade(String submissionJid, GradingSource gradingSource);

    void grade(String gradingJid, GradingResult result, String grader, String graderIpAddress);
}
