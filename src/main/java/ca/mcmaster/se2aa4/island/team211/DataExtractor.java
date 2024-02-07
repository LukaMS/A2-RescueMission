package ca.mcmaster.se2aa4.island.team211;

import ca.mcmaster.se2aa4.island.team211.ControlCentre.IslandFinder;
import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import ca.mcmaster.se2aa4.island.team211.Drone.Radar;
import ca.mcmaster.se2aa4.island.team211.Locations.Coordinate;
import ca.mcmaster.se2aa4.island.team211.Locations.Creek;
import ca.mcmaster.se2aa4.island.team211.Locations.EmergSite;
import org.json.JSONObject;



public class DataExtractor {
    public void extract(JSONObject extraInfo, Radar radar) {
        try {
            switch (IslandFinder.lastAction) {
                case echo -> {
                    radar.range = (Integer) extraInfo.getJSONArray("range").get(0);
                    radar.found = extraInfo.getJSONArray("found").get(0);
                }
                case scan -> {
                    Drone.currentBiomes = extraInfo.getJSONArray("biomes");
                    Creek creek = (Creek) extraInfo.getJSONArray("creeks").get(0);
                    Coordinate creekCord = Drone.getCordinates();
                    EmergSite site = (EmergSite) extraInfo.getJSONArray("sites").get(0);
                    Coordinate siteCord = Drone.getCordinates();
                    Creek.creeks.put(creek, creekCord);
                    EmergSite.sites.put(site, siteCord);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
