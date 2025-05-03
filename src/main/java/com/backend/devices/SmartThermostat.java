package com.backend.devices;

import java.util.ArrayList;
import java.util.List;
import com.backend.core.TemperatureControllable;
import com.backend.observer.SmartHomeObserver;
import com.backend.observer.HomeEvent;
import com.backend.observer.HomeEventType;

/**
 * A simple thermostat that maintains an integer temperature and notifies observers on change.
 */
public class SmartThermostat implements TemperatureControllable {
    private final String deviceName;
    private int temperature = 68;
    private final List<SmartHomeObserver> listeners = new ArrayList<>();

    public SmartThermostat(String deviceName) {
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
    public int getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(int temperature) {
        this.temperature = temperature;
        System.out.println("Smart thermostat " + deviceName + " set to " + temperature + "°F");
        HomeEvent event = new HomeEvent(deviceName, HomeEventType.TEMP_CHANGE);
        listeners.forEach(l -> l.onSmartHomeEvent(event));
    }
}