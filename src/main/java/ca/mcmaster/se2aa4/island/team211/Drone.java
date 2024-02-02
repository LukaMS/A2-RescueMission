package ca.mcmaster.se2aa4.island.team211;

import org.json.JSONObject;

public class Drone {
    public String direction;
    public final Battery battery = new Battery(); //We have to discharge battery by cost
    private final Status status = Status.OK;
    private final Radar radar = new Radar();
    private final PhotoScanner photoScanner = new PhotoScanner();


    public void initialize(JSONObject info) {
        this.direction = info.getString("heading");
        battery.batteryLevel = info.getInt("budget");
    }
}
