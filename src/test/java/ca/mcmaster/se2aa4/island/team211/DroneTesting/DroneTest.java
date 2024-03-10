package ca.mcmaster.se2aa4.island.team211.DroneTesting;

import ca.mcmaster.se2aa4.island.team211.controlcentre.FindStart;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import ca.mcmaster.se2aa4.island.team211.drone.DroneActions;
import ca.mcmaster.se2aa4.island.team211.drone.Radar;
import ca.mcmaster.se2aa4.island.team211.locations.Coordinate;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.notification.RunListener;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DroneTest {

    private Drone drone;

    void setUpDrone(String direction){
        drone = new Drone();
        this.drone.direction = direction;
        this.drone.initialDirection = drone.direction;
        this.drone.x_cord = 0;
        this.drone.y_cord = 0;
        this.drone.decisionMaker = new FindStart(drone);
    }

    void setUpRadar(Drone drone, boolean setFound){
        if(setFound){
            this.drone.radar.found = true;
            this.drone.radar.range = 10;
        } else {
            this.drone.radar.found = false;
        }
    }

    @Test
    void testTurnRightEast() {
        setUpDrone("E");
        drone.droneActions.turnRight(drone);

        assertEquals(1, drone.x_cord);
        assertEquals(1, drone.y_cord);
        assertEquals("S", drone.direction);
    }

    @Test
    void testTurnRightWest() {
        setUpDrone("W");
        drone.droneActions.turnRight(drone);

        assertEquals(-1, drone.x_cord);
        assertEquals(-1, drone.y_cord);
        assertEquals("N", drone.direction);
    }

    @Test
    void testTurnRightNorth() {
        setUpDrone("N");
        drone.droneActions.turnRight(drone);

        assertEquals(1, drone.x_cord);
        assertEquals(-1, drone.y_cord);
        assertEquals("E", drone.direction);
    }

    @Test
    void testTurnRightSouth() {
        setUpDrone("S");
        drone.droneActions.turnRight(drone);

        assertEquals(-1, drone.x_cord);
        assertEquals(1, drone.y_cord);
        assertEquals("W", drone.direction);
    }

    @Test
    void testTurnLeftEast() {
        setUpDrone("E");
        drone.droneActions.turnLeft(drone);

        assertEquals(1, drone.x_cord);
        assertEquals(-1, drone.y_cord);
        assertEquals("N", drone.direction);
    }

    @Test
    void testTurnLeftWest() {
        setUpDrone("W");
        drone.droneActions.turnLeft(drone);

        assertEquals(-1, drone.x_cord);
        assertEquals(1, drone.y_cord);
        assertEquals("S", drone.direction);
    }

    @Test
    void testTurnLeftNorth() {
        setUpDrone("N");
        drone.droneActions.turnLeft(drone);

        assertEquals(-1, drone.x_cord);
        assertEquals(-1, drone.y_cord);
        assertEquals("W", drone.direction);
    }

    @Test
    void testTurnLeftSouth() {
        setUpDrone("S");
        drone.droneActions.turnLeft(drone);

        assertEquals(1, drone.x_cord);
        assertEquals(1, drone.y_cord);
        assertEquals("E", drone.direction);
    }

    @Test
    void testForwardNorth(){
        setUpDrone("N");
        drone.droneActions.forward(drone);

        assertEquals(0, drone.x_cord);
        assertEquals(-1, drone.y_cord);
        assertEquals("N", drone.direction);
    }

    @Test
    void testForwardSouth(){
        setUpDrone("S");
        drone.droneActions.forward(drone);

        assertEquals(0, drone.x_cord);
        assertEquals(1, drone.y_cord);
        assertEquals("S", drone.direction);
    }

    @Test
    void testForwardEast(){
        setUpDrone("E");
        drone.droneActions.forward(drone);

        assertEquals(1, drone.x_cord);
        assertEquals(0, drone.y_cord);
        assertEquals("E", drone.direction);
    }

    @Test
    void testForwardWest(){
        setUpDrone("W");
        drone.droneActions.forward(drone);

        assertEquals(-1, drone.x_cord);
        assertEquals(0, drone.y_cord);
        assertEquals("W", drone.direction);
    }

    @Test
    void testGetSidesNorth(){
        setUpDrone("N");
        drone.droneActions.getSides(drone);

        assertEquals("W", drone.left);
        assertEquals("E", drone.right);
    }

    @Test
    void testGetSidesEast(){
        setUpDrone("E");
        drone.droneActions.getSides(drone);

        assertEquals("N", drone.left);
        assertEquals("S", drone.right);
    }

    @Test
    void testGetSidesSouth(){
        setUpDrone("S");
        drone.droneActions.getSides(drone);

        assertEquals("E", drone.left);
        assertEquals("W", drone.right);
    }

    @Test
    void testGetSidesWest(){
        setUpDrone("W");
        drone.droneActions.getSides(drone);

        assertEquals("S", drone.left);
        assertEquals("N", drone.right);
    }


    @Test
    void testPrintCoords(){
        setUpDrone("N");
        drone.x_cord = 5;
        drone.y_cord = 10;

        Integer[] coords = drone.droneActions.printCoords(drone);

        Assertions.assertNotNull(coords);
        assertEquals(2, coords.length);
        assertEquals(Integer.valueOf(5), coords[0]);
        assertEquals(Integer.valueOf(10), coords[1]);
    }

    @Test
    void testSetStartNorthSouth(){
        setUpDrone("N");
        setUpRadar(drone, true);

        drone.droneActions.setStart(drone);
        int expected_X = drone.radar.range + 1;
        assertEquals(expected_X, drone.x_cord);
        assertEquals(1, drone.y_cord);
    }

    @Test
    void testSetStartEastWest(){
        setUpDrone("E");
        setUpRadar(drone, true);

        drone.droneActions.setStart(drone);
        int expected_Y = drone.radar.range + 1;
        assertEquals(1, drone.x_cord);
        assertEquals(expected_Y, drone.y_cord);
    }

    @Test
    void testGetCoordinates(){
        setUpDrone("N");

        Coordinate coords = DroneActions.getCordinates(drone);

        assertEquals(coords.xCoordinate, drone.x_cord);
        assertEquals(coords.yCoordinate, drone.y_cord);
    }
    
    @Test
    void testGetDecision(){
        setUpDrone("N");

        JSONObject expectedDecision = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put("direction", "W");
        expectedDecision.put("action", "echo").put("parameters", parameters);
        
        JSONObject decision = drone.droneActions.getDecision(drone);
        assertEquals(expectedDecision.toString(), decision.toString());
    }
}
