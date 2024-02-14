package ca.mcmaster.se2aa4.island.team211.Drone;

import ca.mcmaster.se2aa4.island.team211.ControlCentre.DecisionMaker;
import ca.mcmaster.se2aa4.island.team211.ControlCentre.FindStart;
import ca.mcmaster.se2aa4.island.team211.ControlCentre.IslandFinder;
import ca.mcmaster.se2aa4.island.team211.DataExtractor;
import ca.mcmaster.se2aa4.island.team211.Locations.Coordinate;
import org.json.JSONArray;
import org.json.JSONObject;


public class Drone {
    public String direction;
    public String left,right;
    public JSONArray currentBiomes = null;
    public final Battery battery = new Battery();
    public static String status = "OK";
    public final Radar radar = new Radar();
    public DecisionMaker decisionMaker;
    private final DataExtractor dataExtractor = new DataExtractor();

    public Integer x_cord;
    public Integer y_cord;

    public void initialize(JSONObject info) {
        direction = info.getString("heading");
        battery.batteryLevel = info.getInt("budget");
        decisionMaker = new FindStart(this);
    }


    public void extractdata(JSONObject extraInfo) {
        dataExtractor.extract(extraInfo,this, decisionMaker);
    }

    public JSONObject getDecision(){
        this.getSides();
        return decisionMaker.makeDecision();
    }

    public void getSides(){
        switch (this.direction){
            case "N": {left = "W";right = "E"; break;}
            case "S": {left = "E";right = "W"; break;}
            case "E": {left = "N";right = "S"; break;}
            case "W": {left = "S";right = "N"; break;}

        }
    }

    public void setStart(){
        switch (direction){
            case "N", "S":
                x_cord = radar.range+1;
                y_cord = 1;
                break;
            case "E", "W":
                y_cord = radar.range+1;
                x_cord = 1;
                break;
        }
        decisionMaker = new IslandFinder(this);
    }
    public Integer[] printCoords(){
        Integer[] coords = new Integer[2];
        coords[0] = x_cord;
        coords[1] = y_cord;
        return coords;
    }

    public void turnLeft(){
        switch  (direction){
            case "N" -> {
                x_cord -= 1;
                y_cord -= 1;
                direction = "W";
            }
            case "E" -> {
                x_cord += 1;
                y_cord -= 1;
                direction = "N";
            }
            case "S" -> {
                x_cord += 1;
                y_cord += 1;
                direction = "E";
            }
            case "W" -> {
                x_cord -= 1;
                y_cord += 1;
                direction = "S";
            }
        }
    }

    public void turnRight(){
        switch  (direction){
            case "N" -> {
                x_cord += 1;
                y_cord -= 1;
                direction = "E";
            }
            case "E" -> {
                x_cord += 1;
                y_cord += 1;
                direction = "S";
            }
            case "S" -> {
                x_cord -= 1;
                y_cord += 1;
                direction = "W";
            }
            case "W" -> {
                x_cord -= 1;
                y_cord -= 1;
                direction = "N";
            }
        }
    }

    public void forward(){
        switch (direction){
            case "N" -> y_cord--;
            case "E" -> x_cord++;
            case "S" -> y_cord++;
            case "W" -> x_cord--;
        }
    }
}
