package com.backend.controller;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import com.backend.adapter.AdapterRegistry;
import com.backend.adapter.DeviceAdapter;
import com.backend.adapter.SmartDeviceAdapter;
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

    // --- Singleton accessors ---
    public static HomeController getInstance(SmartHomeRepository repo) {
        if (instance == null) {
            instance = new HomeController(repo);
        }
        return instance;
    }

    public static HomeController getInstance() {
        return getInstance(new SmartHomeRepository());
    }

    @Override
    public void onSmartHomeEvent(HomeEvent event) {
        telemetry.add(event);
    }

    // --- Core device operations ---
    public void addDevice(SmartDevice device) {
        homeRepository.addDevice(device);
        device.addSmartHomeEventListener(this);
    }

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

    public void turnOnDevice(String id) {
        SmartDevice device = findDevice(id);
        if (device instanceof Powerable p) {
            p.turnOn();
        } else {
            throw new IllegalArgumentException("Device " + id + " is not powerable");
        }
    }

    public void turnOffDevice(String id) {
        SmartDevice device = findDevice(id);
        if (device instanceof Powerable p) {
            p.turnOff();
        } else {
            throw new IllegalArgumentException("Device " + id + " is not powerable");
        }
    }

    public List<SmartDevice> listAllDevices() {
        return Collections.unmodifiableList(homeRepository.getAllDevices());
    }

    public List<HomeEvent> getTelemetry() {
        return Collections.unmodifiableList(telemetry);
    }

    // --- Third-party adapter support ---
    /**
     * Adds a third-party device via specified adapter.
     *
     * @param adapterName simple class name of the DeviceAdapter
     * @param className   fully-qualified class name of third-party device
     * @param id          desired device ID in the smart home
     */
    public void addThirdPartyDevice(String adapterName, String className, String id) {
        AdapterRegistry registry = new AdapterRegistry();
        DeviceAdapter prototype = registry.getAdapterByName(adapterName);
        Class<? extends DeviceAdapter> adapterClass = prototype.getClass();

        try {
            Constructor<? extends DeviceAdapter> ctor = adapterClass.getConstructor(String.class);
            DeviceAdapter adapter = ctor.newInstance(className);

            if (adapter instanceof SmartDevice) {
                SmartDevice sd = (SmartDevice) adapter;
                sd.addSmartHomeEventListener(this);
                if (sd instanceof SmartDeviceAdapter) {
                    ((SmartDeviceAdapter) sd).setName(id);
                }
                addDevice(sd);
            } else {
                throw new IllegalArgumentException("Adapter " + adapterName + " does not implement SmartDevice");
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate adapter " + adapterName, e);
        }
    }

    // --- Helper ---
    private SmartDevice findDevice(String id) {
        return homeRepository.getAllDevices().stream()
                .filter(d -> d.getName().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No device with id: " + id));
    }
}
