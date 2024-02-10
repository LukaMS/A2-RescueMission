package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.json.JSONObject;

public class FindStart implements DecisionMaker{
    private final Drone drone;
    public static Action lastAction = null;
    public static Action action = null;
    private boolean foundStart = false;
    private int count = 0;

    public FindStart(Drone drone) {
        this.drone = drone;
    }

    @Override
    public JSONObject makeDecision() {
        action = null;
        JSONObject parameters;
        if(!foundStart){
            if(lastAction == null){
                parameters = findStart(drone);
                action = Action.echo;
                lastAction = action;
                return sendDecision(action, parameters);
            } else {
                drone.setStart();
                foundStart = true;
                action = Action.scan;
                lastAction = action;
                return sendDecision(action);
            }
        }
        if(lastAction == Action.fly){
            action = Action.stop;
            lastAction = action;
            return sendDecision(action);
        }
        return sendDecision(Action.stop);
    }

    @Override
    public Action getLastAction() {
        return lastAction;
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

    public JSONObject findStart(Drone drone){
        JSONObject params = new JSONObject();
        String currentHeading = drone.direction;
        switch (currentHeading){
            case "N", "S":
                params.put("direction", "W");
                break;
            case "E", "W":
                params.put("direction", "N");
                break;
        }
        return params;
    }
}
