package com.backend.factory;

import com.backend.core.SmartDevice;
import com.backend.devices.SecurityCamera;
import com.backend.devices.SmartLight;
import com.backend.devices.SmartLock;
import com.backend.devices.SmartThermostat;

public class SmartDeviceFactory {
    public static SmartDevice createDevice(String var0, String var1) {
        if (var0 != null && var1 != null) {
            switch (var0.toLowerCase()) {
                case "light" -> {
                    return new SmartLight(var1);
                }
                case "thermostat" -> {
                    return new SmartThermostat(var1);
                }
                case "camera" -> {
                    return new SecurityCamera(var1);
                }
                case "lock" -> {
                    return new SmartLock(var1);
                }
                default -> throw new IllegalArgumentException("Unknown device type: " + var0);
            }
        } else {
            throw new IllegalArgumentException("Type and name must not be null.");
        }
    }
}
