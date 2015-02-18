package org.iatoki.judgels.sandalphon.commons;

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

    Html renderViewSubmission(Submission submission, GradingSource source, AbstractJidCacheService<?> jidCacheService);

    GradingSource createGradingSourceFromNewSubmission(Http.MultipartFormData body);

    GradingSource createGradingSourceFromPastSubmission(File submissionBaseDir, String submissionJid);

    GradingRequest createGradingRequest(String submissionJid, String problemJid, Date gradingLastUpdateTime, String gradingEngine, String gradingLanguage, GradingSource source);

    void storeSubmissionFiles(File submissionBaseDir, String submissionJid, GradingSource source);
}
