package org.iatoki.judgels.sandalphon.adapters.impls;

import com.google.common.collect.Maps;
import org.iatoki.judgels.sandalphon.adapters.SubmissionAdapter;

import java.util.Map;
import java.util.ServiceLoader;

public final class SubmissionAdapterRegistry {

    private static SubmissionAdapterRegistry INSTANCE;

    private final Map<String, SubmissionAdapter> registry;

    private SubmissionAdapterRegistry() {
        this.registry = Maps.newHashMap();

        populateAdapters();
    }

    public static SubmissionAdapterRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SubmissionAdapterRegistry();
        }
        return INSTANCE;
    }

    public SubmissionAdapter getByGradingEngineName(String gradingEngineName) {
        return registry.get(gradingEngineName);
    }

    private void populateAdapters() {
        for (SubmissionAdapter adapter : ServiceLoader.load(SubmissionAdapter.class)) {
            for (String supportedGradingEngineName : adapter.getSupportedGradingEngineNames()) {
                registry.put(supportedGradingEngineName, adapter);
            }
        }
    }
}
