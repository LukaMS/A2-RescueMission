/*
Receives decisions from DecisionMakers, and sends them to the game to perform actions
 */

package ca.mcmaster.se2aa4.island.team211;

import java.io.StringReader;
import java.util.Map;

import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import ca.mcmaster.se2aa4.island.team211.locations.Coordinate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private Drone drone;
    private final Logger logger = LogManager.getLogger();


    @Override
    public void initialize(String s) {
        if (logger.isInfoEnabled()) { logger.info("** Initializing the Exploration Command Center");}
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        if (logger.isInfoEnabled()) {logger.info("** Initialization info:\n {}",info.toString(2));}
        this.drone = new Drone();
        drone.initialize(info);

        if (logger.isInfoEnabled()) {
            logger.info("The drone is facing {}", drone.direction);
            logger.info("Battery level is {}", drone.battery.batteryLevel);
        }

    }

    @Override
    public String takeDecision() {
        if (logger.isInfoEnabled()) {
            logger.info("** Current Location X: {} Y: {} ", drone.droneActions.printCoords(drone)[0], drone.droneActions.printCoords(drone)[1]);
            logger.info("** Current Battery {}", drone.battery.batteryLevel);
        }

        try {
            JSONObject decision = drone.droneActions.getDecision(drone);
            if (logger.isInfoEnabled()) {logger.info("** Decision: {}", decision);}
            return decision.toString();
        } catch (Exception e){
            if (logger.isErrorEnabled()) {logger.error(e.toString());}
        }
        return null;
    }

    /*
    Displays the result of the performed action
     */
    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        if (logger.isInfoEnabled()) {logger.info("** Response received:\n {}",response.toString(2));}

        int actionCost = response.getInt("cost");
        if (logger.isInfoEnabled()) {logger.info("The cost of the action was {}", actionCost);}
        drone.battery.discharge(actionCost);

        drone.status = response.getString("status");
        if (logger.isInfoEnabled()) {logger.info("The status of the drone is {}", drone.status);}

        JSONObject extraInfo = response.getJSONObject("extras");
        drone.extractData(extraInfo);

        if (logger.isInfoEnabled()) {logger.info("Additional information received: {}", extraInfo);}

        //print set of keys with Coordinates
        for (Map.Entry<String, Coordinate> entry: drone.creeks.entrySet()) {
            if (logger.isInfoEnabled()) {logger.info("Creek {} x = {}  y = {}", entry.getKey() , entry.getValue().xCoordinate , entry.getValue().yCoordinate);}
        }
        for (Map.Entry<String, Coordinate> entry: drone.emergencySites.entrySet()) {
            if (logger.isInfoEnabled()) {logger.info("Site {} x = {} y = {}", entry.getKey() , entry.getValue().xCoordinate , entry.getValue().yCoordinate);}
        }
    }

    @Override
    public String deliverFinalReport() {
        if (logger.isInfoEnabled()) {logger.info("Final Report: ");}
        DistanceCalculator distanceCalculator = new DistanceCalculator(drone);
        distanceCalculator.calculateDistances();
        for (Map.Entry<String,Float> entry2: distanceCalculator.getDistances().entrySet()) {
            if (logger.isInfoEnabled()) {logger.info("Creek: {} Distance to Site: {} ", entry2.getKey(), entry2.getValue());}
        }
        String closestCreek =  distanceCalculator.determineClosest();
        if (logger.isInfoEnabled()) {logger.info("Closest Creek to Emergency Site: {}", closestCreek);}

        return closestCreek;
    }


}
