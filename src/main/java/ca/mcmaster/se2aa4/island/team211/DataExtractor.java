package ca.mcmaster.se2aa4.island.team211;

import ca.mcmaster.se2aa4.island.team211.ControlCentre.DecisionMaker;
import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import ca.mcmaster.se2aa4.island.team211.Drone.DroneActions;
import ca.mcmaster.se2aa4.island.team211.Locations.Coordinate;
import ca.mcmaster.se2aa4.island.team211.Locations.Creek;
import ca.mcmaster.se2aa4.island.team211.Locations.EmergSite;
import org.json.JSONObject;



public class DataExtractor {
    public void extract(JSONObject extraInfo, Drone drone, DecisionMaker islandFinder) {
        try {
            switch (islandFinder.getLastAction()) {
                case echo, echoLeft, echoRight -> {
                    drone.radar.range = extraInfo.getInt("range");
                    drone.radar.found = extraInfo.getString("found");
                }
                case scan -> {
                    drone.currentBiomes = extraInfo.getJSONArray("biomes");
                    Creek creek = (Creek) extraInfo.getJSONArray("creeks").get(0);
                    Coordinate creekCord = DroneActions.getCordinates(drone);
                    EmergSite site = (EmergSite) extraInfo.getJSONArray("sites").get(0);
                    Coordinate siteCord = DroneActions.getCordinates(drone);
                    Creek.creeks.put(creek, creekCord);
                    EmergSite.sites.put(site, siteCord);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
