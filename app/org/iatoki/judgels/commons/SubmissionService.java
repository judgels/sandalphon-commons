package org.iatoki.judgels.commons;

import org.iatoki.judgels.gabriel.GradingSource;

public interface SubmissionService {
    Page<Submission> pageSubmission(long page, long pageSize, String sortBy, String order, String filterString);

    Submission findSubmissionById(long submissionId);

    String submit(String problemJid, String problemGradingType, String gradingLanguage, long problemTimeUpdate, GradingSource source);
}
