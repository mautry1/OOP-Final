package com.backend.controller;

import com.backend.core.Dimmable;
import com.backend.core.MotionSensing;
import com.backend.core.SmartDevice;
import com.backend.core.TemperatureControllable;
import com.backend.observer.HomeEvent;
import com.backend.observer.SmartHomeObserver;
import com.backend.repository.SmartHomeRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeController implements SmartHomeObserver {
    private static HomeController instance;
    private SmartHomeRepository homeRepository;
    private final List<HomeEvent> telemetry;
    private HomeController(SmartHomeRepository homeRepository) {
        this.homeRepository = homeRepository;
        this.telemetry = new ArrayList<>();
    }

    public static HomeController getInstance(SmartHomeRepository repo) {
        if (instance == null) instance = new HomeController(repo);
        return instance;
    }

    public void addDevice(SmartDevice device) {
        homeRepository.addDevice(device);
        device.addSmartHomeEventListener(this);
    }

    public List<HomeEvent> getTelemetry() {
        return telemetry;
    }

    @Override
    public void onSmartHomeEvent(HomeEvent event) {
        telemetry.add(event);
    }

    public List<SmartDevice> getAllDevices() {
        return this.homeRepository.getAllDevices();
    }

    public List<Dimmable> getAllDimmableDevices() {
        List<Dimmable> dimmableDevices = new ArrayList<>();
        for (SmartDevice device : homeRepository.getAllDevices()) {
            if (device instanceof Dimmable) {
                dimmableDevices.add((Dimmable) device);
            }
        }
        
        return dimmableDevices;
    }

    public List<MotionSensing> getAllMotionSensingDevices() {
        List<MotionSensing> motionSensingDevices = new ArrayList<>();
        for (SmartDevice device : homeRepository.getAllDevices()) {
            if (device instanceof MotionSensing) {
                motionSensingDevices.add((MotionSensing) device);
            }
        }
        
        return motionSensingDevices;
    }

    public List<TemperatureControllable> getAllTemperatureControllableDevices() {
        List<TemperatureControllable> temperatureControllableDevices = new ArrayList<>();
        for (SmartDevice device : homeRepository.getAllDevices()) {
            if (device instanceof TemperatureControllable) {
                temperatureControllableDevices.add((TemperatureControllable) device);
            }
        }
        
        return temperatureControllableDevices;
    }
}