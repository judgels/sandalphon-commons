package org.iatoki.judgels.sandalphon.adapters;

import org.iatoki.judgels.gabriel.GradingResponse;

import java.util.Set;

public interface GradingResponseAdapter {

    Set<String> getSupportedGradingResponseNames();

    GradingResponse parseFromJson(String json);
}
