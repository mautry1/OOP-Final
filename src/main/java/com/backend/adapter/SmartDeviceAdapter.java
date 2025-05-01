package com.backend.adapter;

import com.backend.core.SmartDevice;
import com.backend.observer.SmartHomeObserver;

import java.lang.reflect.InvocationTargetException;

public class SmartDeviceAdapter implements SmartDevice, DeviceAdapter {
    private final Object instance;
    private String getNameMethod = "getName";
    private String setNameMethod = "setName";
    private String name = "default";

    public SmartDeviceAdapter(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            this.instance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate: " + className, e);
        }
    }

    // Example: adapter.setGetNameMethodName("fetchDeviceName");
    public void setGetNameMethodName(String methodName) {
        this.getNameMethod = methodName;
    }

    public void setSetNameMethodName(String methodName) {
        this.setNameMethod = methodName;
    }

    @Override
    public String getName() {
        // TODO: use reflection to invoke the method named by getNameMethod
        try {
            name = (String) instance.getClass().getMethod(getNameMethod).invoke(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return name;
    }

    @Override
    public void addSmartHomeEventListener(SmartHomeObserver observer) {}

    public void setName(String name) {
        // TODO: use reflection to invoke the method named by setNameMethod with the given name
        // Hint: getMethod(..., String.class)
        try {
            name = (String) instance.getClass().getMethod(setNameMethod, String.class).invoke(instance, name);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public Object getDeviceInstance(){ return instance;}
}
