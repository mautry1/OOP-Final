package com.backend.controller;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import com.backend.adapter.AdapterRegistry;
import com.backend.core.SmartDevice;
import com.backend.core.Powerable;
import com.backend.core.SecureDevice;
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

    public static HomeController getInstance() {
        return getInstance(new SmartHomeRepository());
    }

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

    // Add built-in device
    public void addDevice(SmartDevice device) {
        homeRepository.addDevice(device);
        device.addSmartHomeEventListener(this);
    }

    // Factory-style add
    public void addDevice(String type, String vendor, String id) {
        SmartDevice device;
        switch (type.toLowerCase()) {
            case "light":      device = new SmartLight(id); break;
            case "lock":       device = new SmartLock(id); break;
            case "thermostat": device = new SmartThermostat(id); break;
            case "camera":     device = new SecurityCamera(id); break;
            default: throw new IllegalArgumentException("Unknown device type: " + type);
        }
        addDevice(device);
    }

    // Direct control methods
    public void turnOnDevice(String id) {
        SmartDevice device = findDevice(id);
        if (device instanceof Powerable p) p.turnOn();
        else throw new IllegalArgumentException("Device " + id + " is not powerable");
    }

    public void turnOffDevice(String id) {
        SmartDevice device = findDevice(id);
        if (device instanceof Powerable p) p.turnOff();
        else throw new IllegalArgumentException("Device " + id + " is not powerable");
    }

    // Toggle based on capability
    public void toggleDevice(String id) {
        SmartDevice device = findDevice(id);
        if (device instanceof Powerable p) {
            if (p.isOn()) p.turnOff(); else p.turnOn();
            return;
        }
        if (device instanceof SecureDevice s) {
            s.operate();
            return;
        }
        throw new IllegalArgumentException("Device " + id + " cannot be toggled");
    }

    public List<SmartDevice> listAllDevices() {
        return Collections.unmodifiableList(homeRepository.getAllDevices());
    }

    public List<HomeEvent> getTelemetry() {
        return Collections.unmodifiableList(telemetry);
    }

    // Adapter-based 3rd-party device add
    public void addThirdPartyDevice(String adapterName, String className, String id) {
        AdapterRegistry registry = new AdapterRegistry();
        var prototype = registry.getAdapterByName(adapterName);
        @SuppressWarnings("unchecked")
        Class<? extends SmartDevice> sdClass = (Class<? extends SmartDevice>) prototype.getClass();
        try {
            var ctor = sdClass.getConstructor(String.class, String.class);
            SmartDevice sd = ctor.newInstance(className, id);
            sd.addSmartHomeEventListener(this);
            addDevice(sd);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate adapter " + adapterName, e);
        }
    }

    private SmartDevice findDevice(String id) {
        return homeRepository.getAllDevices().stream()
                .filter(d -> d.getName().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No device with id: " + id));
    }
}