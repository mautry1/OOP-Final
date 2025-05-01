package com.backend.sim;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.collision.CollisionResults;

import com.backend.controller.HomeController;
import com.backend.core.SmartDevice;

import java.util.List;

public class SimulatorApp extends SimpleApplication {
    private HomeController controller;

    public static void main(String[] args) {
        SimulatorApp app = new SimulatorApp();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.setFullscreen(true);
        settings.setTitle("Smart Home Simulator");
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        controller = HomeController.getInstance();

        // Basic lighting
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(1.2f));
        rootNode.addLight(ambient);
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1f, -1f, -1f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // Camera setup
        cam.setLocation(new Vector3f(0f, 2f, 8f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10f);

        // Crosshair
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText crosshair = new BitmapText(guiFont, false);
        crosshair.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        crosshair.setText("+");
        int w = settings.getWidth();
        int h = settings.getHeight();
        crosshair.setLocalTranslation(
                w / 2f - crosshair.getLineWidth() / 2f,
                h / 2f + crosshair.getLineHeight() / 2f,
                0f
        );
        guiNode.attachChild(crosshair);

        // Pre-register devices
        controller.addDevice("light", "", "L1");
        controller.addDevice("lock", "", "LockA");
        controller.addDevice("thermostat", "", "T100");

        // Attach DeviceNodes at spaced positions
        AssetManager assets = getAssetManager();
        Node root = getRootNode();
        List<SmartDevice> devices = controller.listAllDevices();
        int count = devices.size();
        float spacing = 4f;
        float start = -((count - 1) * spacing) / 2f;

        for (int i = 0; i < count; i++) {
            SmartDevice device = devices.get(i);
            DeviceNode node;
            switch (device.getClass().getSimpleName()) {
                case "SmartLight":
                    node = new LightNode(device.getName(), device, assets);
                    break;
                case "SmartLock":
                    node = new LockNode(device.getName(), device, assets);
                    break;
                case "SmartThermostat":
                    node = new ThermostatNode(device.getName(), device, assets);
                    break;
                default:
                    continue;
            }
            // position node along the X-axis
            node.setLocalTranslation(new Vector3f(start + i * spacing, 0f, 0f));
            root.attachChild(node);
        }

        // Mouse picking for interaction
        inputManager.addMapping("Pick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(pickListener, "Pick");
    }

    // ActionListener for mouse clicks
    private final ActionListener pickListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!name.equals("Pick") || !isPressed) {
                return;
            }
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(click2d, 0f);
            Vector3f dir = cam.getWorldCoordinates(click2d, 1f)
                    .subtractLocal(click3d)
                    .normalizeLocal();
            Ray ray = new Ray(click3d, dir);
            CollisionResults results = new CollisionResults();
            rootNode.collideWith(ray, results);
            if (results.size() > 0) {
                String nodeName = results.getClosestCollision()
                        .getGeometry()
                        .getParent()
                        .getName();
                try {
                    controller.turnOnDevice(nodeName);
                } catch (IllegalArgumentException ex) {
                    controller.turnOffDevice(nodeName);
                }
            }
        }
    };
}