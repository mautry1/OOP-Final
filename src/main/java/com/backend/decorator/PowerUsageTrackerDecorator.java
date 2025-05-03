package com.backend.decorator;

import com.backend.core.Powerable;

public abstract class PowerUsageTrackerDecorator implements Powerable {
    // PowerUsageTrackerDecorator abstract class
    protected Powerable device;

    public PowerUsageTrackerDecorator(Powerable device) {
        this.device = device;
    }

    public void turnOn() {
        device.turnOn();
    }

    public void turnOff() {
        device.turnOff();
    }

    public boolean isOn() {
        return device.isOn();
    }
}
