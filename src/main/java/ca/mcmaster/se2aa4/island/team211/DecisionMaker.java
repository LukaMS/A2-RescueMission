package ca.mcmaster.se2aa4.island.team211;

import org.json.JSONObject;

import java.util.Objects;

public class DecisionMaker {

    static Action lastAction = null;

    public Action makeDecision() {
        Action action = null;
        switch(lastAction){
            case null -> {action = Action.fly;}
            case fly -> {action = Action.scan;}
            case stop -> {}
            case heading -> {}
            case echo -> {}
            case scan -> {}
        }

        lastAction = action;
        return action;
    }




}
