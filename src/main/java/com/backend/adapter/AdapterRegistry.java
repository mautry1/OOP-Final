// File: src/main/java/com/backend/adapter/AdapterRegistry.java
package com.backend.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Discovers and holds all DeviceAdapter implementations via ServiceLoader.
 */
public class AdapterRegistry {
    private final List<DeviceAdapter> adapters = new ArrayList<>();

    public AdapterRegistry() {
        ServiceLoader<DeviceAdapter> loader = ServiceLoader.load(DeviceAdapter.class);
        loader.forEach(adapters::add);
    }

    /**
     * @return an unmodifiable view of all discovered adapters
     */
    public List<DeviceAdapter> getAdapters() {
        return List.copyOf(adapters);
    }

    /**
     * Find an adapter by its simple class name.
     * @param simpleName the adapter's class simple name (e.g. "PowerableAdapter")
     * @return the matching DeviceAdapter
     * @throws IllegalArgumentException if none found
     */
    public DeviceAdapter getAdapterByName(String simpleName) {
        return adapters.stream()
                .filter(a -> a.getClass().getSimpleName().equals(simpleName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No adapter: " + simpleName));
    }
}
