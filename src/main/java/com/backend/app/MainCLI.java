package com.backend.app;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.backend.controller.HomeController;
import com.backend.core.SmartDevice;
import com.backend.observer.HomeEvent;

public class MainCLI {

    public static void main(String[] args) {
        HomeController controller = HomeController.getInstance();

        if (args.length == 0) {
            runInteractive(controller);
        } else {
            executeSingle(args, controller);
        }
    }

    private static void runInteractive(HomeController controller) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entering SmartHome interactive mode. Type 'exit' to quit.");
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line == null) break;
            line = line.trim();
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
                break;
            }
            if (line.isEmpty()) continue;
            String[] tokens = line.split("\\s+");
            executeSingle(tokens, controller);
        }
        scanner.close();
        System.out.println("Exiting SmartHome CLI.");
    }

    private static void executeSingle(String[] args, HomeController controller) {
        String cmd = args[0].toLowerCase();

        try {
            switch (cmd) {
                case "add":
                    if (args.length < 4) {
                        System.err.println("Usage: add <type> <vendor> <id>");
                    } else {
                        controller.addDevice(args[1], args[2], args[3]);
                        System.out.println("Added device '" + args[3] + "' of type " + args[1]);
                    }
                    break;

                case "add3rd":
                    if (args.length < 4) {
                        System.err.println("Usage: add3rd <adapterName> <className> <id>");
                    } else {
                        controller.addThirdPartyDevice(args[1], args[2], args[3]);
                        System.out.println("Added third-party device '" + args[3] + "' via adapter " + args[1]);
                    }
                    break;

                case "on":
                    if (args.length < 2) {
                        System.err.println("Usage: on <id>");
                    } else {
                        controller.turnOnDevice(args[1]);
                        System.out.println("Device '" + args[1] + "' turned on.");
                    }
                    break;

                case "off":
                    if (args.length < 2) {
                        System.err.println("Usage: off <id>");
                    } else {
                        controller.turnOffDevice(args[1]);
                        System.out.println("Device '" + args[1] + "' turned off.");
                    }
                    break;

                case "list":
                    List<SmartDevice> devices = controller.listAllDevices();
                    if (devices.isEmpty()) {
                        System.out.println("No devices registered.");
                    } else {
                        devices.forEach(d -> System.out.println(
                                d.getName() + " (" + d.getClass().getSimpleName() + ")"
                        ));
                    }
                    break;

                case "telemetry":
                    List<HomeEvent> events = controller.getTelemetry();
                    if (events.isEmpty()) {
                        System.out.println("No telemetry events.");
                    } else {
                        events.forEach(e -> System.out.println(e));
                    }
                    break;

                default:
                    System.err.println("Unknown command: " + cmd);
                    printUsage();
            }
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("SmartHome CLI Usage:");
        System.out.println("  (no args)                    - interactive mode");
        System.out.println("  add <type> <vendor> <id>     - register a new device");
        System.out.println("  add3rd <adapter> <class> <id> - register a third-party device");
        System.out.println("  on <id>                      - turn on a powerable device");
        System.out.println("  off <id>                     - turn off a powerable device");
        System.out.println("  list                         - list all devices");
        System.out.println("  telemetry                    - show recorded events");
        System.out.println("  exit / quit                  - exit interactive mode");
    }
}
