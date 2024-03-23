/*
Scans up and down the island until it finds the emergency site and at least one creek
 */
package ca.mcmaster.se2aa4.island.team211.controlcentre;

import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class GridSearch extends PhaseOneCommonDecisions {

    private static final String right = "RIGHT";
    private static final String left = "LEFT";

    public GridSearch(Drone drone, String lastTurn) {
        setDrone(drone);
        setFlyToGround(false);
        setTurned(false);
        setTurnCount(0);
        setLastAction(Action.fly);
        setLastTurn(lastTurn);
    }

    /*Algorithm for searching the island
          1. while |Sites| != 1
          2.    while not at edge of island do
          3.        go straight across until reach ocean -> check if land in front
          4.        if so -> travel to it ->  repeat 3.
          5.        if no -> U-turn right if last turn was left, or vise verse -> go straight until reach ocean -> check for land ->
          6.            if so -> repeat 4.
          7.            if no -> U-turn left if last turn was right, or vise versa -> go straight until reach ocean -> check for land ->
          8.                if so -> repeat 4
          9.                if no -> repeat from 5
          10. if last turn was right -> turn right again -> repeat from 3
          11. if last turn was left -> turn left again -> repeat from 3
        */

    @Override
    public JSONObject makeDecision() {
        //change DecisionMaker conditions
        if (!drone.emergencySites.isEmpty() && !drone.creeks.isEmpty()) {
            drone.decisionMaker = new CreekFinder(drone, lastTurn);
            return super.flyForward();
        }

        //stop conditions
        if (drone.battery.batteryLevel < 1000 || drone.y_cord == 0) { return super.stop(); }

        //determine next action to perform based off last action
        return switch (lastAction) {
            case null -> super.scanPosition();
            case fly, heading -> super.scanPosition();// lastAction := scan
            case echo -> echoCase();
            case scan -> scanCase();
            case RE_ALIGN -> super.reAlign();
            case U_TURN -> uTurnCase();
            case U_TURN_2 -> uTurn2Case();
            default -> null;
        };
    }

    private JSONObject echoCase(){
        //if found ground fly to it
        if (super.foundGround(drone)) {
            setTurned(false);
            return super.flyToGround(); // lastAction := fly
        } //if didn't find ground, but just turned, then reAlign position
        else {
            if (turned) {
                setTurned(false);
                return super.reAlign(); // shifts position
            } else { // if didn't find ground, and didn't just turn, then turn
                if (Objects.equals(lastTurn, right)) {
                    turnDirection = right;
                }
                else {
                    turnDirection = left;
                }
                turnCount = 0;
                if (drone.radar.range <= 3 && Objects.equals(drone.radar.found, "OUT_OF_RANGE")) {
                    return super.uTurn2();
                } else {
                    return super.uTurn();
                }
            }
        }
    }

    private JSONObject scanCase() {
        if (super.overOcean()) {
            if (flyToGround) {
                return super.flyToGround();  // lastAction := fly
            }
            else {
                return super.echoAhead();  // lastAction := echo
            }
        } else {
            flyToGround = false;
            return super.flyForward(); // lastAction := fly
        }
    }

    private JSONObject uTurnCase(){
        if (turnCount < 6) {
            return super.uTurn();  //lastAction := uTurn
        }
        else {
            turnCount = 0;
            return super.echoAhead(); // lastAction := echo
        }
    }

    private JSONObject uTurn2Case(){
        if (turnCount < 6) {
            return super.uTurn2(); //lastAction := uTurn
        }
        else {
            turnCount = 0;
            return super.echoAhead(); // lastAction := echo
        }
    }
}