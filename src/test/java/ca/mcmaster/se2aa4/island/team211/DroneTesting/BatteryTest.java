package ca.mcmaster.se2aa4.island.team211.DroneTesting;

import ca.mcmaster.se2aa4.island.team211.drone.Battery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BatteryTest {

    @Test
    public void TestDischarge() {
        Battery battery = new Battery();
        battery.batteryLevel = 100;
        battery.discharge(5);

        Assertions.assertEquals(95, battery.batteryLevel);

        battery.batteryLevel = 1;
        battery.discharge(5);

        Assertions.assertEquals(0, battery.batteryLevel);

    }
}
