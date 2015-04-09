package org.iatoki.judgels.gabriel.commons;

import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingSource;
import play.mvc.Http;
import play.twirl.api.Html;

import java.io.File;
import java.util.Set;

public interface SubmissionAdapter {

    //TODO: add CSRF token
    Html renderViewStatement(String postSubmitUri, String name, String statement, GradingConfig config, String engine, Set<String> allowedGradingLanguageNames, boolean isAllowedToSubmit);

    Html renderStatementLanguageSelection(String switchLanguageUri, Set<String> allowedStatementLanguages, String currentStatementLanguage, Html statement);

    Html renderViewSubmission(Submission submission, GradingSource source, String authorName, String problemAlias, String problemName, String gradingLanguageName, String contestName);

    GradingSource createGradingSourceFromNewSubmission(Http.MultipartFormData body);

    GradingSource createGradingSourceFromPastSubmission(File submissionBaseDir, String submissionJid);

    GradingRequest createGradingRequest(String gradingJid, String problemJid, String gradingEngine, String gradingLanguage, GradingSource source);

    void storeSubmissionFiles(File submissionBaseDir, String submissionJid, GradingSource source);
}
