package org.iatoki.judgels.commons;

import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingSource;
import play.mvc.Http;
import play.twirl.api.Html;

import java.io.File;

public interface SubmissionAdapter {

    //TODO: add CSRF token
    Html renderViewStatement(String postSubmitUri, String name, String statement, GradingConfig config, String engine, long gradingLastUpdateTime);

    Html renderViewSubmission(Submission submission, GradingSource source);

    GradingSource createGradingSourceFromNewSubmission(Http.MultipartFormData body);

    GradingSource createGradingSourceFromPastSubmission(File submissionBaseDir, String submissionJid);

    GradingRequest createGradingRequest(String submissionJid, String problemJid, long problemLastUpdate, String gradingEngine, String gradingLanguage, GradingSource source);

    void storeSubmissionFiles(File submissionBaseDir, String submissionJid, GradingSource source);
}
