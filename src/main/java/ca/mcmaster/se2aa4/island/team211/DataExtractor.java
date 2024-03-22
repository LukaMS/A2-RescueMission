/*
Extract the information the drone finds after performing an action in the game
 */
package ca.mcmaster.se2aa4.island.team211;

import ca.mcmaster.se2aa4.island.team211.controlcentre.DecisionMaker;
import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import ca.mcmaster.se2aa4.island.team211.drone.DroneActions;
import ca.mcmaster.se2aa4.island.team211.locations.Coordinate;
import org.json.JSONObject;

public class DataExtractor {
    public void extract(JSONObject extraInfo, Drone drone, DecisionMaker decisionMaker) {
        try {
            switch (decisionMaker.getLastAction()) {
                case echo : {
                    extractRadar(extraInfo, drone);
                    break;
                }
                case scan: {
                    extractCreek(extraInfo, drone);
                    extractSite(extraInfo, drone);
                    drone.currentBiomes = extraInfo.getJSONArray("biomes");
                    break;
                }
                default:
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void extractRadar(JSONObject extraInfo, Drone drone){
        drone.radar.range = extraInfo.getInt("range");
        drone.radar.found = extraInfo.getString("found");
    }
    private void extractCreek(JSONObject extraInfo, Drone drone){
        try {
            String creek = (String) extraInfo.getJSONArray("creeks").get(0);
            Coordinate creekCord = DroneActions.getCordinates(drone);
            drone.creeks.put(creek, creekCord);
        } catch (Exception ignored) { }
    }

    private void extractSite(JSONObject extraInfo, Drone drone){
        try {
            String site = (String) extraInfo.getJSONArray("sites").get(0);
            Coordinate siteCord = DroneActions.getCordinates(drone);
            drone.emergencySites.put(site, siteCord);
        } catch (Exception ignored){ }
    }
}
