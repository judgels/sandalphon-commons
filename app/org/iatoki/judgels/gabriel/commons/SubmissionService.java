package org.iatoki.judgels.gabriel.commons;

import org.iatoki.judgels.commons.Page;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.GradingSource;

import java.util.Date;
import java.util.List;

public interface SubmissionService {
    Submission findSubmissionById(long submissionId);

    List<Submission> findAllSubmissionsByContestJid(String contestJid);

    Page<Submission> pageSubmissions(long pageIndex, long pageSize, String orderBy, String orderDir, String authorJid, String problemJid, String contestJid);

    String submit(String problemJid, String contestJid, String gradingEngine, String gradingLanguage, GradingSource gradingSource);

    void regrade(String submissionJid, GradingSource gradingSource);

    void grade(String submissionJid, GradingResult result, String grader, String graderIpAddress);
}
