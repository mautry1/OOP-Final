package com.backend.devices;

import com.backend.core.Powerable;
import com.backend.core.TemperatureControllable;
import com.backend.observer.SmartHomeObserver;

import java.util.ArrayList;
import java.util.List;

public class SmartThermostat implements Powerable, TemperatureControllable {
    private Boolean poweredOn;
    private int temperature;
    private String deviceName;
    private final List<SmartHomeObserver> observers = new ArrayList<>();

    @Override
    public void addSmartHomeEventListener(SmartHomeObserver observer) {
        observers.add(observer);
    }

    public SmartThermostat() {
        this.poweredOn = true;
        this.temperature = 68; // default temperature
    }

    public SmartThermostat(String deviceName) {
        this.poweredOn = true;
        this.temperature = 68; // default temperature
        this.deviceName = deviceName;
    }

    public void turnOn() {
        this.poweredOn = true;
        System.out.println("Smart thermostat is powered on.");
    }

    public void turnOff() {
        this.poweredOn = false;
        System.out.println("Smart thermostat is powered off.");
    }

    public boolean isPoweredOn() {
        return poweredOn;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
        System.out.println("Smart thermostat temperature set to " + temperature + "°F.");
    }

    public int getTemperature() {
        return temperature;
    }

    public String getName() {
        return deviceName;
    }
}