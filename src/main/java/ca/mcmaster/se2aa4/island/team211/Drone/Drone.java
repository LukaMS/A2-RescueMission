package ca.mcmaster.se2aa4.island.team211.Drone;

import ca.mcmaster.se2aa4.island.team211.ControlCentre.DecisionMaker;
import ca.mcmaster.se2aa4.island.team211.ControlCentre.FindStart;
import ca.mcmaster.se2aa4.island.team211.DataExtractor;
import ca.mcmaster.se2aa4.island.team211.Locations.Coordinate;
import ca.mcmaster.se2aa4.island.team211.Locations.Creek;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


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
    //Changed
    //Put the hashmaps inside the drone class (we can switch this around but it works)
    public HashMap<String, Coordinate> creeks = new HashMap<>();
    public HashMap<String, Coordinate> emergSites = new HashMap<>();

    public DroneActions droneActions = new DroneActions();

    public void initialize(JSONObject info) {
        direction = info.getString("heading");
        battery.batteryLevel = info.getInt("budget");
        decisionMaker = new FindStart(this);
    }

    public void extractdata(JSONObject extraInfo) {
        dataExtractor.extract(extraInfo,this, decisionMaker);
    }

}
