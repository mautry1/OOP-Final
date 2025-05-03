package com.backend.core;

/**
 * A device whose temperature can be read and set.
 */
public interface TemperatureControllable extends SmartDevice {
    /**
     * Get current temperature in Fahrenheit.
     */
    int getTemperature();
    /**
     * Set desired temperature in Fahrenheit.
     */
    void setTemperature(int temperature);
}