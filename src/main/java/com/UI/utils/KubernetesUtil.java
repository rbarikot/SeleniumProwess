package com.UI.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class KubernetesUtil {
    public static void startSeleniumGrid() throws InterruptedException {
        executeCommand("kubectl create -f kubernetes/Selenium-grid.yaml");
        Thread.sleep(50000);
    }

    public static void stopSeleniumGrid() {
        executeCommand("kubectl delete -f kubernetes/Selenium-grid.yaml");
    }
    public static void assignPortToLocal()
    {
        //executeCommand("kubectl port-forward service/selenium-hub 4444:4444");
        new Thread(() -> {
            try {
                ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
                        "kubectl port-forward service/selenium-hub 4444:4444");
                builder.redirectErrorStream(true);
                Process process = builder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[PortForward] " + line);
                }

                // Keep a reference to the process if you want to stop it later
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private static void executeCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
