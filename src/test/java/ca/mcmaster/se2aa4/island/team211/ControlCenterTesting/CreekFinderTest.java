package ca.mcmaster.se2aa4.island.team211.ControlCenterTesting;

import ca.mcmaster.se2aa4.island.team211.controlcentre.Action;
import ca.mcmaster.se2aa4.island.team211.controlcentre.CreekFinder;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import ca.mcmaster.se2aa4.island.team211.locations.Coordinate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class CreekFinderTest  {

    private Drone drone;
    private CreekFinder creekFinder;

    void setUp(){
        drone = new Drone();
        this.drone.x_cord = 6;
        this.drone.y_cord = 12;
        this.drone.direction = "N";
        setUpFakeCreeks();
        creekFinder = new CreekFinder(drone, "RIGHT");
        drone.decisionMaker = creekFinder;
    }

    void setUpFakeCreeks(){
        Coordinate coord1 = new Coordinate(15, 20);
        Coordinate coord2 = new Coordinate(10, 16);
        Coordinate coord3 = new Coordinate(25, 7);
        this.drone.creeks.put("testSite1", coord1);
        this.drone.creeks.put("testSite2", coord2);
        this.drone.emergencySites.put("emergencySiteTest1", coord3);
    }

    @Test
    void testCreekFinderOutOfRange() {
        setUp();
        this.creekFinder.outOfRadiusPermanent = true;
        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "stop");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderLowBattery(){
        setUp();
        drone.battery.batteryLevel = 900;

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "stop");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionFly(){
        setUp();
        drone.battery.batteryLevel = 2000;
        creekFinder.setLastAction(Action.fly);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "scan");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionHeading(){
        setUp();
        drone.battery.batteryLevel = 2000;
        creekFinder.setLastAction(Action.heading);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "scan");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionNull(){
        setUp();
        drone.battery.batteryLevel = 2000;
        //creekFinder.setLastAction(Action.heading);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "scan");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionEchoScanOutOfRange(){
        setUp();
        drone.battery.batteryLevel = 2000;
        creekFinder.setLastAction(Action.scan);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionEchoScanOverOcean(){
        setUp();
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        drone.currentBiomes = new JSONArray();
        drone.currentBiomes.put("OCEAN");
        creekFinder.setFlyToGround(false);
        creekFinder.setLastAction(Action.scan);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.direction);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "echo").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionEchoScanNOTOverOcean(){
        setUp();
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        drone.currentBiomes = new JSONArray();
        drone.currentBiomes.put("BEACH");
        creekFinder.setFlyToGround(false);
        creekFinder.setLastAction(Action.scan);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "fly");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionEchoFlyToGround(){
        setUp();
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        drone.radar.found = "GROUND";
        creekFinder.setLastAction(Action.echo);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "fly");

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionEchoNotTurned(){
        setUp();
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        creekFinder.setTurned(false);
        creekFinder.setLastTurn("LEFT");
        drone.radar.range = 2;
        drone.radar.found = "OUT_OF_RANGE";
        creekFinder.setLastAction(Action.echo);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionEchoTurned(){
        setUp();
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        creekFinder.setTurned(true);
        creekFinder.setLastTurn("RIGHT");
        creekFinder.setLastAction(Action.echo);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionUTurn(){
        setUp();
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        creekFinder.setTurnDirection("RIGHT");
        creekFinder.setLastAction(Action.uTurn);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionUTurn2(){
        setUp();
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        creekFinder.setTurnDirection("LEFT");
        creekFinder.setLastAction(Action.uTurn2);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionReAlign(){
        setUp();
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        creekFinder.setTurnDirection("RIGHT");
        creekFinder.setLastAction(Action.reAlign);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "heading").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionReturnToRadius(){
        setUp();
        drone.battery.batteryLevel = 2000;
        drone.x_cord = 20;
        creekFinder.setTurnDirection("RIGHT");
        creekFinder.setLastAction(Action.returnToRadius);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.direction);
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "echo").put("parameters", parameter);

        assertEquals(expectedDecision.toString(), decision.toString());
    }

    @Test
    void testCreekFinderMakeDecisionReturnToRadiusOutOfRange(){
        setUp();
        drone.battery.batteryLevel = 2000;
        creekFinder.setTurnDirection("RIGHT");
        creekFinder.setLastAction(Action.returnToRadius);

        JSONObject decision = drone.decisionMaker.makeDecision();
        JSONObject expectedDecision = new JSONObject();
        expectedDecision.put("action", "fly");

        assertEquals(expectedDecision.toString(), decision.toString());
    }
}
