package org.iatoki.judgels.sandalphon.adapters.impls;

import com.google.common.collect.Maps;
import org.iatoki.judgels.sandalphon.adapters.GradingConfigAdapter;

import java.util.Map;
import java.util.ServiceLoader;

public final class GradingConfigAdapterRegistry {

    private static GradingConfigAdapterRegistry INSTANCE;

    private final Map<String, GradingConfigAdapter> registry;

    private GradingConfigAdapterRegistry() {
        this.registry = Maps.newHashMap();

        populateAdapters();
    }

    public static GradingConfigAdapterRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GradingConfigAdapterRegistry();
        }
        return INSTANCE;
    }

    public GradingConfigAdapter getByGradingEngineName(String gradingEngineName) {
        return registry.get(gradingEngineName);
    }

    private void populateAdapters() {
        for (GradingConfigAdapter adapter : ServiceLoader.load(GradingConfigAdapter.class)) {
            for (String supportedGradingEngineName : adapter.getSupportedGradingEngineNames()) {
                registry.put(supportedGradingEngineName, adapter);
            }
        }
    }
}
