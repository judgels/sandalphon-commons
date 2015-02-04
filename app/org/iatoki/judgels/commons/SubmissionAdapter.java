package org.iatoki.judgels.commons;

import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingSource;
import play.api.mvc.Call;
import play.mvc.Http;
import play.twirl.api.Html;

import java.io.File;

public interface SubmissionAdapter {
    Html renderViewStatement(Call postSubmitCall, String name, String statement, GradingConfig config);

    Html renderViewSubmission(Submission submission, GradingSource source);

    GradingSource createGradingSourceFromNewSubmission(GradingConfig config, Http.MultipartFormData body);

    GradingSource createGradingSourceFromPastSubmission(GradingConfig config, File submissionBaseDir, String submissionJid);

    GradingRequest createGradingRequest(String submissionJid, String problemJid, long problemLastUpdate, String gradingEngine, String gradingLanguage, GradingSource source);

    void storeSubmissionFiles(File submissionBaseDir, String submissionJid, GradingSource source);
}
