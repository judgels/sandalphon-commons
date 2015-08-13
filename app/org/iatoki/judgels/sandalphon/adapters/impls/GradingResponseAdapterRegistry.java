package org.iatoki.judgels.sandalphon.adapters.impls;

import com.google.common.collect.Maps;
import org.iatoki.judgels.sandalphon.BadGradingResponseException;
import org.iatoki.judgels.sandalphon.adapters.GradingResponseAdapter;

import java.util.Map;
import java.util.ServiceLoader;

public final class GradingResponseAdapterRegistry {

    private static GradingResponseAdapterRegistry INSTANCE;

    private final Map<String, GradingResponseAdapter> registry;

    private GradingResponseAdapterRegistry() {
        this.registry = Maps.newHashMap();

        populateAdapters();
    }

    public static GradingResponseAdapterRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GradingResponseAdapterRegistry();
        }
        return INSTANCE;
    }

    public GradingResponseAdapter getByGradingResponseName(String gradingResponseName) throws BadGradingResponseException {
        if (!registry.containsKey(gradingResponseName)) {
            throw new BadGradingResponseException("Grading response " + gradingResponseName + " unknown");
        }
        return registry.get(gradingResponseName);
    }

    private void populateAdapters() {
        for (GradingResponseAdapter adapter : ServiceLoader.load(GradingResponseAdapter.class)) {
            for (String supportedGradingResponseName : adapter.getSupportedGradingResponseNames()) {
                registry.put(supportedGradingResponseName, adapter);
            }
        }
    }
}
