package ca.mcmaster.se2aa4.island.team211.controlcentre;

import org.json.JSONObject;

public interface DecisionMaker {
    JSONObject makeDecision();

    Action getLastAction();

    JSONObject sendDecision(Action action, JSONObject parameters);

    JSONObject sendDecision(Action action);

}
