package ca.mcmaster.se2aa4.island.team211.Drone;

import ca.mcmaster.se2aa4.island.team211.DataExtractor;
import ca.mcmaster.se2aa4.island.team211.Locations.Coordinate;
import org.json.JSONObject;

public class Drone {
    public static String direction;
    public static Object currentBiomes = null;
    public final Battery battery = new Battery(); //We have to discharge battery by cost
    public static String status = "OK";
    public final Radar radar = new Radar();
    private final DataExtractor dataExtractor = new DataExtractor();

    public static Integer x_cord;
    public static Integer y_cord;

    public static Coordinate getCordinates() {
        return new Coordinate(x_cord, y_cord);
    }


    public void initialize(JSONObject info) {
        direction = info.getString("heading");
        battery.batteryLevel = info.getInt("budget");
    }


    public void extractdata(JSONObject extraInfo) {
        dataExtractor.extract(extraInfo, radar);
    }
}
