package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class GridSearch implements DecisionMaker{
    //if you echo over ground, it just finds "GROUND" with a range of 0

    public Action lastAction = null;

    public Drone drone;

    private String lastTurn = "RIGHT";
    private boolean turned = false;
    private boolean uTurning = false;

    private String turnDirection;
    private boolean flyToGround = false; //flag to see if the drone should be flying to a separate piece of land
    private Integer turnCount = 0;

    public GridSearch(Drone drone){
        this.drone = drone;
    }
    /*1. while |Sites| != 1
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

    //Algorithm for searching the island

    //breaking at left edge of island sometimes
    @Override
    public JSONObject makeDecision() {
        if (drone.emergSites.size() == 1) return stop(); //stop once 1 creek have been found
        if (drone.battery.batteryLevel < 100) return stop();

        switch (lastAction){
            case null: {
                return goInward(); // lastAction := heading
            }
            case fly, heading: {
                return scanPosition(); // lastAction := scan
            }
            case echo: {
                //if found ground fly to it
                if (foundGround()){
                    turned = false;
                    return flyToGround(); // lastAction := fly
                } //if didn't find ground, but just turned, then turn 1 more time and go forward
                else {
                    if (turned){
                        turned = false;
                        turnCount = 0;
                        return reAlign(); // shifts position
                    } else { // if didn't find ground, and didn't just turn, then turn
                        if (Objects.equals(lastTurn, "RIGHT")) {
                            turnDirection = drone.left;
                            return uTurn(turnDirection);
                        } // lastAction := heading
                        else if (Objects.equals(lastTurn, "LEFT")) {
                            turnDirection = drone.right;
                            return uTurn(turnDirection);
                        } // lastAction := heading
                        else {
                            lastTurn = "LEFT";
                            return turnRight();
                        }
                    }
                }
            }
            case scan: {
                if (uTurning){
                    if (Objects.equals(lastTurn, "RIGHT")) {
                        if (turnCount < 2) {return turnLeft(); // lastAction := heading
                        } else {lastTurn = "LEFT";} //update direction after completing uTurn
                    }
                    else if (Objects.equals(lastTurn, "LEFT")) {
                        if (turnCount < 2) {return turnRight(); // lastAction := heading
                        } else {lastTurn = "RIGHT";}
                    }
                    turnCount = 0;
                    return echoAhead(); // lastAction := echo
                }
                if (overOcean()){
                    if (flyToGround){return flyToGround();} // lastAction := fly
                    else return echoAhead(); // lastAction := echo
                } else {
                    flyToGround = false;
                    return flyForward(); // lastAction := fly
                }

            }
            case reAlign: {
                return reAlign();
            }
            case uTurn:{
                if (turnCount < 2) {
                    uTurn(turnDirection); //lastAction := uTurn
                } else {
                    echoAhead(); // lastAction := echo
                }
            }
            default: {return null;}
        }
    }

    private JSONObject reAlign() {
        lastAction = Action.reAlign;
        if (turnCount < 3) {
            if (Objects.equals(lastTurn, "RIGHT")) {
                turnCount++;
                return turnLeft();
            } else {
                turnCount++;
                return turnRight();
            }
        } else if (turnCount == 3){
            turnCount++;
            return flyForward();
        } else {
            if (Objects.equals(lastTurn, "RIGHT")) {
                lastAction = Action.heading;
                return turnLeft();
            } else {
                lastAction = Action.heading;
                return turnRight();
            }
        }
    }

    private JSONObject goInward() {return turnRight();} //assumes we mapped coast in a clockwise manner
    private boolean foundGround(){return Objects.equals(drone.radar.found, "GROUND");}
    private boolean overOcean(){return Objects.equals(drone.currentBiomes.get(0), "OCEAN");}

    //If the flyToGround flag is true, it alternates between flying forward and scanning until reaching OCEAN
    private JSONObject flyToGround() {
        flyToGround = true;
        return flyForward();
    }


    //Turns in the drone's right direction
    private JSONObject turnRight() {
        lastAction = Action.heading;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        drone.droneActions.turnRight(drone); //update direction of drone
        return sendDecision(lastAction, parameter);
    }

    //Turns in the drone's left direction
    private JSONObject turnLeft() {
        lastAction = Action.heading;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        drone.droneActions.turnLeft(drone); //update direction of drone
        return sendDecision(lastAction, parameter);
    }

    //flips the drones direction
    private JSONObject uTurn(String direction){
        lastAction = Action.uTurn;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", direction);
        turned = true;
        return sendDecision(Action.heading, parameter);
    }

    private JSONObject flyForward(){
        lastAction = Action.fly;
        drone.droneActions.forward(drone); //update position of drone
        return sendDecision(lastAction);
    }

    private JSONObject scanPosition(){
        lastAction = Action.scan;
        return sendDecision(lastAction);
    }

    private JSONObject echoAhead(){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.direction);
        return sendDecision(lastAction, parameter);
    }

    private JSONObject stop() {
        lastAction = Action.stop;
        return sendDecision(lastAction);
    }

    @Override
    public JSONObject sendDecision(Action action, JSONObject parameters){
        JSONObject decision = new JSONObject();
        decision.put("action", action).put("parameters", parameters);
        return decision;
    }

    public JSONObject sendDecision(Action action){
        JSONObject decision = new JSONObject();
        decision.put("action", action);
        return decision;
    }

    @Override
    public Action getLastAction() {
        return lastAction;
    }
}
