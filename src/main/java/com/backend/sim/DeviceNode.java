package com.backend.sim;

import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;
import com.backend.core.SmartDevice;
import com.backend.observer.HomeEvent;
import com.backend.observer.SmartHomeObserver;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Geometry;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;

/**
 * Base class for visualizing SmartDevice objects in 3D,
 * with attached invisible picker geometry for reliable clicks.
 */
public abstract class DeviceNode extends Node implements SmartHomeObserver {
    protected final SmartDevice device;
    protected final AssetManager assetManager;
    private Geometry picker;

    public DeviceNode(String id, SmartDevice device, AssetManager assetManager) {
        super(id);
        this.device = device;
        this.assetManager = assetManager;
        this.device.addSmartHomeEventListener(this);

        initModel();
        initPicker();
    }

    /**
     * Subclasses create their visible geometry here.
     */
    protected abstract void initModel();
    /**
     * Called when HomeEvent for this device arrives.
     */
    protected abstract void updateVisual(HomeEvent event);

    /**
     * Initializes an invisible, enlarged bounding box for picking.
     */
    private void initPicker() {
        // Refresh bounds
        updateModelBound();
        updateGeometricState();

        BoundingVolume bv = getWorldBound();
        if (bv instanceof BoundingBox) {
            BoundingBox bb = (BoundingBox) bv;
            Vector3f ext = bb.getExtent(null);
            // Scale extents by 150%
            float sx = ext.x * 1.5f;
            float sy = ext.y * 1.5f;
            float sz = ext.z * 1.5f;

            Box box = new Box(sx, sy, sz);
            picker = new Geometry(getName() + "_picker", box);

            // Invisible material
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            mat.setColor("Color", new ColorRGBA(1,1,1,0f));
            picker.setMaterial(mat);
            picker.setShadowMode(RenderQueue.ShadowMode.Off);

            // Center picker at device
            Vector3f center = bb.getCenter();
            picker.setLocalTranslation(center.subtract(getLocalTranslation()));

            attachChild(picker);
        }
    }

    @Override
    public void onSmartHomeEvent(HomeEvent event) {
        // Match by device name
        if (device.getName().equals(event.sourceDevice())) {
            updateVisual(event);
        }
    }
}