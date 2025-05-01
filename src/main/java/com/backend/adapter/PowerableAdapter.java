package com.backend.adapter;

import com.backend.core.Powerable;
import com.backend.observer.SmartHomeObserver;

import java.lang.reflect.Method;

public class PowerableAdapter implements Powerable {
    private final Object instance;
    private String turnOnMethod = "turnOn";
    private String turnOffMethod = "turnOff";
    private String isOnMethod = "isOn";
    private Boolean powered;

    public PowerableAdapter(String className) {
        try {
            Class<?> powerableClass = Class.forName(className);
            this.instance = powerableClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + className, e);
        }
    }

    public void setTurnOnMethodName(String methodName) {
        this.turnOnMethod = methodName;
    }

    public void setTurnOffMethodName(String methodName) {
        this.turnOffMethod = methodName;
    }

    public void setIsOnMethodName(String methodName) {
        this.isOnMethod = methodName;
    }

    @Override
    public void turnOn() {
        // TODO: use reflection to invoke the method stored in turnOnMethod
        try {
            instance.getClass().getMethod(turnOnMethod).invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method: ", e);
        }
    }

@Override
    public void turnOff() {
        // TODO: same as turnOn but with turnOffMethod
        Method method;
        try {
            instance.getClass().getMethod(turnOffMethod).invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method turnOffMethod: " + turnOffMethod, e);
        }
    }

    @Override
    public boolean isPoweredOn() {
        try {
            powered = (Boolean) instance.getClass().getMethod(isOnMethod).invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method isOnMethod: " + isOnMethod, e);
        }

        return powered;
    }

    public boolean isOn() {
        // TODO: invoke isOnMethod and return the boolean result
        try {
            powered = (Boolean) instance.getClass().getMethod(isOnMethod).invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method isOnMethod: " + isOnMethod, e);
        }

        return powered;
    }

    public Object getDeviceInstance(){
        return this.instance;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void addSmartHomeEventListener(SmartHomeObserver observer) {

    }
}
