package org.iatoki.judgels.commons;

import org.iatoki.judgels.gabriel.GradingConfig;
import org.iatoki.judgels.gabriel.GradingRequest;
import play.api.mvc.Call;
import play.mvc.Http;
import play.twirl.api.Html;

public interface SubmissionAdapter {
    Html renderViewStatement(Call postSubmitCall, String statement, GradingConfig config);

    GradingRequest createGradingRequest(GradingConfig config, Http.MultipartFormData body);
}
