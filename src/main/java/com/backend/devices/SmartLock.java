package com.backend.devices;

import com.backend.core.SecureDevice;
import com.backend.observer.HomeEvent;
import com.backend.observer.HomeEventType;
import com.backend.observer.SmartHomeObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SmartLock implements SecureDevice {
    private boolean isLocked;
    private String name;
    private final List<SmartHomeObserver> observers = new ArrayList<>();

    /*public SmartLock() {
        this.isLocked = true;
    }*/
    public SmartLock(String name) {
        this.isLocked = true;
        this.name = name;
    }

    public void lock() {
        this.isLocked = true;
        System.out.println("Smart lock is now locked.");
    }

    public void unlock() {
        this.isLocked = false;
        System.out.println("Smart lock is now unlocked.");
    }

    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public void addSmartHomeEventListener(SmartHomeObserver observer){
        observers.add(observer);
    }

    public void operate() {
        System.out.println("Smart lock is now locked.");
        HomeEvent event = new HomeEvent(HomeEventType.LOCK_OPENED, this, LocalDateTime.now());
        for (SmartHomeObserver observer : observers) {
            observer.onSmartHomeEvent(event);
        }
    }

    public String getName() {
        return name;
    }
}
