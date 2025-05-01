package com.backend.observer;

import java.util.ArrayList;
import java.util.List;

public class SmartHomeEventManager {
    private List<SmartHomeObserver> observers = new ArrayList<>();
    private String name;

    public void addObserver(SmartHomeObserver observer) {observers.add(observer);}

    public void notifyObservers(HomeEvent event) {
        for (SmartHomeObserver observer : observers) {
            observer.onSmartHomeEvent(event);
        }
    }
}
