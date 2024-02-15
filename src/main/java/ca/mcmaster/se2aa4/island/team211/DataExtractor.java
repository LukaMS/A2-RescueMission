package ca.mcmaster.se2aa4.island.team211;

import ca.mcmaster.se2aa4.island.team211.ControlCentre.DecisionMaker;
import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import ca.mcmaster.se2aa4.island.team211.Drone.DroneActions;
import ca.mcmaster.se2aa4.island.team211.Locations.Coordinate;
import ca.mcmaster.se2aa4.island.team211.Locations.Creek;
import ca.mcmaster.se2aa4.island.team211.Locations.EmergSite;
import org.json.JSONObject;



public class DataExtractor {
    public void extract(JSONObject extraInfo, Drone drone, DecisionMaker decisionMaker) {
        try {
            switch (decisionMaker.getLastAction()) {
                case echo, echoLeft, echoRight -> {
                    drone.radar.range = extraInfo.getInt("range");
                    drone.radar.found = extraInfo.getString("found");
                }
                case scan -> {
                    //Changed
                    //Try and add a creek into the drones hashmap
                    try {
                        String creek = (String) extraInfo.getJSONArray("creeks").get(0);
                        Coordinate creekCord = DroneActions.getCordinates(drone);
                        drone.creeks.put(creek, creekCord);
                    } catch (Exception e){
                        ;
                    }
                    //try and add emergSite to drone hashmap
                    try {
                        String site = (String) extraInfo.getJSONArray("sites").get(0);
                        Coordinate siteCord = DroneActions.getCordinates(drone);
                        drone.emergSites.put(site, siteCord);
                    } catch (Exception e){
                        ;
                    }
                    drone.currentBiomes = extraInfo.getJSONArray("biomes");

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
