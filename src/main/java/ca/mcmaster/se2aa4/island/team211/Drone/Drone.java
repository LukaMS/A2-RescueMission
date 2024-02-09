package ca.mcmaster.se2aa4.island.team211.Drone;

import ca.mcmaster.se2aa4.island.team211.ControlCentre.DecisionMaker;
import ca.mcmaster.se2aa4.island.team211.ControlCentre.FindStart;
import ca.mcmaster.se2aa4.island.team211.ControlCentre.IslandFinder;
import ca.mcmaster.se2aa4.island.team211.DataExtractor;
import ca.mcmaster.se2aa4.island.team211.Locations.Coordinate;
import org.json.JSONArray;
import org.json.JSONObject;


public class Drone {
    public static String direction;
    public String left,right;
    public static JSONArray currentBiomes = null;
    public final Battery battery = new Battery();
    public static String status = "OK";
    public final Radar radar = new Radar();
    private DecisionMaker decisionMaker;
    private final DataExtractor dataExtractor = new DataExtractor();

    public static Integer x_cord;
    public static Integer y_cord;

    public static Coordinate getCordinates() {
        return new Coordinate(x_cord, y_cord);
    }


    public void initialize(JSONObject info) {
        direction = info.getString("heading");
        battery.batteryLevel = info.getInt("budget");
        decisionMaker = new IslandFinder(this);
    }


    public void extractdata(JSONObject extraInfo) {
        dataExtractor.extract(extraInfo, radar, decisionMaker);
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
    }
    public Integer[] printCoords(){
        Integer[] coords = new Integer[2];
        coords[0] = x_cord;
        coords[1] = y_cord;
        return coords;
    }
}
