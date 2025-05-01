package com.backend.core;

public interface Powerable extends SmartDevice {
    void turnOn();
    void turnOff();
    boolean isPoweredOn();
}