package com.backend.devices;

import com.backend.core.Dimmable;
import com.backend.core.MotionSensing;
import com.backend.core.Powerable;
import com.backend.core.SmartDevice;
import com.backend.observer.HomeEvent;
import com.backend.observer.HomeEventType;
import com.backend.observer.SmartHomeObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SmartLight implements Dimmable, MotionSensing, Powerable, SmartDevice {
    public int brightnessLevel;
    public boolean poweredOn;
    private String name;
    private final List<SmartHomeObserver> observers = new ArrayList<>();

    /*public SmartLight() {
        this.name = "default";
        this.brightnessLevel = 0;
        this.poweredOn = false;
    }*/

    public SmartLight(String name) {
        this.name = name;
        this.brightnessLevel = 0;
        this.poweredOn = false;
    }

    public int getBrightnessLevel() {
        return this.brightnessLevel;
    }

    public void setBrightnessLevel(int level) {
        this.brightnessLevel = level;
    }

    public void turnOn() {
        this.poweredOn = true;
        HomeEvent event = new HomeEvent(HomeEventType.LIGHT_ON, this, LocalDateTime.now());
        for (SmartHomeObserver observer : observers) {
            observer.onSmartHomeEvent(event);
        }
    }

    public void turnOff() {
        this.poweredOn = false;
    }

    public boolean isPoweredOn() {
        if(poweredOn) {
            return true;
        } else {
            return false;
        }
    }

    public void detectMotion() {
        this.brightnessLevel = 100;
    }

    public void stopDetectingMotion() {
        this.brightnessLevel = 0;
    }

    public boolean isDetectingMotion() {
        if(brightnessLevel == 100) {
            return true;
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void addSmartHomeEventListener(SmartHomeObserver observer) {
        observers.add(observer);
    }
}
