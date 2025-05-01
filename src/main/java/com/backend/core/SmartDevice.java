package com.backend.core;

import com.backend.observer.SmartHomeObserver;

public interface SmartDevice {
    public String getName();

    void addSmartHomeEventListener(SmartHomeObserver observer);
}
