package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class IslandFinder implements DecisionMaker {
    private final Drone drone;
    public static Action lastAction;
    public JSONObject lastDecision;
    public static Action action = null;

    public IslandFinder(Drone drone){this.drone = drone;}

    @Override
    public JSONObject makeDecision() {
        action = null;
        switch(lastAction){
            case null -> {
                action = Action.echo;
                JSONObject parameter = new JSONObject();
                parameter.put("direction", drone.direction);
                lastAction = action;
                return sendDecision(action,parameter);
            }
            case echo -> {
                if (Objects.equals(drone.radar.found,"GROUND")){
                    action = Action.fly;
                    lastAction = Action.flyToGround;
                    drone.droneActions.forward(drone);
                    return sendDecision(action);
                }else if (Objects.equals(drone.radar.found,"OUT_OF_RANGE")){
                    action = Action.fly;
                    lastAction = Action.flyBlind;
                    drone.droneActions.forward(drone);
                    return sendDecision(action);
                }
            }
            case flyToGround -> {
                action = Action.scan;
                lastAction = action;
                return sendDecision(action);
            }
            case flyBlind ->{
                action = Action.echo;
                JSONObject parameter = new JSONObject();
                parameter.put("direction", drone.left);
                lastAction = Action.echoLeft;
                return sendDecision(action,parameter);
            }
            case echoLeft ->{
                if (Objects.equals(drone.radar.found,"GROUND")){
                    action = Action.heading;
                    JSONObject parameter = new JSONObject();
                    parameter.put("direction", drone.left);
                    lastAction = Action.heading;
                    drone.droneActions.turnLeft(drone);
                    return sendDecision(action,parameter);
                }else{
                    action = Action.echo;
                    JSONObject parameter = new JSONObject();
                    parameter.put("direction", drone.right);
                    lastAction = Action.echoRight;
                    return sendDecision(action,parameter);
                }
            }
            case echoRight ->{
                if (Objects.equals(drone.radar.found,"GROUND")) {
                    action = Action.heading;
                    JSONObject parameter = new JSONObject();
                    parameter.put("direction", drone.right);
                    lastAction = Action.heading;
                    drone.droneActions.turnRight(drone);
                    return sendDecision(action, parameter);
                } else{
                    action = Action.scan;
                    lastAction = Action.scan;
                    return sendDecision(action);
                }
            }

            case heading ->{
                action = Action.scan;
                lastAction = Action.scan;
                return sendDecision(action);
            }

            case scan ->{
                if (Objects.equals(drone.radar.found,"GROUND")) {
                    if (!overOcean()) {
                        action = Action.fly;
                        lastAction = action;
                        drone.decisionMaker = new GridSearch(drone);
                    } else {
                        action = Action.fly;
                        lastAction = Action.flyToGround;
                        drone.droneActions.forward(drone);

                    }
                } else {
                    action = Action.fly;
                    lastAction = Action.flyBlind;
                    drone.droneActions.forward(drone);
                }
                return sendDecision(action);
            }

            default -> {return null;}
        }
        return null;
    }

    public JSONObject sendDecision(Action action, JSONObject parameters){
        JSONObject decision = new JSONObject();
        decision.put("action", action).put("parameters", parameters);
        lastDecision = decision;
        return decision;
    }

    public JSONObject sendDecision(Action action){
        JSONObject decision = new JSONObject();
        decision.put("action", action);
        lastDecision = decision;
        return decision;
    }

    public Action getLastAction(){return lastAction;}
    public boolean overOcean(){return Objects.equals(drone.currentBiomes.get(0), "OCEAN");}





}