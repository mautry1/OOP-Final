package com.backend.core;

public interface MotionSensing extends SmartDevice {
    public void detectMotion();
    public void stopDetectingMotion();
    public boolean isDetectingMotion();
}
