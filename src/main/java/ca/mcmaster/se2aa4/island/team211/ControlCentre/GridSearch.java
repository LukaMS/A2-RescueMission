package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class GridSearch implements DecisionMaker{
    //if you echo over ground, it just finds "GROUND" with a range of 0

    public Action lastAction = null;

    public Drone drone;

    private String lastTurn;
    private boolean turned = false;
    private boolean flyToGround = false; //flag to see if the drone should be flying to a separate piece of land
    private Integer numberOfTurns = 0;

    public GridSearch(Drone drone, String lastTurn){
        this.drone = drone;
        this.lastTurn = lastTurn;
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
                return scanPosition(); // lastAction := scan
            }
            case fly: {
                return scanPosition(); // lastAction := scan
            }
            case echo: {
                if (foundGround()){
                    turned = false;
                    return flyToGround(); // lastAction := fly
                }
                else {
                    if (turned){
                        turned = false;
                        if (Objects.equals(lastTurn, "RIGHT")){
                            numberOfTurns = 1;
                            return uTurnRight(); // lastAction := heading
                        }else if (Objects.equals(lastTurn, "LEFT")) {
                            numberOfTurns = 1;
                            return uTurnLeft();} // lastAction := heading
                    } else {
                        if (Objects.equals(lastTurn, "RIGHT")) {return uTurnLeft();} // lastAction := heading
                        else if (Objects.equals(lastTurn, "LEFT")) {return uTurnRight();} // lastAction := heading
                        else {
                            lastTurn = "LEFT";
                            return uTurnRight();
                        }
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
            case heading:{

                if (Objects.equals(lastTurn, "RIGHT")) {
                    if (numberOfTurns < 2) {return uTurnLeft(); // lastAction := heading
                    } else {lastTurn = "LEFT";} //update direction after completing uTurn
                }
                else if (Objects.equals(lastTurn, "LEFT")) {
                    if (numberOfTurns < 2) {return uTurnRight(); // lastAction := heading
                    } else {lastTurn = "RIGHT";}
                }
                numberOfTurns = 0;
                return echoAhead(); // lastAction := echo
            }
            default: {return null;}
        }
    }


    private boolean foundGround(){return Objects.equals(drone.radar.found, "GROUND");}
    private boolean overOcean(){return Objects.equals(drone.currentBiomes.get(0), "OCEAN");}

    //If the flyToGround flag is true, it alternates between flying forward and scanning until reaching OCEAN
    private JSONObject flyToGround() {
        flyToGround = true;
        return flyForward();
    }


    //Turns twice in the drone's right direction
    private JSONObject uTurnRight() {
        lastAction = Action.heading;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        numberOfTurns++;
        turned = true;
        drone.droneActions.turnRight(drone); //update direction of drone
        if (numberOfTurns >= 2) lastTurn = "RIGHT";
        return sendDecision(lastAction, parameter);
    }

    //Turns twice in the drone's left direction
    private JSONObject uTurnLeft() {
        lastAction = Action.heading;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        numberOfTurns++;
        turned = true;
        if (numberOfTurns >= 2) lastTurn = "LEFT";
        drone.droneActions.turnLeft(drone); //update direction of drone
        return sendDecision(lastAction, parameter);
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
