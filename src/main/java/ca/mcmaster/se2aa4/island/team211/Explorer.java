package ca.mcmaster.se2aa4.island.team211;

import java.io.StringReader;

import ca.mcmaster.se2aa4.island.team211.ControlCentre.Action;
import ca.mcmaster.se2aa4.island.team211.ControlCentre.DecisionMaker;
import ca.mcmaster.se2aa4.island.team211.ControlCentre.FindStart;
import ca.mcmaster.se2aa4.island.team211.Drone.Drone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();

    private Drone drone;


    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));

        this.drone = new Drone();
        drone.initialize(info);

        logger.info("The drone is facing {}", drone.direction);
        logger.info("Battery level is {}", drone.battery.batteryLevel);

    }

    @Override
    public String takeDecision() {
        JSONObject decision = null;
        logger.info("** Current Location X: " + drone.printCoords()[0] + " Y: " + drone.printCoords()[1]);
        try {
            decision = drone.getDecision();
            logger.info("** Decision: {}",decision.toString());
            return decision.toString();
        } catch (Exception e){
            logger.error(e.toString());
        }
        return null;
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n"+response.toString(2));

        Action.cost = response.getInt("cost");
        logger.info("The cost of the action was {}", Action.cost);
        drone.battery.discharge(Action.cost);

        Drone.status = response.getString("status");
        logger.info("The status of the drone is {}", Drone.status);

        JSONObject extraInfo = response.getJSONObject("extras");
        drone.extractdata(extraInfo);

        logger.info("Additional information received: {}", extraInfo);
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }

}
