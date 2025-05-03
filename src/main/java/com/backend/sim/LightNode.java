package com.backend.sim;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Box;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.backend.core.SmartDevice;
import com.backend.observer.HomeEvent;
import com.backend.observer.HomeEventType;

/**
 * Visual node for SmartLight devices, including a switch button in front of the sphere.
 */
public class LightNode extends DeviceNode {
    private static final float SPHERE_RADIUS = 0.5f;
    private Geometry sphereGeom;
    private Geometry switchGeom;

    public LightNode(String id, SmartDevice device, AssetManager assetManager) {
        super(id, device, assetManager);
    }

    @Override
    protected void initModel() {
        // Create the light sphere
        Sphere sphereMesh = new Sphere(32, 32, SPHERE_RADIUS);
        sphereMesh.setTextureMode(Sphere.TextureMode.Projected);
        sphereGeom = new Geometry(device.getName(), sphereMesh);
        Material sphereMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sphereMat.setColor("Color", ColorRGBA.Gray);
        sphereGeom.setMaterial(sphereMat);
        sphereGeom.setShadowMode(RenderQueue.ShadowMode.Off);
        sphereGeom.updateModelBound();
        attachChild(sphereGeom);

        // Create the switch button (box) in front of sphere
        Box switchMesh = new Box(0.2f, 0.1f, 0.05f);
        switchGeom = new Geometry(device.getName() + "_switch", switchMesh);
        Material switchMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        switchMat.setColor("Color", ColorRGBA.DarkGray);
        switchGeom.setMaterial(switchMat);
        switchGeom.setShadowMode(RenderQueue.ShadowMode.Off);
        // Position it in front of the sphere along Z-axis
        switchGeom.setLocalTranslation(0f, -SPHERE_RADIUS - 0.2f, 0f);
        // Ensure collision shape matches
        switchGeom.updateModelBound();
        attachChild(switchGeom);
    }

    @Override
    protected void updateVisual(HomeEvent event) {
        String evSource = event.sourceDevice();
        if (!device.getName().equals(evSource)) {
            return;
        }
        Material sphereMat = sphereGeom.getMaterial();
        if (event.type() == HomeEventType.LIGHT_ON) {
            sphereMat.setColor("Color", ColorRGBA.Yellow);
        } else if (event.type() == HomeEventType.LIGHT_OFF) {
            sphereMat.setColor("Color", ColorRGBA.Gray);
        }
        // Optionally update switch color to reflect current
        Material switchMat = switchGeom.getMaterial();
        if (event.type() == HomeEventType.LIGHT_ON) {
            switchMat.setColor("Color", ColorRGBA.Green);
        } else if (event.type() == HomeEventType.LIGHT_OFF) {
            switchMat.setColor("Color", ColorRGBA.DarkGray);
        }
        // Refresh bounds
        sphereGeom.updateModelBound();
        switchGeom.updateModelBound();
    }
}
