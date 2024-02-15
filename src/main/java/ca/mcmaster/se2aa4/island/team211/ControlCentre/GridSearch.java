package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.Objects;

public class GridSearch implements DecisionMaker{
    //if you echo over ground, it just finds "GROUND" with a range of 0

    public Action lastAction = null;

    public Drone drone;

    private String lastTurn = "RIGHT";
    private boolean turned = false;
    private boolean flyToGround = false; //flag to see if the drone should be flying to a separate piece of land
    private Integer numberOfTurns = 0;

    public GridSearch(Drone drone){this.drone = drone;}
    /*1. while |Creeks| != 10
          2.    while not at edge of island do
          3.        go straight across until reach ocean -> check if land in front
          4.        if so -> travel to it ->  repeat 3.
          5.        if no -> U-turn right if last turn was left, or vise verse -> go straight until reach ocean -> check for land ->
          6.            if so -> repeat 4.
          7.            if no -> U-turn left if last turn was right, or vise versa -> go straight until reach ocean -> check for land ->
          8.                if so -> repeat 4
          9.                if no -> repeat from 5
          10. if last turn was right -> turn right again -> repeat from 3
          11. if last turn was left -> turn left again -> repeat from 3
        */
    private final Logger logger = LogManager.getLogger();

    @Override
    public JSONObject makeDecision() {

        switch (lastAction){
            case null: {
                logger.info("Starting Grid Search");
                return flyForward(); // lastAction := fly
            }


            case fly: {
                return scanPosition(); // lastAction := scan
            }


            case echo: {
                if (foundGround()){
                    turned = false;
                    return flyToGround();
                } // lastAction := fly
                else {
                    if (turned){
                        turned = false;
                        return stop();
                    } else {
                        if (Objects.equals(lastTurn, "RIGHT")) {return uTurnLeft();} // lastAction := heading
                        else if (Objects.equals(lastTurn, "LEFT")) {return uTurnRight();} // lastAction := heading
                    }
                }
            }


//not sending next decision here
            case scan: {
                if (overOcean()){
                    if (flyToGround){return flyToGround();} // lastAction := fly
                    else return echoAhead(); // lastAction := echo
                } else {
                    flyToGround = false;
                    return flyForward(); // lastAction := fly
                }

            }


            case heading:{
                if (Objects.equals(lastTurn, "RIGHT")) {
                    if (numberOfTurns < 2) {return uTurnLeft(); // lastAction := heading
                    } else {lastTurn = "LEFT";} //update direction after completing uTurn
                }
                else if (Objects.equals(lastTurn, "LEFT")) {
                    if (numberOfTurns < 2) {return uTurnRight(); // lastAction := heading
                    } else {lastTurn = "RIGHT";}
                }
                numberOfTurns = 0;
                turned = true;
                return echoAhead(); // lastAction := echo
            }

            default: {return null;}
        }
    }



    private boolean foundGround(){return Objects.equals(drone.radar.found, "GROUND");}
    private boolean overOcean(){return Objects.equals(drone.currentBiomes.get(0), "OCEAN");}

    private JSONObject flyToGround() {
        flyToGround = true;
        return flyForward();
    }

    private JSONObject stop() {
        lastAction = Action.stop;
        return sendDecision(lastAction);
    }

    private JSONObject uTurnRight() {
        lastAction = Action.heading;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        numberOfTurns++;
        return sendDecision(lastAction, parameter);
    }
    private JSONObject uTurnLeft() {
        lastAction = Action.heading;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        numberOfTurns++;
        return sendDecision(lastAction, parameter);
    }


    private JSONObject flyForward(){
        lastAction = Action.fly;
        return sendDecision(lastAction);
    }

    private JSONObject scanPosition(){
        lastAction = Action.scan;
        return sendDecision(lastAction);
    }

    private JSONObject echoAhead(){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.direction);
        return sendDecision(lastAction, parameter);
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

    @Override
    public Action getLastAction() {
        return null;
    }
}
