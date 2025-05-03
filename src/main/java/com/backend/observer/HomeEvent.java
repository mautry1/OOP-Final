package com.backend.observer;

public class HomeEvent {
    private final String deviceName;
    private final HomeEventType type;

    public HomeEvent(String deviceName, HomeEventType type) {
        this.deviceName = deviceName;
        this.type = type;
    }

    public String sourceDevice() {
        return deviceName;
    }

    public HomeEventType type() {
        return type;
    }

    @Override
    public String toString() {
        return "HomeEvent[type=" + type + ", device=" + deviceName + "]";
    }
}