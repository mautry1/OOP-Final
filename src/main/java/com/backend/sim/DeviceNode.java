package com.backend.sim;

import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;
import com.backend.core.SmartDevice;
import com.backend.observer.HomeEvent;
import com.backend.observer.SmartHomeObserver;

public abstract class DeviceNode extends Node implements SmartHomeObserver {
    protected final SmartDevice device;
    protected final AssetManager assetManager;

    public DeviceNode(String id, SmartDevice device, AssetManager assetManager) {
        super(id);
        this.device = device;
        this.assetManager = assetManager;
        this.device.addSmartHomeEventListener(this);
        initModel();
    }

    protected abstract void initModel();
    protected abstract void updateVisual(HomeEvent event);

    @Override
    public void onSmartHomeEvent(HomeEvent event) {
        if (event.sourceDevice().equals(device)) {
            updateVisual(event);
        }
    }
}