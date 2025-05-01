package com.backend.sim;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.backend.core.SmartDevice;
import com.backend.observer.HomeEvent;
import com.backend.observer.HomeEventType;

public class LightNode extends DeviceNode {
    private Geometry geom;

    public LightNode(String id, SmartDevice device, AssetManager assetManager) {
        super(id, device, assetManager);
    }

    @Override
    protected void initModel() {
        Sphere sphere = new Sphere(16, 16, 0.5f);
        geom = new Geometry(device.getName(), sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.Gray);
        geom.setMaterial(mat);
        attachChild(geom);
    }

    @Override
    protected void updateVisual(HomeEvent event) {
        if (event.type() == HomeEventType.LIGHT_ON) {
            geom.getMaterial().setColor("Diffuse", ColorRGBA.Yellow);
        } else if (event.type() == HomeEventType.LIGHT_OFF) {
            geom.getMaterial().setColor("Diffuse", ColorRGBA.Gray);
        }
    }
}