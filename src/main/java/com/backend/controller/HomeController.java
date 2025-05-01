// File: src/main/java/com/backend/controller/HomeController.java
package com.backend.controller;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.backend.core.SmartDevice;
import com.backend.core.Powerable;
import com.backend.observer.HomeEvent;
import com.backend.observer.SmartHomeObserver;
import com.backend.repository.SmartHomeRepository;
import com.backend.devices.SmartLight;
import com.backend.devices.SmartLock;
import com.backend.devices.SmartThermostat;
import com.backend.devices.SecurityCamera;

public class HomeController implements SmartHomeObserver {
    private static HomeController instance;
    private final SmartHomeRepository homeRepository;
    private final List<HomeEvent> telemetry = new ArrayList<>();

    private HomeController(SmartHomeRepository repo) {
        this.homeRepository = repo;
    }

    /**
     * Initialize or retrieve singleton with custom repository.
     */
    public static HomeController getInstance(SmartHomeRepository repo) {
        if (instance == null) {
            instance = new HomeController(repo);
        }
        return instance;
    }

    /**
     * Initialize or retrieve singleton with default (empty) repository.
     */
    public static HomeController getInstance() {
        return getInstance(new SmartHomeRepository());
    }

    @Override
    public void onSmartHomeEvent(HomeEvent event) {
        telemetry.add(event);
    }

    /**
     * Add a device instance and register for events.
     */
    public void addDevice(SmartDevice device) {
        homeRepository.addDevice(device);
        device.addSmartHomeEventListener(this);
    }

    /**
     * Convenience overload: build device by type string.
     */
    public void addDevice(String type, String vendor, String id) {
        SmartDevice device;
        switch (type.toLowerCase()) {
            case "light":
                device = new SmartLight(id);
                break;
            case "lock":
                device = new SmartLock(id);
                break;
            case "thermostat":
                device = new SmartThermostat(id);
                break;
            case "camera":
                device = new SecurityCamera(id);
                break;
            default:
                throw new IllegalArgumentException("Unknown device type: " + type);
        }
        addDevice(device);
    }

    /**
     * Find a registered device by its identifier.
     */
    private SmartDevice findDevice(String id) {
        return homeRepository.getAllDevices().stream()
                .filter(d -> d.getName().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No device with id: " + id));
    }

    /**
     * Turn on a powerable device by id.
     */
    public void turnOnDevice(String id) {
        SmartDevice device = findDevice(id);
        if (device instanceof Powerable p) {
            p.turnOn();
        } else {
            throw new IllegalArgumentException("Device " + id + " is not powerable");
        }
    }

    /**
     * Turn off a powerable device by id.
     */
    public void turnOffDevice(String id) {
        SmartDevice device = findDevice(id);
        if (device instanceof Powerable p) {
            p.turnOff();
        } else {
            throw new IllegalArgumentException("Device " + id + " is not powerable");
        }
    }

    /**
     * List all registered devices.
     */
    public List<SmartDevice> listAllDevices() {
        return Collections.unmodifiableList(homeRepository.getAllDevices());
    }

    /**
     * Retrieve collected telemetry events.
     */
    public List<HomeEvent> getTelemetry() {
        return Collections.unmodifiableList(telemetry);
    }
}