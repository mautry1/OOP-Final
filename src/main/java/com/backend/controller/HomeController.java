package com.backend.controller;

import com.backend.core.SmartDevice;
import com.backend.core.Powerable;
import com.backend.core.SecureDevice;
import com.backend.core.TemperatureControllable;
import com.backend.observer.HomeEvent;
import com.backend.observer.SmartHomeObserver;
import com.backend.repository.SmartHomeRepository;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class HomeController implements SmartHomeObserver {
    private static HomeController instance;
    private final SmartHomeRepository homeRepository;
    private final List<HomeEvent> telemetry = new ArrayList<>();

    private HomeController(SmartHomeRepository repo) {
        this.homeRepository = repo;
    }

    /** No‐arg singleton entrypoint */
    public static HomeController getInstance() {
        return getInstance(new SmartHomeRepository());
    }

    /** Internal singleton init */
    public static HomeController getInstance(SmartHomeRepository repo) {
        if (instance == null) {
            instance = new HomeController(repo);
        }
        return instance;
    }

    @Override
    public void onSmartHomeEvent(HomeEvent event) {
        telemetry.add(event);
    }

    /** Add a device instance */
    public void addDevice(SmartDevice device) {
        homeRepository.addDevice(device);
        device.addSmartHomeEventListener(this);
    }

    /** Factory‐style add (type,vendor,id) */
    public void addDevice(String type, String vendor, String id) {
        SmartDevice device;
        switch (type.toLowerCase()) {
            case "light":      device = new com.backend.devices.SmartLight(id);      break;
            case "lock":       device = new com.backend.devices.SmartLock(id);       break;
            case "thermostat": device = new com.backend.devices.SmartThermostat(id); break;
            case "camera":     device = new com.backend.devices.SecurityCamera(id);  break;
            default: throw new IllegalArgumentException("Unknown device type: " + type);
        }
        addDevice(device);
    }

    /** Toggle logic for powerable/secure */
    public void toggleDevice(String id) {
        SmartDevice d = findDevice(id);
        if (d instanceof Powerable p) {
            if (p.isOn()) p.turnOff(); else p.turnOn();
        } else if (d instanceof SecureDevice s) {
            s.operate();
        }
    }

    public List<SmartDevice> listAllDevices() {
        return Collections.unmodifiableList(homeRepository.getAllDevices());
    }

    public List<HomeEvent> getTelemetry() {
        return Collections.unmodifiableList(telemetry);
    }

    private SmartDevice findDevice(String id) {
        return homeRepository.getAllDevices().stream()
                .filter(d -> d.getName().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No device with id: " + id));
    }
}
