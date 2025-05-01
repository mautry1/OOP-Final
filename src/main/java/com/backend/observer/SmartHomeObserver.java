package com.backend.observer;

public interface SmartHomeObserver {
    //boolean hasReceivedEvent();
    void onSmartHomeEvent(HomeEvent event);
}