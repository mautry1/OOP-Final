package com.backend.adapter;

import com.backend.core.SmartDevice;
import com.backend.observer.SmartHomeObserver;

/**
 * Adapter enabling any class with a String constructor or no-arg constructor
 * plus SmartDevice methods to be treated as a SmartDevice.
 */
public class SmartDeviceAdapter implements SmartDevice, DeviceAdapter {
    private final Object instance;
    private final String deviceId;

    /**
     * Prototype constructor for ServiceLoader discovery.
     */
    public SmartDeviceAdapter() {
        this.instance = null;
        this.deviceId = null;
    }

    /**
     * Create and wrap the target class by name, using either a no-arg constructor or
     * a single-String constructor for the desired device ID.
     * @param className fully-qualified class name of the target device
     * @param id desired identifier within the smart home
     */
    public SmartDeviceAdapter(String className, String id) {
        Object tmp;
        try {
            Class<?> clazz = Class.forName(className);
            try {
                // try no-arg
                tmp = clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                // fallback to (String) ctor
                tmp = clazz.getDeclaredConstructor(String.class).newInstance(id);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not instantiate: " + className, e);
        }
        this.instance = tmp;
        this.deviceId = id;
    }

    @Override
    public String getName() {
        return deviceId;
    }

    @Override
    public void addSmartHomeEventListener(SmartHomeObserver observer) {
        try {
            var method = instance.getClass()
                    .getMethod("addSmartHomeEventListener", SmartHomeObserver.class);
            method.invoke(instance, observer);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register observer on " + deviceId, e);
        }
    }

    /**
     * @return the raw underlying device instance
     */
    @Override
    public Object getDeviceInstance() {
        return instance;
    }
}
