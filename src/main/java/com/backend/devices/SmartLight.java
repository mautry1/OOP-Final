package com.backend.devices;

import java.util.List;
import java.util.ArrayList;
import com.backend.core.Powerable;
import com.backend.core.SmartDevice;
import com.backend.observer.HomeEvent;
import com.backend.observer.HomeEventType;
import com.backend.observer.SmartHomeObserver;

public class SmartLight implements SmartDevice, Powerable {
    private final String deviceName;
    private boolean poweredOn = false;
    private final List<SmartHomeObserver> listeners = new ArrayList<>();

    public SmartLight(String deviceName) {
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
    public void turnOn() {
        if (!poweredOn) {
            poweredOn = true;
            System.out.println("Smart light " + deviceName + " is now ON");
            HomeEvent event = new HomeEvent(deviceName, HomeEventType.LIGHT_ON);
            listeners.forEach(l -> l.onSmartHomeEvent(event));
        }
    }

    @Override
    public void turnOff() {
        if (poweredOn) {
            poweredOn = false;
            System.out.println("Smart light " + deviceName + " is now OFF");
            HomeEvent event = new HomeEvent(deviceName, HomeEventType.LIGHT_OFF);
            listeners.forEach(l -> l.onSmartHomeEvent(event));
        }
    }

    @Override
    public boolean isOn() {
        return poweredOn;
    }
}