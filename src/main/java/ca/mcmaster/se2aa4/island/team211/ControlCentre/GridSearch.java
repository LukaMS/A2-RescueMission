package ca.mcmaster.se2aa4.island.team211.ControlCentre;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
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

    public GridSearch(Drone drone){
        this.drone = drone;
    }

    @Override
    public JSONObject makeDecision() {
        /*2. while |Creeks| != 10
          2.    while not at edge of island do
          3.        go stright across until reach ocean -> check if land in front
          4.        if so -> travel to it -> travel straight until reach ocean -> repeat 3.
          5.        if no -> U-turn right if last turn was left, or vise verse -> go straight until reach ocean -> check for land ->
          6.            if so -> repeat 4.
          7.            if no -> U-turn left if last turn was right, or vise versa -> go straight until reach ocean -> check for land ->
          8.                if so -> repeat 4
          9.                if no -> repeat from 5
          10. if last turn was right -> turn right again -> repeat from 3
          11. if last turn was left -> turn left again -> repeat from 3
        */
        switch (lastAction){
            case null: {
                flyForward(); // lastAction := fly
            }


            case fly: {
                scanPosition(); // lastAction := scan
            }


            case echo: {
                if (foundGround()){flyToGround();} // lastAction := fly
                else {
                    if (turned){
                        stop();
                    } else {
                        if (Objects.equals(lastTurn, "RIGHT")) { uTurnLeft();} // lastAction := heading
                        else if (Objects.equals(lastTurn, "LEFT")) {uTurnRight();} // lastAction := heading
                    }
                }
            }


            case scan: {
                if (overOcean()){
                    if (flyToGround){flyToGround();} // lastAction := fly
                    else echoAhead(); // lastAction := echo
                } else {
                    flyToGround = false;
                    flyForward(); // lastAction := fly
                }
            }


            case heading:{
                if (Objects.equals(lastTurn, "RIGHT")) {
                    if (numberOfTurns < 2) {uTurnLeft(); // lastAction := heading
                    } else {lastTurn = "LEFT";} //update direction after completing uTurn
                }
                else if (Objects.equals(lastTurn, "LEFT")) {
                    if (numberOfTurns < 2) {uTurnRight(); // lastAction := heading
                    } else {lastTurn = "RIGHT";}
                }
                numberOfTurns = 0;
                turned = true;
                echoAhead(); // lastAction := echo
            }

            default: {return null;}
        }
    }





    private void stop() {
        lastAction = Action.stop;
        sendDecision(lastAction);
    }

    private void uTurnRight() {
        lastAction = Action.heading;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        numberOfTurns++;
        sendDecision(lastAction, parameter);
    }
    private void uTurnLeft() {
        lastAction = Action.heading;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        numberOfTurns++;
        sendDecision(lastAction, parameter);
    }

    private void flyToGround() {
        flyToGround = true;
        flyForward();
    }

    private boolean foundGround(){return Objects.equals(drone.radar.found, "GROUND");}
    private boolean overOcean(){return Objects.equals(drone.currentBiomes.get(0), "OCEAN");}

    private void flyForward(){
        lastAction = Action.fly;
        sendDecision(lastAction);
    }

    private void scanPosition(){
        lastAction = Action.scan;
        sendDecision(lastAction);
    }

    private void echoAhead(){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.direction);
        sendDecision(lastAction, parameter);
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
