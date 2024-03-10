package ca.mcmaster.se2aa4.island.team211.DroneTesting;

import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DroneTest {

    private Drone drone;

    void setUp(String direction){
        drone = new Drone();
        this.drone.direction = direction;
        this.drone.initialDirection = drone.direction;
        this.drone.x_cord = 0;
        this.drone.y_cord = 0;
    }

    @Test
    void testTurnRightEast() {
        setUp("E");
        drone.droneActions.turnRight(drone);

        assertEquals(1, drone.x_cord);
        assertEquals(1, drone.y_cord);
        assertEquals("S", drone.direction);
    }

    @Test
    void testTurnRightWest() {
        setUp("W");
        drone.droneActions.turnRight(drone);

        assertEquals(-1, drone.x_cord);
        assertEquals(-1, drone.y_cord);
        assertEquals("N", drone.direction);
    }

    @Test
    void testTurnRightNorth() {
        setUp("N");
        drone.droneActions.turnRight(drone);

        assertEquals(1, drone.x_cord);
        assertEquals(-1, drone.y_cord);
        assertEquals("E", drone.direction);
    }

    @Test
    void testTurnRightSouth() {
        setUp("S");
        drone.droneActions.turnRight(drone);

        assertEquals(-1, drone.x_cord);
        assertEquals(1, drone.y_cord);
        assertEquals("W", drone.direction);
    }

    @Test
    void testTurnLeftEast() {
        setUp("E");
        drone.droneActions.turnLeft(drone);

        assertEquals(1, drone.x_cord);
        assertEquals(-1, drone.y_cord);
        assertEquals("N", drone.direction);
    }

    @Test
    void testTurnLeftWest() {
        setUp("W");
        drone.droneActions.turnLeft(drone);

        assertEquals(-1, drone.x_cord);
        assertEquals(1, drone.y_cord);
        assertEquals("S", drone.direction);
    }

    @Test
    void testTurnLeftNorth() {
        setUp("N");
        drone.droneActions.turnLeft(drone);

        assertEquals(-1, drone.x_cord);
        assertEquals(-1, drone.y_cord);
        assertEquals("W", drone.direction);
    }

    @Test
    void testTurnLeftSouth() {
        setUp("S");
        drone.droneActions.turnLeft(drone);

        assertEquals(1, drone.x_cord);
        assertEquals(1, drone.y_cord);
        assertEquals("E", drone.direction);
    }

    @Test
    void testForwardNorth(){
        setUp("N");
        drone.droneActions.forward(drone);

        assertEquals(0, drone.x_cord);
        assertEquals(-1, drone.y_cord);
        assertEquals("N", drone.direction);
    }

    @Test
    void testForwardSouth(){
        setUp("S");
        drone.droneActions.forward(drone);

        assertEquals(0, drone.x_cord);
        assertEquals(1, drone.y_cord);
        assertEquals("S", drone.direction);
    }

    @Test
    void testForwardEast(){
        setUp("E");
        drone.droneActions.forward(drone);

        assertEquals(1, drone.x_cord);
        assertEquals(0, drone.y_cord);
        assertEquals("E", drone.direction);
    }

    @Test
    void testForwardWest(){
        setUp("W");
        drone.droneActions.forward(drone);

        assertEquals(-1, drone.x_cord);
        assertEquals(0, drone.y_cord);
        assertEquals("W", drone.direction);
    }
}
