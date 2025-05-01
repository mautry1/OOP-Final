package com.backend.adapter;

import com.backend.core.SecureDevice;
import com.backend.observer.SmartHomeObserver;

public class SecureDeviceAdapter implements SecureDevice, DeviceAdapter {
    public SecureDeviceAdapter() {
        this.instance = null;
    }

    private final Object instance;
    private String operateMethod = "operate";

    public SecureDeviceAdapter(String className) {
        try {
            Class<?> secureClass = Class.forName(className);
            this.instance = secureClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create class: ", e);
        }
    }

    public void setOperateMethodName(String operateMethodName) {
        this.operateMethod = operateMethodName;
    }

    @Override
    public void operate() {
        // TODO: use reflection to invoke the operate method
        try {
            instance.getClass().getMethod(operateMethod).invoke(instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method: ", e);
        }
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
