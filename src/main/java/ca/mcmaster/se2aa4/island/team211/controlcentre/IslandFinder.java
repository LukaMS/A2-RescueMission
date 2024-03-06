package ca.mcmaster.se2aa4.island.team211.controlcentre;

import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class IslandFinder implements DecisionMaker {
    private final Drone drone;
    public Action lastAction;
    private String lastEchoDirection;
    private boolean flyToGround = false; //flag to see if the drone should be flying to a separate piece of land
    private boolean adjust = true;
    private int turnCount = 0;

    public IslandFinder(Drone drone){this.drone = drone;}

    @Override
    public JSONObject makeDecision() {
        switch(lastAction){
            case null: {
                lastEchoDirection = "RIGHT";
                return echoDirection(drone.right);
            }
            case echo: {
                if (foundGround()){
                    if (adjust) {
                        return adjustHeading();
                    }else{
                        return flyToGround(); //loops flying and scanning for ground
                    }
                }else{
                    return flyForward(); // lastAction := fly
                }
            }
            case fly: {
                if (flyToGround){
                    return scanPosition(); // lastAction := scan
                }
                if (!Objects.equals(lastEchoDirection,"LEFT")){
                    lastEchoDirection = "LEFT";
                    return echoDirection(drone.left); // lastAction := echo
                } else{
                    lastEchoDirection = "RIGHT";
                    return echoDirection(drone.right); // lastAction := echo
                }
            }
            case scan: {
                if (flyToGround) {
                    if (overOcean()) {
                        return flyToGround(); // lastAction := fly
                    } else {
                        drone.decisionMaker = new GridSearch(drone, lastEchoDirection);
                        return scanPosition();
                    }
                }
                return null;
            }
            case heading: {
                return flyToGround();
            }
            case reAlign:{
                if (turnCount < 5) {
                    return adjustHeading();
                }
                else{
                    turnCount = 0;
                    adjust = false;
                    return echoDirection(drone.direction);
                }
            }
            default: {return null;}
        }
    }

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

    public Action getLastAction(){return lastAction;}


    private JSONObject adjustHeading() {
        lastAction = Action.reAlign;
        switch (turnCount){
            case 0 -> {
                turnCount++;
                return flyForward();
            }
            case 1, 2-> {
                turnCount++;
                if (Objects.equals(lastEchoDirection, "LEFT")) {
                    return turnLeft();
                } else {
                    return turnRight();
                }
            }
            case 3 -> {
                turnCount++;
                if (Objects.equals(lastEchoDirection, "LEFT")) {
                    return turnRight();
                } else {
                    return turnLeft();
                }
            }
            case 4 -> {
                turnCount++;
                return scanPosition();
            }
            default -> {}
        }
        return null;
    }

    private boolean foundGround(){return Objects.equals(drone.radar.found, "GROUND");}
    private boolean overOcean(){return Objects.equals(drone.currentBiomes.get(0), "OCEAN");}

    //If the flyToGround flag is true, it alternates between flying forward and scanning until reaching LAND
    private JSONObject flyToGround() {
        flyToGround = true;
        return flyForward();
    }

    //Turns in the drone's right direction
    private JSONObject turnRight() {
        if (shouldChangeLastAction()){lastAction = Action.heading;}
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        drone.droneActions.turnRight(drone); //update direction of drone
        return sendDecision(Action.heading, parameter);
    }

    //Turns in the drone's left direction
    private JSONObject turnLeft() {
        if (shouldChangeLastAction()){lastAction = Action.heading;}
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        drone.droneActions.turnLeft(drone); //update direction of drone
        return sendDecision(Action.heading, parameter);
    }

    private JSONObject flyForward(){
        if (shouldChangeLastAction()){lastAction = Action.fly;}
        drone.droneActions.forward(drone); //update position of drone
        return sendDecision(Action.fly);
    }

    private JSONObject scanPosition(){
        if (shouldChangeLastAction()){lastAction = Action.scan;}
        return sendDecision(Action.scan);
    }

    private boolean shouldChangeLastAction(){
        return !(Objects.equals(lastAction,Action.uTurn) || Objects.equals(lastAction,Action.reAlign) || Objects.equals(lastAction,Action.uTurn2));
    }

    private JSONObject echoDirection(String direction){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", direction);
        return sendDecision(lastAction, parameter);
    }

}