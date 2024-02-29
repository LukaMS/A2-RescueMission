package ca.mcmaster.se2aa4.island.team211;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import ca.mcmaster.se2aa4.island.team211.Locations.Coordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DistanceCalculator {
    private final Drone drone;
    private final Map<String, Float> distances = new HashMap<>();

    private final Logger logger = LogManager.getLogger();

    public DistanceCalculator(Drone drone) {
        this.drone = drone;
    }

    public Map<String, Float> getDistances(){
        return distances;
    }

    public String determineClosest() {
        String closestCreek = "";
        Float minDistance = (float) 999999;
        for (Map.Entry<String,Float> entry: distances.entrySet()){
            if (entry.getValue() < minDistance){
                closestCreek = entry.getKey();
                minDistance = entry.getValue();
            }
        }
        return closestCreek;
    }

    public void calculateDistances() {
        logger.info("Calculating Distances");
        Map.Entry<String,Coordinate> entry = drone.emergSites.entrySet().iterator().next(); //extract site x and y coord.
        int site_x = (int) entry.getValue().x_cord;
        int site_y = (int) entry.getValue().y_cord;

        for (Map.Entry<String,Coordinate> entry2: drone.creeks.entrySet()) {
            String creek = entry2.getKey();
            int creek_x = (int) entry2.getValue().x_cord;
            int creek_y = (int) entry2.getValue().y_cord;

            Float distance = (float) Math.sqrt(Math.pow(creek_x-site_x, 2) + Math.pow(creek_y-site_y, 2) );
            distances.put(creek,distance);
        }
    }
}
