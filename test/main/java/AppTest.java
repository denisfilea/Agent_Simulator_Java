package main.java;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class AppTest {
    @Test
    void noOfVehiclesTest(){
        App app = new App();
        app.setNrCarP(5);
        app.setNrMotoP(5);
        app.setNrBusP(5);
        app.setNrSemiP(5);
        app.generateVehicles();
        assertEquals(40, app.getVehicleListSize());
    }
}