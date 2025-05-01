package com.backend.core;

public interface TemperatureControllable extends SmartDevice {
    public void setTemperature(int temperature);
    public int getTemperature();
}
