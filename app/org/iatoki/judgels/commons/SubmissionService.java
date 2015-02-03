package org.iatoki.judgels.commons;

import org.iatoki.judgels.gabriel.GradingSource;

public interface SubmissionService {
    Page<Submission> pageSubmission(long page, long pageSize, String sortBy, String order, String filterString);

    Submission findSubmissionById(long submissionId);

    void submit(String problemJid, String problemGradingType, long problemTimeUpdate, GradingSource source);
}
