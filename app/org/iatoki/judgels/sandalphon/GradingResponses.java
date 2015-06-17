package org.iatoki.judgels.sandalphon;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.iatoki.judgels.gabriel.GradingResponse;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingResponse;

public final class GradingResponses {
    private GradingResponses() {
        // prevent instantiation
    }

    public static GradingResponse parseFromJson(String type, String json) throws BadGradingResponseException {
        if (type.equals("BlackBoxGradingResponse")) {
            try {
                return new Gson().fromJson(json, BlackBoxGradingResponse.class);
            } catch (JsonSyntaxException e) {
                throw new BadGradingResponseException("Malformed BlackBoxGradingResponse");
            }
        } else {
            throw new BadGradingResponseException("Grading response type unknown: " + type);
        }
    }
}
