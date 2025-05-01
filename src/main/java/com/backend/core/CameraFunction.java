package com.backend.core;

public interface CameraFunction extends SmartDevice {
    public void takePicture();
    public void recordVideo();
    public void setCameraMode(String mode);
}
