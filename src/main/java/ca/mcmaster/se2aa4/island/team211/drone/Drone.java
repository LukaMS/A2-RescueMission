/*
This class stores the information needed to determine what decision should be made next.
It stores Battery level, coordinates of the drone, current biomes, and Radar, and Data Extractor
 */
package ca.mcmaster.se2aa4.island.team211.drone;

import ca.mcmaster.se2aa4.island.team211.controlcentre.DecisionMaker;
import ca.mcmaster.se2aa4.island.team211.controlcentre.FindStart;
import ca.mcmaster.se2aa4.island.team211.DataExtractor;
import ca.mcmaster.se2aa4.island.team211.locations.Coordinate;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Drone {
    public final Battery battery = new Battery();

    public final Radar radar = new Radar();

    private final DataExtractor dataExtractor = new DataExtractor();

    private String direction;
    public String initialDirection;
    public String left;

    public String right;
    public JSONArray currentBiomes = null;

    public String status = "OK";

    public DecisionMaker decisionMaker;
    public Integer x_cord;

    public Integer y_cord;

    public Map<String, Coordinate> creeks = new HashMap<>();
    public Map<String, Coordinate> emergencySites = new HashMap<>();
    public DroneActions droneActions = new DroneActions();


    /*
    initialized the drone based off of the given information in the info file
     */
    public void initialize(JSONObject info) {
        direction = info.getString("heading");
        initialDirection = direction;
        battery.batteryLevel = info.getInt("budget");
        decisionMaker = new FindStart(this);
    }

    public void extractData(JSONObject extraInfo) {
        dataExtractor.extract(extraInfo,this, decisionMaker);
    }

    public String getDirection(){return direction;}
    public void setDirection(String dir){direction = dir;}
}