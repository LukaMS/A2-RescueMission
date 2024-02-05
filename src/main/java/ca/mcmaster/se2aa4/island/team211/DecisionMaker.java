package ca.mcmaster.se2aa4.island.team211;

import eu.ace_design.island.game.Directions;

import java.util.Objects;

public class DecisionMaker {

    static Action lastAction = null;

    public static Action action = null;

    private int count = 0;

    public Object makeDecision(Drone drone) {
        action = null;
        switch(lastAction){
            case null : {
                action = Action.fly;
            }
            case fly : {
                if (count < 50){
                    action = Action.fly;
                    count++;
                }else {
                    action = Action.stop;
                }

            }
            case stop:
                break;
            case heading:
                break;
            case echo:
                break;
            case scan:
                break;
        }
        lastAction = action;
        return action;
    }





}
