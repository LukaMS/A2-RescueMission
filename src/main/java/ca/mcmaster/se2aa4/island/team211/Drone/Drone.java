package ca.mcmaster.se2aa4.island.team211.Drone;

import org.json.JSONObject;

public class Drone {
    public static String direction;

    public static Object currentBiomes = null;
    public final Battery battery = new Battery(); //We have to discharge battery by cost
    public static String status = "OK";
    public final Radar radar = new Radar();

    public void initialize(JSONObject info) {
        this.direction = info.getString("heading");
        battery.batteryLevel = info.getInt("budget");
    }

}