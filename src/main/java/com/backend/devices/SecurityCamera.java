package com.backend.devices;

import com.backend.core.CameraEnabled;
import com.backend.core.MotionSensing;
import com.backend.core.Powerable;
import com.backend.core.SecureDevice;
import com.backend.observer.SmartHomeObserver;

import java.util.ArrayList;
import java.util.List;

public class SecurityCamera implements MotionSensing, CameraEnabled, SecureDevice, Powerable {
    private Boolean motionDetected;
    private String cameraMode;
    private String deviceName;
    private Boolean poweredOn;
    private final List<SmartHomeObserver> observers = new ArrayList<>();

    @Override
    public void addSmartHomeEventListener(SmartHomeObserver observer) {
        observers.add(observer);
    }

    public SecurityCamera() {
        this.motionDetected = false;
        this.cameraMode = "default";
        this.deviceName = "default";
    }
    
    public SecurityCamera(String deviceName) {
        this.motionDetected = false;
        this.cameraMode = "default";
        this.deviceName = deviceName;
    }
    
    public void takePicture() {
        if(cameraMode == "Picture") {
            // Implementation for taking a picture
        } else {
            System.out.println("Camera is not in Picture mode.");
        }
    }

    public void recordVideo() {
        if(cameraMode == "Video") {
            // Implementation for recording a video
        } else {
            System.out.println("Camera is not in Video mode.");
        }
    }

    public void setCameraMode(String mode) {
        this.cameraMode = mode;
    }

    public String getCameraMode() {
        return cameraMode;
    }

    public void detectMotion() {
        this.motionDetected = true;
    }

    public void stopDetectingMotion() {
        this.motionDetected = false;
    }

    public boolean isDetectingMotion() {
        if(motionDetected) {
            return true;
        } else {
            return false;
        }
    }

    public void operate() {
        // Implementation for operating the secure device
    }

    public void turnOn() {
        poweredOn = true;
    }

    public void turnOff() {
        poweredOn = false;
    }

    public boolean isOn() {
        return poweredOn;
    }

    public String getName() {
        return deviceName;
    }
}
