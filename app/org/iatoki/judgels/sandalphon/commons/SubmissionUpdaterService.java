package org.iatoki.judgels.sandalphon.commons;

import org.iatoki.judgels.gabriel.GradingResult;

public interface SubmissionUpdaterService {
    void updateResult(String submissionJid, GradingResult result, String grader, String graderIpAddress);
}
