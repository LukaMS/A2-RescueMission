/*
finds the coast of the island, such that it is set up to efficiently use GridSearch (it will not miss any sections of land)
 */
package ca.mcmaster.se2aa4.island.team211.controlcentre;

import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import org.json.JSONObject;

import java.util.Objects;

public class IslandFinder extends PhaseOneCommonDecisions {

    private static final String right = "RIGHT";
    private static final String left = "LEFT";
    private String lastEchoDirection = "RIGHT";
    private boolean adjust = true;

    public IslandFinder(Drone drone){
        setLastAction(null);
        setTurnCount(0);
        setFlyToGround(false);
        setDrone(drone);
    }

    @Override
    public JSONObject makeDecision() {
        return switch (lastAction) {
            case null -> echoDirection(drone.right);
            case echo -> echoCase();
            case fly -> flyCase();
            case scan -> scanCase();
            case heading -> super.flyToGround();
            case RE_ALIGN -> realignCase();
            default -> null;
        };
    }

    private JSONObject adjustHeading() {
        lastAction = Action.RE_ALIGN;
        switch (turnCount){
            case 0 -> {
                turnCount++;
                return super.flyForward();
            }
            case 1, 2-> {
                turnCount++;
                if (Objects.equals(lastEchoDirection, left)) {
                    return super.turnLeft();
                } else {
                    return super.turnRight();
                }
            }
            case 3 -> {
                turnCount++;
                if (Objects.equals(lastEchoDirection, left)) {
                    return super.turnRight();
                } else {
                    return super.turnLeft();
                }
            }
            case 4 -> {
                turnCount++;
                return super.scanPosition();
            }
            default -> {}
        }
        return null;
    }

    private JSONObject echoDirection(String direction){
        lastAction = Action.echo;
        JSONObject parameter = new JSONObject();
        parameter.put("direction", direction);
        return super.sendDecision(lastAction, parameter);
    }

    private JSONObject echoCase(){
        if (super.foundGround(drone)){
            if (adjust) {
                return adjustHeading();
            }else{
                return super.flyToGround(); //loops flying and scanning for ground
            }
        }else{
            return super.flyForward(); // lastAction := fly
        }
    }

    private JSONObject scanCase(){
        if (flyToGround) {
            if (super.overOcean()) {
                return super.flyToGround(); // lastAction := fly
            } else {
                drone.decisionMaker = new GridSearch(drone, lastEchoDirection);
                return super.scanPosition();
            }
        }
        return null;
    }

    private JSONObject flyCase(){
        if (flyToGround){
            return super.scanPosition(); // lastAction := scan
        }
        if (!Objects.equals(lastEchoDirection,left)){
            lastEchoDirection = left;
            return echoDirection(drone.left); // lastAction := echo
        } else{
            lastEchoDirection = right;
            return echoDirection(drone.right); // lastAction := echo
        }
    }

    private JSONObject realignCase(){
        if (turnCount < 5) {
            return adjustHeading();
        }
        else{
            turnCount = 0;
            adjust = false;
            return echoDirection(drone.getDirection());
        }
    }

    public String getLastEchoDirection() {return lastEchoDirection;}
    public boolean isAdjust(){return adjust;}
    public void setAdjust(boolean set){adjust = set;}

}