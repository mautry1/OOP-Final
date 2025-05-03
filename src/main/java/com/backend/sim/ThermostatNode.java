// File: src/main/java/com/backend/sim/ThermostatNode.java
package com.backend.sim;

import com.backend.core.SmartDevice;
import com.backend.core.TemperatureControllable;
import com.backend.observer.HomeEvent;
import com.backend.observer.HomeEventType;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Box;

/**
 * Renders a thermostat with two small buttons (up/down) and
 * a vertical bar whose height reflects the current temperature.
 */
public class ThermostatNode extends DeviceNode {
    private static final float BODY_RADIUS     = 0.5f;
    private static final float BODY_HEIGHT     = 1.0f;
    private static final float BUTTON_SIZE     = 0.15f;
    private static final float INDICATOR_WIDTH = 0.1f;
    private static final int   MIN_TEMP        = 60;
    private static final int   MAX_TEMP        = 90;

    private Geometry upButton, downButton, indicator;

    public ThermostatNode(String id, SmartDevice device, AssetManager assets) {
        super(id, device, assets);
    }

    @Override
    protected void initModel() {
        // 1) Thermostat body
        Cylinder bodyMesh = new Cylinder(16, 16, BODY_RADIUS, BODY_HEIGHT, true);
        Geometry bodyGeom = new Geometry(device.getName() + "_body", bodyMesh);
        Material bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bodyMat.setColor("Color", ColorRGBA.LightGray);
        bodyGeom.setMaterial(bodyMat);
        attachChild(bodyGeom);

        // 2) Up‑button
        Box btnMesh = new Box(BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
        upButton = new Geometry(device.getName() + "_up", btnMesh);
        Material upMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        upMat.setColor("Color", ColorRGBA.Green);
        upButton.setMaterial(upMat);
        upButton.setLocalTranslation(
                BODY_RADIUS + BUTTON_SIZE/2f,
                BODY_HEIGHT/2f + BUTTON_SIZE/2f,
                0f
        );
        attachChild(upButton);

        // 3) Down‑button
        downButton = new Geometry(device.getName() + "_down", btnMesh);
        Material downMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        downMat.setColor("Color", ColorRGBA.Red);
        downButton.setMaterial(downMat);
        downButton.setLocalTranslation(
                BODY_RADIUS + BUTTON_SIZE/2f,
                -BODY_HEIGHT/2f - BUTTON_SIZE/2f,
                0f
        );
        attachChild(downButton);

        // 4) Indicator bar (cyan) whose height will be set in updateIndicator()
        Box barMesh = new Box(INDICATOR_WIDTH, 0.1f, 0.05f);
        indicator = new Geometry(device.getName() + "_bar", barMesh);
        Material barMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        barMat.setColor("Color", ColorRGBA.Cyan);
        indicator.setMaterial(barMat);
        attachChild(indicator);

        // Position the initial indicator bar
        updateIndicator();
    }

    @Override
    protected void updateVisual(HomeEvent event) {
        if (event.type() == HomeEventType.TEMP_CHANGE) {
            updateIndicator();
        }
    }

    /** Recompute & reposition the cyan bar based on current temperature. */
    private void updateIndicator() {
        // Always cast the core SmartDevice to TemperatureControllable here
        TemperatureControllable t =
                (TemperatureControllable) device;
        int temp = t.getTemperature();
        float norm = (temp - MIN_TEMP) / (float)(MAX_TEMP - MIN_TEMP);
        float height = norm * BODY_HEIGHT;

        // Rebuild the mesh to reflect new height
        Box mesh = new Box(INDICATOR_WIDTH, height/2f, 0.05f);
        indicator.setMesh(mesh);

        // Anchor the bottom of the bar at the bottom edge of the cylinder,
        // on the side opposite of the buttons
        indicator.setLocalTranslation(
                -BODY_RADIUS - INDICATOR_WIDTH - 0.1f,
                -BODY_HEIGHT/2f + height/2f,
                0f
        );
        indicator.updateModelBound();
    }

    /** Called by the click‑handler when the up‑button is pressed */
    public void increaseTemperature() {
        TemperatureControllable t = (TemperatureControllable) device;
        int next = Math.min(MAX_TEMP, t.getTemperature() + 1);
        t.setTemperature(next);
    }

    /** Called by the click‑handler when the down‑button is pressed */
    public void decreaseTemperature() {
        TemperatureControllable t = (TemperatureControllable) device;
        int next = Math.max(MIN_TEMP, t.getTemperature() - 1);
        t.setTemperature(next);
    }
}
