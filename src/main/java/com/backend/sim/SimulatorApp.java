package com.backend.sim;

import com.backend.controller.HomeController;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.scene.shape.Line;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.MouseInput;
import com.jme3.math.Vector2f;
import java.util.function.Function;

public class SimulatorApp extends SimpleApplication {
    private HomeController controller;
    private final float walkSpeed = 1.5f; // m/s
    private Node playerNode;         // your physics and walk node
    private Node cameraPivot;        // sits at eye‑height, child of playerNode
    
    // Mouse control variables
    private boolean mouseActive = true; // Start with mouse look enabled
    private float mouseXSensitivity = 5.0f; // Adjusted sensitivity (tweak as needed)
    private float mouseYSensitivity = 5.0f; // Adjusted sensitivity (tweak as needed)

    // For debug comparison - stores previous cursor position (if needed for other purposes)
    private Vector2f prevCursorPos = new Vector2f(); 

    public static void main(String[] args) {
        SimulatorApp app = new SimulatorApp();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Smart Home Simulator – Room Only");
        settings.setFullscreen(true); // Consider starting windowed for easier debugging
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Initial cursor state (hidden when mouse look is active)
        inputManager.setCursorVisible(!mouseActive);
        
        // ---------- Physics Setup ----------
        BulletAppState bullet = new BulletAppState();
        stateManager.attach(bullet);
        PhysicsSpace physics = bullet.getPhysicsSpace();

        // ---------- Controller ----------
        controller = HomeController.getInstance();

        // ---------- Lighting ----------
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.5f));
        rootNode.addLight(ambient);
        PointLight ceilingLight = new PointLight();
        ceilingLight.setColor(ColorRGBA.White.mult(2f));
        // will set its position after we know ceilY
        rootNode.addLight(ceilingLight);

        // ---------- Materials ----------
        Material fillMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        fillMat.setBoolean("UseMaterialColors", true);
        fillMat.setColor("Ambient",  ColorRGBA.White.mult(0.3f));
        fillMat.setColor("Diffuse",  ColorRGBA.White);
        fillMat.setColor("Specular", ColorRGBA.White);
        fillMat.setFloat("Shininess", 64f);
        fillMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        Function<ColorRGBA,Material> lineMat = c -> {
            Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            m.getAdditionalRenderState().setLineWidth(2f);
            m.setColor("Color", c);
            return m;
        };
        Material blackGrid  = lineMat.apply(ColorRGBA.Black);
        Material greenGrid  = lineMat.apply(ColorRGBA.Green);
        Material redGrid    = lineMat.apply(ColorRGBA.Red);
        Material yellowGrid = lineMat.apply(ColorRGBA.Yellow);
        Material blueGrid   = lineMat.apply(ColorRGBA.Blue);

        // ---------- Room Dimensions ----------
        float roomW = 100f, roomD = 100f, roomH = 10f, wallT = 0.2f;
        float halfW = roomW/2f, halfD = roomD/2f, halfH = roomH/2f;
        int   divs  = 20;
        float dx = roomW/divs, dz = roomD/divs, dy = roomH/divs;
        float floorY = -halfH - wallT, ceilY = halfH + wallT;
        float backZ  = -halfD - wallT, frontZ = halfD + wallT;
        float leftX  = -halfW - wallT, rightX = halfW + wallT;

        // ---------- Floor ----------
        Box floorBox = new Box(halfW, wallT, halfD);
        Geometry floorGeo = new Geometry("floor", floorBox);
        floorGeo.setMaterial(fillMat);
        floorGeo.setLocalTranslation(0, floorY, 0);
        rootNode.attachChild(floorGeo);
        RigidBodyControl floorPhy = new RigidBodyControl(0);
        floorGeo.addControl(floorPhy);
        physics.add(floorPhy);
        for (int i = 0; i <= divs; i++) {
            float x = -halfW + i*dx;
            Line lx = new Line(
                    new Vector3f(x, floorY + 0.01f, -halfD),
                    new Vector3f(x, floorY + 0.01f, +halfD)
            );
            Geometry gx = new Geometry("floorGX"+i, lx);
            gx.setMaterial(blackGrid);
            rootNode.attachChild(gx);

            float z = -halfD + i*dz;
            Line lz = new Line(
                    new Vector3f(-halfW, floorY + 0.01f, z),
                    new Vector3f(+halfW, floorY + 0.01f, z)
            );
            Geometry gz = new Geometry("floorGZ"+i, lz);
            gz.setMaterial(blackGrid);
            rootNode.attachChild(gz);
        }

        // ---------- Ceiling ----------
        Geometry ceilGeo = new Geometry("ceiling", floorBox);
        ceilGeo.setMaterial(fillMat);
        ceilGeo.setLocalTranslation(0, ceilY, 0);
        rootNode.attachChild(ceilGeo);
        RigidBodyControl ceilPhy = new RigidBodyControl(0);
        ceilGeo.addControl(ceilPhy);
        physics.add(ceilPhy);
        for (int i = 0; i <= divs; i++) {
            float x = -halfW + i*dx;
            Line lx = new Line(
                    new Vector3f(x, ceilY - 0.01f, -halfD),
                    new Vector3f(x, ceilY - 0.01f, +halfD)
            );
            Geometry gx = new Geometry("ceilGX"+i, lx);
            gx.setMaterial(blackGrid);
            rootNode.attachChild(gx);

            float z = -halfD + i*dz;
            Line lz = new Line(
                    new Vector3f(-halfW, ceilY - 0.01f, z),
                    new Vector3f(+halfW, ceilY - 0.01f, z)
            );
            Geometry gz = new Geometry("ceilGZ"+i, lz);
            gz.setMaterial(blackGrid);
            rootNode.attachChild(gz);
        }
        ceilingLight.setPosition(new Vector3f(0, ceilY - 0.1f, 0));

        // ---------- Back Wall (green) ----------
        Box wallFB = new Box(halfW, halfH, wallT);
        Geometry backGeo = new Geometry("backWall", wallFB);
        backGeo.setMaterial(fillMat);
        backGeo.setLocalTranslation(0, 0, backZ);
        rootNode.attachChild(backGeo);
        RigidBodyControl backPhy = new RigidBodyControl(0);
        backGeo.addControl(backPhy);
        physics.add(backPhy);
        for (int i = 0; i <= divs; i++) {
            float x = -halfW + i*dx;
            Line lv = new Line(
                    new Vector3f(x, -halfH, backZ + 0.01f),
                    new Vector3f(x, +halfH, backZ + 0.01f)
            );
            Geometry gv = new Geometry("backGV"+i, lv);
            gv.setMaterial(greenGrid);
            rootNode.attachChild(gv);

            float y = -halfH + i*dy;
            Line lh = new Line(
                    new Vector3f(-halfW, y, backZ + 0.01f),
                    new Vector3f(+halfW, y, backZ + 0.01f)
            );
            Geometry gh = new Geometry("backGH"+i, lh);
            gh.setMaterial(greenGrid);
            rootNode.attachChild(gh);
        }

        // ---------- Front Wall (red) ----------
        Geometry frontGeo = new Geometry("frontWall", wallFB);
        frontGeo.setMaterial(fillMat);
        frontGeo.setLocalTranslation(0, 0, frontZ);
        rootNode.attachChild(frontGeo);
        RigidBodyControl frontPhy = new RigidBodyControl(0);
        frontGeo.addControl(frontPhy);
        physics.add(frontPhy);
        for (int i = 0; i <= divs; i++) {
            float x = -halfW + i*dx;
            Line lv = new Line(
                    new Vector3f(x, -halfH, frontZ - 0.01f),
                    new Vector3f(x, +halfH, frontZ - 0.01f)
            );
            Geometry gv = new Geometry("frontGV"+i, lv);
            gv.setMaterial(redGrid);
            rootNode.attachChild(gv);

            float y = -halfH + i*dy;
            Line lh = new Line(
                    new Vector3f(-halfW, y, frontZ - 0.01f),
                    new Vector3f(+halfW, y, frontZ - 0.01f)
            );
            Geometry gh = new Geometry("frontGH"+i, lh);
            gh.setMaterial(redGrid);
            rootNode.attachChild(gh);
        }

        // ---------- Left Wall (yellow) ----------
        Box wallLR = new Box(halfD, halfH, wallT);
        Geometry leftGeo = new Geometry("leftWall", wallLR);
        leftGeo.setMaterial(fillMat);
        leftGeo.rotate(0, FastMath.HALF_PI, 0);
        leftGeo.setLocalTranslation(leftX, 0, 0);
        rootNode.attachChild(leftGeo);
        RigidBodyControl leftPhy = new RigidBodyControl(0);
        leftGeo.addControl(leftPhy);
        physics.add(leftPhy);
        for (int i = 0; i <= divs; i++) {
            float z = -halfD + i*dz;
            Line lv = new Line(
                    new Vector3f(leftX + 0.01f, -halfH, z),
                    new Vector3f(leftX + 0.01f, +halfH, z)
            );
            Geometry gv = new Geometry("leftGV"+i, lv);
            gv.setMaterial(yellowGrid);
            rootNode.attachChild(gv);

            float y = -halfH + i*dy;
            Line lh = new Line(
                    new Vector3f(leftX + 0.01f, y, -halfD),
                    new Vector3f(leftX + 0.01f, y, +halfD)
            );
            Geometry gh = new Geometry("leftGH"+i, lh);
            gh.setMaterial(yellowGrid);
            rootNode.attachChild(gh);
        }

        // ---------- Right Wall (blue) ----------
        Geometry rightGeo = new Geometry("rightWall", wallLR);
        rightGeo.setMaterial(fillMat);
        rightGeo.rotate(0, FastMath.HALF_PI, 0);
        rightGeo.setLocalTranslation(rightX, 0, 0);
        rootNode.attachChild(rightGeo);
        RigidBodyControl rightPhy = new RigidBodyControl(0);
        rightGeo.addControl(rightPhy);
        physics.add(rightPhy);
        for (int i = 0; i <= divs; i++) {
            float z = -halfD + i*dz;
            Line lv = new Line(
                    new Vector3f(rightX - 0.01f, -halfH, z),
                    new Vector3f(rightX - 0.01f, +halfH, z)
            );
            Geometry gv = new Geometry("rightGV"+i, lv);
            gv.setMaterial(blueGrid);
            rootNode.attachChild(gv);

            float y = -halfH + i*dy;
            Line lh = new Line(
                    new Vector3f(rightX - 0.01f, y, -halfD),
                    new Vector3f(rightX - 0.01f, y, +halfD)
            );
            Geometry gh = new Geometry("rightGH"+i, lh);
            gh.setMaterial(blueGrid);
            rootNode.attachChild(gh);
        }

        // ------ Player & Capsule ------
        playerNode = new Node("player");
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(0.5f, 1.8f);
        CharacterControl player = new CharacterControl(capsule, 0.05f);
        player.setGravity(9.81f);
        playerNode.addControl(player);

        // teleport into the center of the room, 1 m above the floor
        float spawnY = -halfH + 1f;
        player.setPhysicsLocation(new Vector3f(0, spawnY, 0));

        rootNode.attachChild(playerNode);
        physics.add(player);

        // ------ Camera Pivot for Pitch ------
        cameraPivot = new Node("cameraPivot");
        cameraPivot.setLocalTranslation(0, 1.6f, 0);  // eye height
        playerNode.attachChild(cameraPivot);
        cameraPivot.addControl(
                new CameraControl(cam, CameraControl.ControlDirection.SpatialToCamera)
        );

        // disable the default FlyCam and ensure cursor is hidden
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(!mouseActive);

        // ------ Input Mappings ------
        // Movement
        inputManager.addMapping("Forward",  new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Left",     new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right",    new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump",     new KeyTrigger(KeyInput.KEY_SPACE));

        // Mouse‑look axes
        inputManager.addMapping("LookLeft",  new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("LookRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("LookUp",    new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("LookDown",  new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        // ------ Mouse‑Look Listener ------
        AnalogListener lookListener = (name, value, tpf) -> {
            if (!mouseActive) {
                return;
            }
            float xs = value * mouseXSensitivity;
            float ys = value * mouseYSensitivity;

            switch (name) {
                case "LookLeft"  -> playerNode.rotate(0,  xs, 0);
                case "LookRight" -> playerNode.rotate(0, -xs, 0);
                case "LookUp"    -> cameraPivot.rotate(-ys, 0, 0);
                case "LookDown"  -> cameraPivot.rotate( ys, 0, 0);
            }
        };
        inputManager.addListener(lookListener,
                "LookLeft","LookRight","LookUp","LookDown"
        );

        // ------ Movement + Jump Listener ------
        ActionListener moveListener = new ActionListener() {
            private boolean fwd, back, left, right;

            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                CharacterControl ctrl = playerNode.getControl(CharacterControl.class);
                if (ctrl == null) {
                    return;
                }
                switch (name) {
                    case "Forward"  -> fwd  = isPressed;
                    case "Backward" -> back = isPressed;
                    case "Left"     -> left = isPressed;
                    case "Right"    -> right= isPressed;
                    case "Jump"     -> {
                        if (isPressed && ctrl.onGround()) {
                            ctrl.jump();  // <-- no-arg jump()
                        }
                    }
                }
                // build horizontal move direction from cam orientation
                Vector3f dir = new Vector3f();
                Vector3f camDir  = cam.getDirection().clone().setY(0).normalizeLocal();
                Vector3f camLeft = cam.getLeft().clone().setY(0).normalizeLocal();

                if (fwd)  dir.addLocal(camDir);
                if (back) dir.subtractLocal(camDir);
                if (left) dir.addLocal(camLeft);
                if (right)dir.subtractLocal(camLeft);

                ctrl.setWalkDirection(dir.multLocal(walkSpeed));
            }
        };
        inputManager.addListener(moveListener,
                "Forward","Backward","Left","Right","Jump"
        );
    }
}