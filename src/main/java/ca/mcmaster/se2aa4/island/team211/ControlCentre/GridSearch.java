package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.json.JSONObject;

public class GridSearch implements DecisionMaker{
    public static Action lastAction;

    public GridSearch(Drone drone){
    }

    @Override
    public JSONObject makeDecision() {
        return null;
    }

    @Override
    public Action getLastAction() {
        return null;
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
}
