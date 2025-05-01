package com.backend.core;

public interface Dimmable extends SmartDevice {
    public int getBrightnessLevel();
    public void setBrightnessLevel(int level);
}
