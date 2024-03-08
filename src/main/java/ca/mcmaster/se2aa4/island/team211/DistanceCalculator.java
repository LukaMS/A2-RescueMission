package ca.mcmaster.se2aa4.island.team211;

import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import ca.mcmaster.se2aa4.island.team211.locations.Coordinate;
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
        Map.Entry<String,Coordinate> entry = drone.emergencySites.entrySet().iterator().next(); //extract site x and y coord.
        int siteX = (int) entry.getValue().xCoordinate;
        int siteY = (int) entry.getValue().yCoordinate;

        for (Map.Entry<String,Coordinate> entry2: drone.creeks.entrySet()) {
            String creek = entry2.getKey();
            int creekX = (int) entry2.getValue().xCoordinate;
            int creekY = (int) entry2.getValue().yCoordinate;

            Float distance = (float) Math.sqrt(Math.pow(creekX-siteX, 2) + Math.pow(creekY-siteY, 2) );
            distances.put(creek,distance);
        }
    }
}
