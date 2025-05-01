package com.backend.repository;

import com.backend.core.SmartDevice;

import java.util.List;
import java.util.ArrayList;

public class SmartHomeRepository {
    private List<SmartDevice> smartDevices;
    
    public SmartHomeRepository() {
        this.smartDevices = new ArrayList<>();
    }

    public void addDevice(SmartDevice device) {
        this.smartDevices.add(device);
    }

    public List<SmartDevice> getAllDevices() {
        return this.smartDevices;
    }
}
