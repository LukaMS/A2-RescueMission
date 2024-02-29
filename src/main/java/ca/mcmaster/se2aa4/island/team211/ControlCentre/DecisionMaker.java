package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import org.json.JSONObject;

public interface DecisionMaker {
    JSONObject makeDecision();
    Action getLastAction();

    JSONObject sendDecision(Action action, JSONObject parameters);

    JSONObject sendDecision(Action action);

}
