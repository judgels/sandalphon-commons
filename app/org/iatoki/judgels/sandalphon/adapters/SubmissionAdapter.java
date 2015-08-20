package org.iatoki.judgels.sandalphon.adapters;

import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingSource;
import org.iatoki.judgels.sandalphon.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.ProgrammingSubmissionException;
import play.mvc.Http;
import play.twirl.api.Html;

import java.util.Set;

public interface SubmissionAdapter {

    Set<String> getSupportedGradingEngineNames();

    //TODO add CSRF token
    Html renderViewStatement(String postSubmitUri, String name, String statement, GradingConfig config, String engine, Set<String> allowedGradingLanguageNames, String reasonNotAllowedToSubmit);

    Html renderStatementLanguageSelection(String switchLanguageUri, Set<String> allowedStatementLanguages, String currentStatementLanguage, Html statement);

    Html renderViewSubmission(ProgrammingSubmission submission, GradingSource source, String authorName, String problemAlias, String problemName, String gradingLanguageName, String contestName);

    GradingSource createGradingSourceFromNewSubmission(Http.MultipartFormData body) throws ProgrammingSubmissionException;

    GradingSource createGradingSourceFromPastSubmission(FileSystemProvider localFileSystemProvider, FileSystemProvider remoteFileSystemProvider, String submissionJid);

    GradingRequest createGradingRequest(String gradingJid, String problemJid, String gradingEngine, String gradingLanguage, GradingSource source);

    void storeSubmissionFiles(FileSystemProvider localFileSystemProvider, FileSystemProvider remoteFileSystemProvider, String submissionJid, GradingSource source);
}
