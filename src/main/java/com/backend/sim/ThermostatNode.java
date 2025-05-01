package com.backend.sim;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.backend.core.SmartDevice;
import com.backend.observer.HomeEvent;
import com.backend.observer.HomeEventType;
import com.backend.devices.SmartThermostat;

public class ThermostatNode extends DeviceNode {
    private Geometry geom;

    public ThermostatNode(String id, SmartDevice device, AssetManager assetManager) {
        super(id, device, assetManager);
    }

    @Override
    protected void initModel() {
        Cylinder cyl = new Cylinder(16, 16, 0.3f, 1f, true);
        geom = new Geometry(device.getName(), cyl);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        attachChild(geom);
    }

    @Override
    protected void updateVisual(HomeEvent event) {
        if (event.type() == HomeEventType.TEMP_CHANGE) {
            double t = ((SmartThermostat)device).getTemperature();
            float ratio = Math.min(Math.max((float)((t - 50)/50), 0f), 1f);
            ColorRGBA color = new ColorRGBA(ratio, 0f, 1f - ratio, 1f);
            geom.getMaterial().setColor("Color", color);
        }
    }
}