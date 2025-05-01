package com.backend.sim;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.backend.core.SmartDevice;
import com.backend.observer.HomeEvent;
import com.backend.observer.HomeEventType;

public class LockNode extends DeviceNode {
    private Geometry geom;

    public LockNode(String id, SmartDevice device, AssetManager assetManager) {
        super(id, device, assetManager);
    }

    @Override
    protected void initModel() {
        Box box = new Box(0.5f, 0.5f, 0.2f);
        geom = new Geometry(device.getName(), box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        geom.setMaterial(mat);
        attachChild(geom);
    }

    @Override
    protected void updateVisual(HomeEvent event) {
        if (event.type() == HomeEventType.LOCK_SECURED) {
            geom.getMaterial().setColor("Color", ColorRGBA.Green);
        } else if (event.type() == HomeEventType.LOCK_UNSECURED) {
            geom.getMaterial().setColor("Color", ColorRGBA.Red);
        }
    }
}