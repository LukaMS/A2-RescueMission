package ca.mcmaster.se2aa4.island.team211.ControlCenterTesting;

import ca.mcmaster.se2aa4.island.team211.controlcentre.Action;
import ca.mcmaster.se2aa4.island.team211.controlcentre.IslandFinder;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class IslandFinderTest  {


    @Test
    public void testInitialIslandFinderState() {
        Drone mockDrone = new Drone();
        IslandFinder islandFinder = new IslandFinder(mockDrone);

        // Test initial state
        assertEquals(0, islandFinder.getTurnCount());
        assertFalse(islandFinder.isFlyToGround());
        assertEquals(mockDrone, islandFinder.getDrone());
        assertEquals("RIGHT", islandFinder.getLastEchoDirection());
    }

    @Test
    public void testMakeDecision() {
        Drone mockDrone = new Drone();
        IslandFinder islandFinder = new IslandFinder(mockDrone);

        // Test cases for each possible last action
        islandFinder.setLastAction(null);
        islandFinder.makeDecision();
        assertEquals("RIGHT", islandFinder.getLastEchoDirection());
        assertEquals(Action.echo, islandFinder.getLastAction());
        JSONObject

        islandFinder.setLastAction(Action.echo);

        islandFinder.setLastAction(Action.fly);

        islandFinder.setLastAction(Action.scan);

        islandFinder.setLastAction(Action.heading);

        islandFinder.setLastAction(Action.reAlign);

        islandFinder.setLastAction(Action.uTurn); //goes to default








        // You may need to mock certain behavior of the drone
        // and ensure that the decision returned by makeDecision() is as expected
        // For brevity, I'll leave the actual implementation of test cases for you to complete
    }

    @Test
    public void testAdjustHeading() {
        // Create a drone instance (you might need a mock object)
        Drone mockDrone = new Drone();
        IslandFinder islandFinder = new IslandFinder(mockDrone);

        // Test boundary cases for adjustHeading()
        // Ensure that the turnCount is correctly incremented and actions are returned as expected
        // For brevity, I'll leave the actual implementation of test cases for you to complete
    }

    // Add more test cases as needed
}
