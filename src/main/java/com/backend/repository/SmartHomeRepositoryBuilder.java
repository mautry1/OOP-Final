package com.backend.repository;

import com.backend.core.SmartDevice;
import com.backend.devices.SmartLight;
import com.backend.devices.SmartLock;
import com.backend.devices.SmartThermostat;

public class SmartHomeRepositoryBuilder {
    private SmartHomeRepository repository = new SmartHomeRepository();
    private SmartDevice smartLight, smartLock, smartThermostat, securityCamera;

    public SmartHomeRepositoryBuilder addSmartLight(String name) {
        this.smartLight = new SmartLight(name);
        return this;
    }

    public SmartHomeRepositoryBuilder addSmartLock(String name) {
        this.smartLock = new SmartLock(name);
        return this;
    }

    public SmartHomeRepositoryBuilder addSmartThermostat(String name) {
        this.smartThermostat = new SmartThermostat(name);
        return this;
    }

    public SmartHomeRepositoryBuilder addSecurityCamera(String name) {
        this.securityCamera = new SmartLight(name);
        return this;
    }

    public SmartHomeRepository build() {
        if(securityCamera != null) {
            repository.addDevice(securityCamera);
        }

        repository.addDevice(smartLight);
        repository.addDevice(smartLock);
        repository.addDevice(smartThermostat);
        return repository;
    }
}
