package com.backend.decorator;

import com.backend.core.Powerable;
import com.backend.observer.SmartHomeObserver;

import java.util.ArrayList;
import java.util.List;

public class UsageTrackerPowerableDevice extends PowerUsageTrackerDecorator {
    private long totalUsageTime;
    private long lastTurnOnTime;
    private final List<SmartHomeObserver> observers = new ArrayList<>();

    @Override
    public void addSmartHomeEventListener(SmartHomeObserver observer) {
        observers.add(observer);
    }

    // UsageTrackerPowerableDevice class
    public UsageTrackerPowerableDevice(Powerable device) {
        super(device);
        totalUsageTime = 0;
        lastTurnOnTime = 0;
    }

    @Override
    public void turnOn() {
            lastTurnOnTime = System.currentTimeMillis();
            device.turnOn();
    }

    @Override
    public void turnOff() {
        if (device.isPoweredOn()) {
            long elapsedTime = System.currentTimeMillis() - lastTurnOnTime;
            totalUsageTime += elapsedTime;
            device.turnOff();
        }
    }

    @Override
    public String getName() {
        return device.getName();
    }

    public long getTotalUsageTime() {
        if(device.isPoweredOn()) {
            return totalUsageTime;
        }
        return totalUsageTime;
    }
}
