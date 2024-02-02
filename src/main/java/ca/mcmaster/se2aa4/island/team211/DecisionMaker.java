package ca.mcmaster.se2aa4.island.team211;

import java.util.Objects;

public class DecisionMaker {

    private Action lastAction = null;
    public Action makeDecision() {
        Action action = Action.fly;
        if (Objects.equals(lastAction, Action.fly)){
            action = Action.stop;
        }
        this.lastAction = action;
        return action;
    }
}
