package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class GridSearch implements DecisionMaker{

    public Drone drone;
    public Action lastAction = null;
    private boolean turned = false; //indicates whether a turn was just made or not
    private String lastTurn; //Used to determine next turn
    private String turnDirection; //The current turn being made
    private boolean flyToGround = false; //flag to see if the drone should be flying to a separate piece of land
    private Integer turnCount = 0; //used for determining how many more turns need to be made
    private Integer testCount = 0;
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

    @Override
    public JSONObject makeDecision() {
        testCount++;
        if(testCount > 100) {
            return stop();
        }
        if (drone.emergSites.size() == 1 && drone.creeks.size() == 7) return stop(); //stop once 1 creek have been found
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
                        return reAlign(); // shifts position
                    } else { // if didn't find ground, and didn't just turn, then turn
                        if (Objects.equals(lastTurn, "RIGHT")) {
                            turnDirection = "LEFT";
                        } // lastAction := heading
                        else{
                            turnDirection = "RIGHT";
                        }
                        turnCount = 0;
                        return uTurn();
                    }
                }
            }
            case scan: {
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
                if (turnCount < 5) {
                    return uTurn(); //lastAction := uTurn
                } else {
                    turnCount = 0;
                    return echoAhead(); // lastAction := echo
                }
            }
            default: {return null;}
        }
    }
    /*
    Weird U-Turn to make it go row by row instead of being offset by one
        1. If last turn was right and turn count is 0 then turn right
        2. If turn count is 1 and do opposite of last turn
        3. If turn count is 2 then do same turn
        4. if turn count is 3 fly forward
        5. if turn count is 5 then turn same again.
     */
    //flips the drones direction
    private JSONObject uTurn(){
        lastAction = Action.uTurn;
        turned = true;
        switch (turnCount){
            case 0 -> {
                turnCount++;
                if (Objects.equals(turnDirection, "LEFT")) {
                    lastTurn = "RIGHT";
                    return turnRight();
                } else {
                    lastTurn = "LEFT";
                    return turnLeft();
                }
            }
            case 1, 2, 4 -> {
                turnCount++;
                if (Objects.equals(turnDirection, "LEFT")) {
                    lastTurn = "LEFT";
                    return turnLeft();
                } else {
                    lastTurn = "RIGHT";
                    return turnRight();
                }
            }
            case 3 -> {
                turnCount++;
                return flyForward();
            }
        }



        turnCount++;
        if (Objects.equals(turnDirection, "LEFT")){
            lastTurn = "LEFT";
            return turnLeft();
        } else {
            lastTurn = "RIGHT";
            return turnRight();
        }
    }

    private JSONObject reAlign() {
        lastAction = Action.reAlign;
        if (turnCount == 0) {
            if (Objects.equals(lastTurn, "RIGHT")) {
                turnCount++;
                return turnRight();
            } else {
                turnCount++;
                return turnLeft();
            }
        } else if (turnCount == 1){
            turnCount++;
            return flyForward();
        } else if (turnCount == 2) {
            if (Objects.equals(lastTurn, "RIGHT")) {
                turnCount = 0;
                lastAction = Action.heading;
                lastTurn = "LEFT";
                return turnLeft();
            } else {
                turnCount = 0;
                lastAction = Action.heading;
                lastTurn = "RIGHT";
                return turnRight();
            }
        }
        else {
            return echoAhead();
        }
    }

    //at the end of IslandFinder it echo's to the left of the drone,
    // this is how it determines which direction to go at the start of GridSearch
    private JSONObject goInward() {
        if (foundGround()){
            lastTurn = "LEFT";
            return turnLeft();
        } else{
            lastTurn = "RIGHT";
            return turnRight();
        }
    }


    //If the flyToGround flag is true, it alternates between flying forward and scanning until reaching OCEAN
    private JSONObject flyToGround() {
        flyToGround = true;
        return flyForward();
    }

    //Turns in the drone's right direction
    private JSONObject turnRight() {
        if (!(Objects.equals(lastAction,Action.uTurn) || (Objects.equals(lastAction,Action.reAlign)))){lastAction = Action.heading;}
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        drone.droneActions.turnRight(drone); //update direction of drone
        return sendDecision(Action.heading, parameter);
    }

    //Turns in the drone's left direction
    private JSONObject turnLeft() {
        if (!(Objects.equals(lastAction,Action.uTurn) || (Objects.equals(lastAction,Action.reAlign)))){lastAction = Action.heading;}
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        drone.droneActions.turnLeft(drone); //update direction of drone
        return sendDecision(Action.heading, parameter);
    }


    private JSONObject flyForward(){
        if (!Objects.equals(lastAction,Action.reAlign)){lastAction = Action.fly;}
        drone.droneActions.forward(drone); //update position of drone
        return sendDecision(Action.fly);
    }

    private JSONObject scanPosition(){
        if (!Objects.equals(lastAction,Action.reAlign)){lastAction = Action.scan;}
        return sendDecision(Action.scan);
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

    private boolean foundGround(){return Objects.equals(drone.radar.found, "GROUND");}
    private boolean overOcean(){
        for (int i = 0; i < drone.currentBiomes.length(); i++) {
            if (!drone.currentBiomes.get(i).equals("OCEAN")) {
                return false;
            }
        }
        return true;
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
