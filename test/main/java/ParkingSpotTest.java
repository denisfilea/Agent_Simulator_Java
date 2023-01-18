package main.java;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSpotTest {

    ParkingSpot parkingSpot = new ParkingSpot("Bus");

    @Test
    void lightTest(){
        assertFalse(parkingSpot.isLightOn());
        parkingSpot.lightOn();
        assertTrue(parkingSpot.isLightOn());
    }

    @Test
    void isFreeTest(){
        assertTrue(parkingSpot.isFree());
        parkingSpot.occupy();
        assertFalse(parkingSpot.isFree());
    }

}