package com.backend.devices;

import java.util.List;
import java.util.ArrayList;
import com.backend.core.SecureDevice;
import com.backend.core.SmartDevice;
import com.backend.observer.HomeEvent;
import com.backend.observer.HomeEventType;
import com.backend.observer.SmartHomeObserver;

public class SmartLock implements SmartDevice, SecureDevice {
    private final String deviceName;
    private boolean locked = false;
    private final List<SmartHomeObserver> listeners = new ArrayList<>();

    public SmartLock(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String getName() {
        return deviceName;
    }

    @Override
    public void addSmartHomeEventListener(SmartHomeObserver observer) {
        listeners.add(observer);
    }

    @Override
    public void operate() {
        locked = !locked;
        String state = locked ? "LOCKED" : "UNSECURED";
        System.out.println("Smart lock " + deviceName + " is now " + (locked ? "LOCKED" : "UNSECURED"));
        HomeEventType type = locked ? HomeEventType.LOCK_SECURED : HomeEventType.LOCK_UNSECURED;
        HomeEvent event = new HomeEvent(deviceName, type);
        listeners.forEach(l -> l.onSmartHomeEvent(event));
    }
}