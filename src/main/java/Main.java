package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        App app = new App();
        Map<String, ArrayList<Vehicle>> threadMap = new HashMap<>();
        int j = 0;

        app.setNrP();
        app.generateVehicles();
        app.generateParking();

        // Creating the list of vehicles for each gate
        for (int i = 0; i < app.getVehicles().size(); i++) {
            if (app.getVehicles().get(i).getType().equals("Car") || app.getVehicles().get(i).getType().equals("Moto")) {
                if (j % 3 == 0) {
                    threadMap.putIfAbsent("commonGate1", new ArrayList<>());
                    threadMap.get("commonGate1").add(app.getVehicles().get(i));
                }
                if (j % 3 == 1) {
                    threadMap.putIfAbsent("commonGate2", new ArrayList<>());
                    threadMap.get("commonGate2").add(app.getVehicles().get(i));
                }
                if (j % 3 == 2) {
                    threadMap.putIfAbsent("commonGate3", new ArrayList<>());
                    threadMap.get("commonGate3").add(app.getVehicles().get(i));
                }
                j++;
            }
            if (app.getVehicles().get(i).getType().equals("Bus") || app.getVehicles().get(i).getType().equals("Semi")) {
                threadMap.putIfAbsent("bigGate", new ArrayList<>());
                threadMap.get("bigGate").add(app.getVehicles().get(i));
            }
        }

        app.run(threadMap);

    }
}