// File: src/main/java/com/backend/sim/SimulatorApp.java
package com.backend.sim;

import com.backend.controller.HomeController;
import com.backend.core.SmartDevice;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.MouseInput;
import com.jme3.system.AppSettings;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import java.util.List;

public class SimulatorApp extends SimpleApplication {
    private HomeController controller;
    private ThermostatNode activeThermostat;

    public static void main(String[] args) {
        SimulatorApp app = new SimulatorApp();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Smart Home Simulator");
        settings.setFullscreen(true);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        controller = HomeController.getInstance();

        // (lighting, camera, crosshair omitted for brevity)

        // Register & place devices
        controller.addDevice("light", "",      "L1");
        controller.addDevice("lock",  "",      "LockA");
        controller.addDevice("thermostat", "", "T100");

        AssetManager assets = getAssetManager();
        Node root = getRootNode();
        List<SmartDevice> devices = controller.listAllDevices();
        float spacing = 4f, start = -((devices.size() - 1) * spacing) / 2f;

        for (int i = 0; i < devices.size(); i++) {
            SmartDevice d = devices.get(i);
            DeviceNode node;
            switch (d.getClass().getSimpleName()) {
                case "SmartLight":
                    node = new LightNode(d.getName(), d, assets);
                    break;
                case "SmartLock":
                    node = new LockNode(d.getName(), d, assets);
                    break;
                case "SmartThermostat":
                    node = new ThermostatNode(d.getName(), d, assets);
                    break;
                default: continue;
            }
            node.setLocalTranslation(start + i*spacing, 0f, 0f);
            root.attachChild(node);
        }

        // Single “Pick” mapping for all clicks
        inputManager.addMapping("Pick",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(clickListener, "Pick");
    }

    private final ActionListener clickListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!"Pick".equals(name) || !isPressed) return;

            // 1) Cast a ray from screen center
            Vector2f pos  = inputManager.getCursorPosition();
            Vector3f o    = cam.getWorldCoordinates(pos, 0f);
            Vector3f dir  = cam.getWorldCoordinates(pos, 1f)
                    .subtractLocal(o).normalizeLocal();
            CollisionResults results = new CollisionResults();
            rootNode.collideWith(new Ray(o, dir), results);
            if (results.size() == 0) return;

            // 2) Determine hit geometry and parent device node
            var hit   = results.getClosestCollision().getGeometry();
            String geomName = hit.getName();
            var parentNode  = (DeviceNode) hit.getParent();

            // 3) Thermostat buttons
            if (parentNode instanceof ThermostatNode tn) {
                if (geomName.endsWith("_up")) {
                    tn.increaseTemperature();
                    return;
                }
                if (geomName.endsWith("_down")) {
                    tn.decreaseTemperature();
                    return;
                }
                // ignore other clicks on thermostat
                return;
            }
            // 4) Lights & locks toggle via controller
            try {
                controller.toggleDevice(parentNode.getName());
            } catch (Exception ex) {
                System.err.println("Toggle failed: " + ex.getMessage());
            }
        }
    };
}
