package main.java;

import java.util.Random;

public class Vehicle implements hasType, hasNumber{
    private int heading;
    private int parkingNr;
    private boolean parked = false;
    private final int parkingTime;
    private int elapsedTime = 0;
    private final int timeTaken;
    private boolean moving;
    private final String type;
    private boolean gate = true;
    private int inPark = 0;
    private boolean waiting = false;

    public Vehicle(String type){
        this.parkingNr = 0;
        this.type = type;
        Random random = new Random();
        parkingTime = random.nextInt(25,40);
        timeTaken = random.nextInt(3,15);
    }

    public String getType(){
        return type;
    }

    public int timeTaken(){
        return timeTaken;
    }

    public int parkingTime(){
        return parkingTime;
    }

    public void setParkingNr(int number){
        parkingNr = number;
    }

    public void park(){
        parked = true;
    }

    public void onePlus(){
        elapsedTime++;
    }

    public int getElapsedTime(){
        return elapsedTime;
    }
    public boolean isParked() {
        return parked;
    }

    public boolean isMoving(){
        return moving;
    }
    public void start(){
        moving = true;
    }

    public void stop(){
        moving = false;
    }

    public void resetElapsed(){
        this.elapsedTime = 0;
    }

    public void setHeading(int heading){
        this.heading = heading;
    }

    public int getHeading(){
        return heading;
    }

    public int getParkingNr(){
        return parkingNr;
    }

    public int getNumber(){
        return parkingNr;
    }

    public void depart(){
        gate = false;
    }

    public boolean isAtGate(){
        return gate;
    }

    public void onePlusPark(){
        inPark++;
    }

    public int getParkingTime(){
        return inPark;
    }

    public void carWait(){
        waiting = true;
    }
    public void dontWait(){
        waiting = false;
    }
    public boolean getWait(){
        return waiting;
    }

}
