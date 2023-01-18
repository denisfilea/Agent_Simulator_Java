package main.java;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.*;

public class App {
    private int nrCarP, nrMotoP, nrBusP, nrSemiP;
    private final List<ParkingSpot> parkingSpots = new ArrayList<>();
    private final List<Vehicle> vehicles = new ArrayList<>();

    public int getVehicleListSize() {
        return vehicles.size();
    }

    public void setNrCarP(int nrCarP) {
        this.nrCarP = nrCarP;
    }

    public void setNrMotoP(int nrMotoP) {
        this.nrMotoP = nrMotoP;
    }

    public void setNrBusP(int nrBusP) {
        this.nrBusP = nrBusP;
    }

    public void setNrSemiP(int nrSemiP) {
        this.nrSemiP = nrSemiP;
    }

    // Random vehicle generator. Odds: 40% Car, 30% Motorcycle, 20% Bus, 10% Semi
    public void generateVehicles() {
        Random random = new Random();

        for (int i = 0; i < ((nrCarP + nrMotoP + nrSemiP + nrBusP) * 2); i++) {
            int rand = random.nextInt(10);
            if (rand == 0 || rand == 1 || rand == 2 || rand == 3)
                vehicles.add(new Vehicle("Car"));
            if (rand == 4 || rand == 5 || rand == 6)
                vehicles.add(new Vehicle("Moto"));
            if (rand == 7 || rand == 8)
                vehicles.add(new Vehicle("Bus"));
            if (rand == 9)
                vehicles.add(new Vehicle("Semi"));
        }
    }


    // Getter for vehicles
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    // Generator for parking spots
    public void generateParking() {
        for (int i = 0; i < nrCarP; i++) {
            parkingSpots.add(new ParkingSpot("Car"));
        }
        for (int i = 0; i < nrMotoP; i++) {
            parkingSpots.add(new ParkingSpot("Moto"));
        }
        for (int i = 0; i < nrBusP; i++) {
            parkingSpots.add(new ParkingSpot("Bus"));
        }
        for (int i = 0; i < nrSemiP; i++) {
            parkingSpots.add(new ParkingSpot("Semi"));
        }

    }

    // Setting the number of each parking spot type by user input
    public void setNrP() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of car parking spots");
        nrCarP = scanner.nextInt();
        System.out.println("Enter the number of motorcycle parking spots");
        nrMotoP = scanner.nextInt();
        System.out.println("Enter the number of bus parking spots");
        nrBusP = scanner.nextInt();
        System.out.println("Enter the number of semi parking spots");
        nrSemiP = scanner.nextInt();
    }

    // Run the main app
    public void run(Map<String, ArrayList<Vehicle>> threadMap) {

        String url = "jdbc:mysql://localhost:3306/logging";
        String user = "root";
        String pw = "df1802";

        String path = "log.csv";

        try {
            Connection connection = DriverManager.getConnection(url, user, pw);
            Statement statement = connection.createStatement();
            statement.executeUpdate("TRUNCATE log");


            Thread commonGate1 = new GateThread("Common1", threadMap.get("commonGate1"), parkingSpots);
            Thread commonGate2 = new GateThread("Common2", threadMap.get("commonGate2"), parkingSpots);
            Thread commonGate3 = new GateThread("Common3", threadMap.get("commonGate3"), parkingSpots);
            Thread bigGate = new GateThread("Big", threadMap.get("bigGate"), parkingSpots);

            System.out.println("Parking lot is now open");
            statement.executeUpdate("INSERT INTO log (logtext) VALUES (\'Parking lot is now open\')");


            commonGate1.start();
            commonGate2.start();
            commonGate3.start();
            bigGate.start();

            commonGate1.join();
            commonGate2.join();
            commonGate3.join();
            bigGate.join();


            Scanner scanner = new Scanner(System.in);
            String response;
            System.out.println("Parking lot is now closed.");
            statement.executeUpdate("INSERT INTO log (logtext) VALUES (\'Parking lot is now closed.\')");
            System.out.println("\nWould you like to export the log? (Y/N)");
            response = scanner.nextLine();

            if (response.equals("y") || response.equals("Y")) {
                System.out.println("Log exported successfully.");
                ResultSet result = statement.executeQuery("SELECT * FROM log");
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(path));

                fileWriter.write("id,logtext");

                while (result.next()) {
                    int id = result.getInt("id");
                    String logtext = result.getString("logtext");

                    String line = String.format("%d,%s", id, logtext);

                    fileWriter.newLine();
                    fileWriter.write(line);
                }

                statement.close();
                fileWriter.close();
            }
            if (response.equals("n") || response.equals("N")) {
                System.out.printf("Closing the program.");
            }


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
