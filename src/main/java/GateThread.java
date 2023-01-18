package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

public class GateThread extends Thread implements hasType{
    private String type;
    private int nr;
    private List<Vehicle> vehicles;
    private List<ParkingSpot> parkingSpots;

    public GateThread(String type, List<Vehicle> vehicles, List<ParkingSpot> parkingSpots) {
        this.parkingSpots = parkingSpots;
        this.vehicles = vehicles;
        this.type = type;
        if (type.equals("Common1")) {
            this.nr = 0;
        }
        if (type.equals("Common2")) {
            this.nr = 1;
        }
        if (type.equals("Common3")) {
            this.nr = 2;
        }
        if (type.equals("Big")) {
            this.nr = 3;
        }
    }

    public String getType(){
        return type;
    }


    @Override
    public void run() {
        int elapsedTime = 0;
        int m = vehicles.size();

        String url = "jdbc:mysql://localhost:3306/logging";
        String user = "root";
        String pw = "df1802";
        String qu;
        try {
            Connection connection = DriverManager.getConnection(url, user, pw);
            Statement statement = connection.createStatement();

            while (!vehicles.isEmpty() && m > 0) {
                for (Iterator<Vehicle> iterator = vehicles.iterator(); iterator.hasNext(); ) {
                    Vehicle vehicle = iterator.next();
                    if (!vehicle.isParked() && !vehicle.isMoving() && vehicle.isAtGate()) {
                        int freeSpot = -1;
                        for (int i = nr + 1; i <= parkingSpots.size() - 4; i += 4) {
                            if (parkingSpots.get(i).isFree() && vehicle.getType().equals(parkingSpots.get(i).getType())) {
                                freeSpot = i;
                                parkingSpots.get(freeSpot).lightOn();
                                parkingSpots.get(freeSpot).occupy();
                                System.out.println(type + ": " + vehicle.getType() + ": Parking spot number " + freeSpot + " is now lit up.");
                                qu = "INSERT INTO log (logtext) VALUES (\'" + type + ": " + vehicle.getType() + ": Parking spot number " + freeSpot + " is now lit up.\')";
                                statement.executeUpdate(qu);
                                vehicle.start();
                                vehicle.onePlus();
                                vehicle.setHeading(freeSpot);
                                vehicle.depart();
                                vehicle.dontWait();
                                elapsedTime++;
                                break;
                            }
                        }
                        if (freeSpot == -1 && !vehicle.getWait()) {
                            System.out.println(type + ": " + vehicle.getType() + ": No parking spots available. Waiting for some to free up...");
                            qu = "INSERT INTO log (logtext) VALUES (\'" + type + ": " + vehicle.getType() + ": No parking spots available. Waiting for some to free up...\')";
                            statement.executeUpdate(qu);
                            vehicle.carWait();
                        }
                    }
                    if (vehicle.isMoving()) {
                        if (vehicle.getElapsedTime() <= 10 && vehicle.getElapsedTime() != vehicle.timeTaken()) {
                            vehicle.onePlus();
                        }
                        if (vehicle.getElapsedTime() > 6 && vehicle.getElapsedTime() < vehicle.timeTaken()) {
                            System.out.println(type + ": " + vehicle.getType() + ": A vehicle has failed to reach the designated parking spot, looking for another one...");
                            qu = "INSERT INTO log (logtext) VALUES (\'" + type + ": " + vehicle.getType() + ": A vehicle has failed to reach the designated parking spot, looking for another one...')";
                            statement.executeUpdate(qu);
                            if (!vehicle.getWait())
                                parkingSpots.get(vehicle.getHeading()).free();
                            int freeSpot = 0;
                            for (int i = nr + 1; i <= parkingSpots.size() - 4; i += 4) {
                                if (parkingSpots.get(i).isFree() && vehicle.getType().equals(parkingSpots.get(i).getType())) {
                                    freeSpot = i;
                                    parkingSpots.get(freeSpot).occupy();
                                    System.out.println(type + ": " + vehicle.getType() + ": is now heading towards parking spot number " + freeSpot + ".");
                                    qu = "INSERT INTO log (logtext) VALUES (\'" + type + ": " + vehicle.getType() + ": is now heading towards parking spot number " + freeSpot + ".\')";
                                    statement.executeUpdate(qu);
                                    vehicle.onePlus();
                                    vehicle.setHeading(freeSpot);
                                    vehicle.dontWait();
                                    elapsedTime++;
                                    break;
                                }
                            }
                            if (freeSpot == 0 && !vehicle.getWait()) {
                                System.out.println(type + ": " + vehicle.getType() + ": Still no parking spots available...");
                                qu = "INSERT INTO log (logtext) VALUES (\'" + type + ": " + vehicle.getType() + ": Still no parking spots available...\')";
                                statement.executeUpdate(qu);
                            }
                        }
                        if (vehicle.getElapsedTime() > vehicle.timeTaken()) {
                            iterator.remove();
                        }
                        if (vehicle.getElapsedTime() == vehicle.timeTaken()) {
                            vehicle.park();
                            vehicle.stop();
                            vehicle.resetElapsed();
                            vehicle.setParkingNr(vehicle.getHeading());
                            vehicle.onePlus();
                            elapsedTime++;

                            System.out.println(type + ": " + vehicle.getType() + ": Vehicle has reached parking spot number " + vehicle.getHeading() + " and parked.");
                            qu = "INSERT INTO log (logtext) VALUES (\'" + type + ": " + vehicle.getType() + ": Vehicle has reached parking spot number " + vehicle.getHeading() + " and parked.\')";
                            statement.executeUpdate(qu);
                        }
                    }
                    if (vehicle.isParked()) {
                        if (vehicle.getElapsedTime() < vehicle.parkingTime()) {
                            vehicle.onePlusPark();
                            elapsedTime++;
                        }
                        if (vehicle.getParkingTime() == vehicle.parkingTime()) {
                            iterator.remove();
                            elapsedTime++;
                            System.out.println(type + ": " + vehicle.getType() + ": Vehicle on parking spot number " + vehicle.getParkingNr() + " left.");
                            qu = "INSERT INTO log (logtext) VALUES (\'" + type + ": " + vehicle.getType() + ": Vehicle on parking spot number " + vehicle.getParkingNr() + " left.\')";
                            statement.executeUpdate(qu);
                            parkingSpots.get(vehicle.getParkingNr()).free();
                            m--;
                        }
                    }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
                }
            }
            System.out.println(type + ": Finished service.");
            qu = "INSERT INTO log (logtext) VALUES (\'" + type + ": Finished service.\')";
            statement.executeUpdate(qu);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}


