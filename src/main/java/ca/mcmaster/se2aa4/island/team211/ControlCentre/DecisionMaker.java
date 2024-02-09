package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.json.JSONObject;

public interface DecisionMaker {
    JSONObject makeDecision(Drone drone);
}
