package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class IslandFinder implements DecisionMaker {
    private final Drone drone;
    public Action lastAction;
    public String lastEchoDirection;
    private boolean flyToGround = false; //flag to see if the drone should be flying to a separate piece of land

    public IslandFinder(Drone drone){this.drone = drone;}

    @Override
    public JSONObject makeDecision() {
        switch(lastAction){
            case null: {
                return echoDirection(drone.direction);
            }
            case echo: {
                if (foundGround()){
                    return flyToGround(); //loops flying and scanning for ground
                }else{
                    return flyForward(); // lastAction := fly
                }
            }
            case fly: {
                if (flyToGround){
                    return scanPosition(); // lastAction := scan
                }
                if (Objects.equals(lastEchoDirection,drone.right) || Objects.equals(lastEchoDirection, drone.direction)){
                    return echoDirection(drone.left); // lastAction := echo
                } else{
                    return echoDirection(drone.right); // lastAction := echo
                }
            }
            case scan: {
                if (flyToGround) {
                    if (overOcean()) {
                        return flyToGround(); // lastAction := fly
                    } else {
                        drone.decisionMaker = new GridSearch(drone);
                        return scanPosition();
                    }
                }
            }
            case heading: {
                return flyToGround();
            }
            default: {return null;}
        }
    }

    private boolean foundGround(){return Objects.equals(drone.radar.found, "GROUND");}
    private boolean overOcean(){return Objects.equals(drone.currentBiomes.get(0), "OCEAN");}

    //If the flyToGround flag is true, it alternates between flying forward and scanning until reaching OCEAN
    private JSONObject flyToGround() {
        flyToGround = true;
        if (Objects.equals(lastEchoDirection, drone.direction)) {
            return flyForward();
        } else if (Objects.equals(lastEchoDirection, drone.right)){
            return turnRight();
        } else {
            return turnLeft();
        }
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

    private JSONObject flyForward(){
        lastAction = Action.fly;
        drone.droneActions.forward(drone); //update position of drone
        return sendDecision(lastAction);
    }

    private JSONObject scanPosition(){
        lastAction = Action.scan;
        return sendDecision(lastAction);
    }

    private JSONObject echoDirection(String direction){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", direction);
        lastEchoDirection = direction;
        return sendDecision(lastAction, parameter);
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





}