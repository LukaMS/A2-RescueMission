package ca.mcmaster.se2aa4.island.team211;

import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import ca.mcmaster.se2aa4.island.team211.Locations.Coordinate;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DistanceCalculator {
    private final Drone drone;
    private HashMap<String, Float> distances;
    public DistanceCalculator(Drone drone) {
        this.drone = drone;
    }

    public void displayDistances(Logger logger) {
        for (Map.Entry<String,Float> entry: distances.entrySet()) {
            logger.info("Creek: " + entry.getKey() + " Distance to Site: " + entry.getValue());
        }
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

    public void calculateDistances(Object xCord, Object yCord) {
        for (Map.Entry<String,Coordinate> entry: drone.creeks.entrySet()) {
            String creek = entry.getKey();
            int creek_x = (int) entry.getValue().x_cord;
            int creek_y = (int) entry.getValue().y_cord;

            Float distance = (float) Math.sqrt(Math.pow(creek_x-(int)xCord, 2) + Math.pow(creek_y-(int)yCord, 2) );
            distances.put(creek,distance);
        }
    }
}
