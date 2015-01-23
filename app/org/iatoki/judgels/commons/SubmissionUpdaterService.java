package org.iatoki.judgels.commons;

import org.iatoki.judgels.gabriel.GradingResult;

public interface SubmissionUpdaterService {
    void updateResult(String submissionJid, GradingResult result);
}
