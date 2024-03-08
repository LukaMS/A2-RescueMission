package ca.mcmaster.se2aa4.island.team211.controlcentre;

import ca.mcmaster.se2aa4.island.team211.DistanceCalculator;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class CreekFinder implements DecisionMaker {
    public Drone drone;
    public Action lastAction = Action.fly;
    DistanceCalculator distanceCalculator;
    public float minRadius;
    private float droneDistanceToSite;
    private boolean turned = false; //indicates whether a turn was just made or not
    private String lastTurn; //Used to determine next turn
    private String turnDirection; //The current turn being made
    private boolean flyToGround = false; //flag to see if the drone should be flying to a separate piece of land
    private Integer turnCount = 0; //used for determining how many more turns need to be made
    private boolean outOfRadiusPermanent = false;

    public CreekFinder(Drone drone) {
        this.drone = drone;
        initialize();
    }

    public void initialize(){
        distanceCalculator = new DistanceCalculator(drone);
        distanceCalculator.calculateDistances();
        setMinRadius();
        setDroneDistanceToSite();
    }

    private void setMinRadius(){
        distanceCalculator.calculateDistances();
        String closestCreek = distanceCalculator.determineClosest();
        minRadius = distanceCalculator.getDistances().get(closestCreek);
    }

    private void setDroneDistanceToSite(){droneDistanceToSite = distanceCalculator.distanceToSite(drone); }

    private boolean outOfRange(){return droneDistanceToSite > minRadius; }

    @Override
    public JSONObject makeDecision() {
        setDroneDistanceToSite();

        //stop conditions
        if (outOfRadiusPermanent || drone.battery.batteryLevel < 1000 || drone.y_cord == 0) {return stop();}

        switch (lastAction){
            case null:
            case fly, heading: {
                return scanPosition(); // lastAction := heading
            }// lastAction := scan
            case echo: {
                if(outOfRange()){outOfRadiusPermanent = true;}
                //if found ground fly to it
                if (foundGround()){
                    turned = false;
                    return flyToGround(); // lastAction := fly
                } //if didn't find ground, but just turned, then reAlign position
                else {
                    if (turned){
                        turned = false;
                        return reAlign(); // shifts position
                    } else { // if didn't find ground, and didn't just turn, then turn
                        if (Objects.equals(lastTurn, "RIGHT")) {
                            turnDirection = "RIGHT";
                        } // lastAction := heading
                        else{
                            turnDirection = "LEFT";
                        }
                        turnCount = 0;
                        if (drone.radar.range > 3 && Objects.equals(drone.radar.found,"OUT_OF_RANGE")){
                            return uTurn();
                        } else {
                            return uTurn2();
                        }
                    }
                }
            }
            case scan: {
                if (outOfRange()){ //if the drone is out of min range, then turn around
                    if (Objects.equals(lastTurn, "RIGHT")) {turnDirection = "RIGHT";}
                    else{turnDirection = "LEFT";}
                    turnCount = 0;
                    return uTurn();
                }

                if (overOcean()){
                    if (flyToGround){return flyToGround();} // lastAction := fly
                    else {return echoAhead();} // lastAction := echo
                } else {
                    flyToGround = false;
                    return flyForward(); // lastAction := fly
                }

            }
            case reAlign: {
                return reAlign();
            }
            case uTurn:{
                if (turnCount < 6) {
                    return uTurn(); //lastAction := uTurn
                } else {
                    turnCount = 0;
                    return echoAhead(); // lastAction := echo
                }
            }
            case uTurn2:{
                if (turnCount < 6) {
                    return uTurn2(); //lastAction := uTurn
                } else {
                    turnCount = 0;
                    return echoAhead(); // lastAction := echo
                }
            }
            default: {return null;}
        }
    }
    /*
    Weird U-Turn to make it go row by row instead of being offset by one
        1. If last turn was right and turn count is 0 then turn right
        2. If turn count is 1 and do opposite of last turn
        3. If turn count is 2 then do same turn
        4. if turn count is 3 fly forward
        5. if turn count is 5 then turn same again.
     */

    private JSONObject uTurn(){
        lastAction = Action.uTurn;
        turned = true;
        switch (turnCount){
            case 0 -> {
                turnCount++;
                if (Objects.equals(turnDirection, "LEFT")) {
                    lastTurn = "LEFT";
                    turnDirection = "RIGHT";
                    return turnLeft();
                } else {
                    lastTurn = "RIGHT";
                    turnDirection = "LEFT";
                    return turnRight();
                }
            }
            case 1, 2, 4 -> {
                turnCount++;
                if (Objects.equals(turnDirection, "LEFT")) {
                    lastTurn = "LEFT";
                    return turnLeft();
                } else {
                    lastTurn = "RIGHT";
                    return turnRight();
                }
            }
            case 3, 5 -> {
                turnCount++;
                return flyForward();
            }
            default -> {return null;}
        }
    }

    private JSONObject uTurn2(){
        lastAction = Action.uTurn2;
        turned = true;
        switch (turnCount){
            case 0, 1, 2, 4 -> {
                turnCount++;
                if (Objects.equals(turnDirection, "LEFT")) {
                    lastTurn = "LEFT";
                    return turnLeft();
                } else {
                    lastTurn = "RIGHT";
                    return turnRight();
                }
            }
            case 3 -> {
                turnCount++;
                if (Objects.equals(lastTurn, "LEFT")) {
                    turnDirection = "RIGHT";
                } else {
                    turnDirection = "LEFT";
                }
                return flyForward();
            }
            case 5 -> {
                turnCount++;
                return scanPosition();
            }
            default -> {return null;}
        }
    }

    private JSONObject reAlign() {
        lastAction = Action.reAlign;
        if (turnCount == 0) {
            if (Objects.equals(lastTurn, "RIGHT")) {
                turnCount++;
                return turnRight();
            } else {
                turnCount++;
                return turnLeft();
            }
        } else if (turnCount == 1){
            turnCount++;
            return flyForward();
        } else if (turnCount == 2) {
            if (Objects.equals(lastTurn, "RIGHT")) {
                turnCount = 0;
                lastAction = Action.heading;
                lastTurn = "LEFT";
                return turnLeft();
            } else {
                turnCount = 0;
                lastAction = Action.heading;
                lastTurn = "RIGHT";
                return turnRight();
            }
        }
        else {
            return echoAhead();
        }
    }

    //If the flyToGround flag is true, it alternates between flying forward and scanning until reaching OCEAN
    private JSONObject flyToGround() {
        flyToGround = true;
        return flyForward();
    }

    //Turns in the drone's right direction
    private JSONObject turnRight() {
        if (shouldChangeLastAction()){lastAction = Action.heading;}
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.right);
        drone.droneActions.turnRight(drone); //update direction of drone
        return sendDecision(Action.heading, parameter);
    }

    //Turns in the drone's left direction
    private JSONObject turnLeft() {
        if (shouldChangeLastAction()){lastAction = Action.heading;}
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.left);
        drone.droneActions.turnLeft(drone); //update direction of drone
        return sendDecision(Action.heading, parameter);
    }


    private JSONObject flyForward(){
        if (shouldChangeLastAction()){lastAction = Action.fly;}
        drone.droneActions.forward(drone); //update position of drone
        return sendDecision(Action.fly);
    }

    private JSONObject scanPosition(){
        if (shouldChangeLastAction()){lastAction = Action.scan;}
        return sendDecision(Action.scan);
    }

    private boolean shouldChangeLastAction(){
        return !(Objects.equals(lastAction,Action.uTurn) || Objects.equals(lastAction,Action.reAlign) || Objects.equals(lastAction,Action.uTurn2));
    }

    private JSONObject echoAhead(){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", drone.direction);
        return sendDecision(lastAction, parameter);
    }

    private JSONObject stop() {
        lastAction = Action.stop;
        return sendDecision(lastAction);
    }

    private boolean foundGround(){return Objects.equals(drone.radar.found, "GROUND");}
    private boolean overOcean(){
        for (int i = 0; i < drone.currentBiomes.length(); i++) {
            if (!"OCEAN".equals(drone.currentBiomes.get(i))) {
                return false;
            }
        }
        return true;
    }
    @Override
    public Action getLastAction() {
        return lastAction;
    }

    @Override
    public JSONObject sendDecision(Action action, JSONObject parameters){
        JSONObject decision = new JSONObject();
        decision.put("action", action).put("parameters", parameters);
        return decision;
    }

    @Override
    public JSONObject sendDecision(Action action){
        JSONObject decision = new JSONObject();
        decision.put("action", action);
        return decision;
    }
}
