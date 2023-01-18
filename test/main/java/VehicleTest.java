package main.java;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    Vehicle car = new Vehicle("Car");
    @Test
    void parking(){
        assertFalse(car.isParked());
        car.park();
        assertTrue(car.isParked());
    }

    @Test
    void resetTime(){
        car.onePlus();
        assertEquals(1,car.getElapsedTime());
        car.resetElapsed();
        assertEquals(0,car.getElapsedTime());
    }


}