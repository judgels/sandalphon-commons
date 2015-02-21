package org.iatoki.judgels.gabriel.commons;

import org.iatoki.judgels.commons.AbstractJidCacheService;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingSource;
import play.mvc.Http;
import play.twirl.api.Html;

import java.io.File;
import java.util.Date;

public interface SubmissionAdapter {

    //TODO: add CSRF token
    Html renderViewStatement(String postSubmitUri, String name, String statement, GradingConfig config, String engine, Date gradingLastUpdateTime);

    Html renderViewSubmission(Submission submission, GradingSource source, String authorName, String problemAlias, String problemName, String gradingLanguageName, String contestName);

    GradingSource createGradingSourceFromNewSubmission(Http.MultipartFormData body);

    GradingSource createGradingSourceFromPastSubmission(File submissionBaseDir, String submissionJid);

    GradingRequest createGradingRequest(String gradingJid, String problemJid, Date gradingLastUpdateTime, String gradingEngine, String gradingLanguage, GradingSource source);

    void storeSubmissionFiles(File submissionBaseDir, String submissionJid, GradingSource source);
}
