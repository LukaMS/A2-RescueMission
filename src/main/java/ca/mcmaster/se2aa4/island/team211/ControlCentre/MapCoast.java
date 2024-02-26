package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class MapCoast implements DecisionMaker{

    public Drone drone;
    public Action lastAction = null;
    private String lastTurn; //Used to determine next turn
    private Integer turnCount = 0; //used for determining how many more turns need to be made
    private String echoDirection;
    private Integer rightTurnCount = 0;
    private Integer leftTurnCount = 0;
    private Integer test = 0;
    private final Integer startX;
    private final Integer startY;

    public MapCoast(Drone drone){
        this.drone = drone;
        this.startX = drone.x_cord;
        this.startY = drone.y_cord;
    }
    /*1. while !mapped:
        1. ScanRight and fly right if there is land
        2. if no land right, scan forward and fly if there is land
        3. scan left and fly if there is land
        4. if no land found turn around
        5. if 2 of same turns in a row then skip scanning that wat to avoid circles
        */

    //Algorithm for searching the island

    @Override
    public JSONObject makeDecision() {
        if (fullCircle() && test > 100) {
            drone.decisionMaker = new GridSearch(drone);
            return scanPosition();
        }
        switch (lastAction){
            case null -> {
                return echoRight();
            }
            case echo -> {
                switch (echoDirection){
                    case "F" -> {
                        if(foundGround()){
                            return flyForward();
                        } else {
                            return echoLeft();
                        }
                    }
                    case "R" -> {
                        if(rightTurnCount >= 2) {
                            return echoAhead();
                        } else if (foundGround()) {
                            return turnRight();
                        } else {
                            return echoAhead();
                        }
                    }
                    case "L" -> {
                        if(leftTurnCount >= 2) {
                            return uTurn();
                        } else if (foundGround()) {
                            return turnLeft();
                        } else {
                            return uTurn();
                        }
                    }
                }
            }
            case fly, heading -> {
                return scanPosition();
            }
            case scan -> {
                return echoRight();
            }
            case uTurn -> {
                if (turnCount < 2) {
                    return uTurn(); //lastAction := uTurn
                } else {
                    turnCount = 0;
                    return echoRight(); // lastAction := echo
                }
            }

            default -> throw new IllegalStateException("Unexpected value: " + lastAction);
        }
        return null;
    }

    //flips the drones direction
    private JSONObject uTurn(){
        lastAction = Action.uTurn;
        turnCount++;
        if(turnCount == 1) {
            if(Objects.equals(lastTurn, "LEFT")) {
                rightTurnCount --;
                return turnRight();
            } else {
                leftTurnCount --;
                return turnLeft();
            }
        } else {
            if(Objects.equals(lastTurn, "LEFT")) {
                leftTurnCount --;
                return turnLeft();
            } else {
                rightTurnCount --;
                return turnRight();
            }
        }
    }

    //Turns in the drone's right direction
    private JSONObject turnRight() {
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        drone.droneActions.turnRight(drone); //update direction of drone
        if(!Objects.equals(lastAction, Action.uTurn)){lastAction = Action.heading;}
        lastTurn = "RIGHT";
        rightTurnCount++;
        leftTurnCount = 0;
        return sendDecision(Action.heading, parameter);
    }

    //Turns in the drone's left direction
    private JSONObject turnLeft() {
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        drone.droneActions.turnLeft(drone); //update direction of drone
        if(!Objects.equals(lastAction, Action.uTurn)){lastAction = Action.heading;}
        lastTurn = "LEFT";
        leftTurnCount++;
        rightTurnCount = 0;
        return sendDecision(Action.heading, parameter);
    }


    private JSONObject flyForward(){
        lastAction = Action.fly;
        drone.droneActions.forward(drone); //update position of drone
        leftTurnCount = 0;
        rightTurnCount = 0;
        return sendDecision(Action.fly);
    }

    private JSONObject scanPosition(){
        lastAction = Action.scan;
        return sendDecision(Action.scan);
    }

    private JSONObject echoAhead(){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.direction);
        echoDirection = "F";
        return sendDecision(lastAction, parameter);
    }

    private JSONObject echoRight(){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        echoDirection = "R";
        return sendDecision(lastAction, parameter);
    }
    private JSONObject echoLeft(){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        echoDirection = "L";
        return sendDecision(lastAction, parameter);
    }

    private boolean foundGround(){return Objects.equals(drone.radar.found, "GROUND");}

    @Override
    public JSONObject sendDecision(Action action, JSONObject parameters){
        JSONObject decision = new JSONObject();
        decision.put("action", action).put("parameters", parameters);
        test++;
        return decision;
    }

    public JSONObject sendDecision(Action action){
        JSONObject decision = new JSONObject();
        decision.put("action", action);
        test++;
        return decision;
    }

    public boolean fullCircle(){
        return Math.abs(drone.x_cord - startX) < 3 && Math.abs(drone.y_cord - startY) < 3;
    }

    @Override
    public Action getLastAction() {
        return lastAction;
    }
}
