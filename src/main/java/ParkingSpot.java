package main.java;

public class ParkingSpot implements hasType, hasNumber{
    static int counter = 0;
    private final int number;
    private boolean free = true;
    private boolean light = false;
    private final String type;

    // Constructor
    public ParkingSpot(String type) {
        this.type = type;
        counter++;
        this.number = counter;
    }

    // Getters

    public boolean isFree() {
        return free;
    }

    public boolean isLightOn() {
        return light;
    }

    public int getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    // Setters

    public void lightOn() {
        light = true;
    }

    public void lightOff() {
        light = false;
    }

    public void occupy() {
        free = false;
    }

    public void free() {
        free = true;
    }
}
