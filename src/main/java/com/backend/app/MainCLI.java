package com.backend.app;

import com.backend.controller.HomeController;

import java.util.Scanner;

public class MainCLI {
    private final HomeController controller = HomeController.getInstance();

    public static void main(String[] args) {
        // If user wants the sim, they can type 'sim' at the REPL
        new MainCLI().runREPL();
    }

    private void runREPL() {
        Scanner in = new Scanner(System.in);
        System.out.println("SmartHome CLI – type 'help' for commands");
        while (true) {
            System.out.print("> ");
            String line = in.nextLine().trim();
            if (line.isEmpty()) continue;
            if ("exit".equalsIgnoreCase(line) || "quit".equalsIgnoreCase(line)) break;
            try {
                execute(line);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void execute(String line) {
        String[] parts = line.split("\\s+");
        switch (parts[0].toLowerCase()) {
            case "help" -> {
                System.out.println("add <type> <vendor> <id>");
                System.out.println("list");
                System.out.println("toggle <id>");
                System.out.println("telemetry");
                System.out.println("exit");
            }
            case "add" -> {
                if (parts.length < 4) {
                    System.out.println("Usage: add <type> <vendor> <id>");
                } else {
                    controller.addDevice(parts[1], parts[2], parts[3]);
                    System.out.println("Added " + parts[1] + " '" + parts[3] + "'");
                }
            }
            case "list" -> {
                controller.listAllDevices()
                        .forEach(d -> System.out.println(d.getName()
                                + " (" + d.getClass().getSimpleName() + ")"));
            }
            case "toggle" -> {
                if (parts.length < 2) {
                    System.out.println("Usage: toggle <id>");
                } else {
                    controller.toggleDevice(parts[1]);
                    System.out.println("Toggled " + parts[1]);
                }
            }
            case "telemetry" -> {
                controller.getTelemetry()
                        .forEach(System.out::println);
            }
            default -> System.out.println("Unknown command: " + parts[0]);
        }
    }
}
