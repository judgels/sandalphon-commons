package org.iatoki.judgels.commons;

import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingSource;
import org.iatoki.judgels.gabriel.Verdict;
import play.api.mvc.Call;
import play.mvc.Http;
import play.twirl.api.Html;

public interface SubmissionAdapter {
    Html renderViewStatement(Call postSubmitCall, String statement, GradingConfig config);

    Html renderViewSubmission(long submissionId, Verdict verdict, int score, String detailsAsJson, GradingConfig config);

    GradingSource createGradingSource(GradingConfig config, Http.MultipartFormData body);

    GradingRequest createGradingRequest(String submissionJid, String problemJid, long problemLastUpdate, String gradingType, GradingSource source);
}
