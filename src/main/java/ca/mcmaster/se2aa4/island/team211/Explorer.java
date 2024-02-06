package ca.mcmaster.se2aa4.island.team211;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();

    private final DecisionMaker decisionMaker = new IslandFinder();

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

        JSONObject decision = new JSONObject();
        Object action = decisionMaker.makeDecision(drone);

        decision.put("action", action);
        logger.info("** Decision: {}",decision.toString());
        return decision.toString();
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
        extractExtraInfo(extraInfo);

        logger.info("Additional information received: {}", extraInfo);

    }

    private void extractExtraInfo(JSONObject extraInfo) {
        try {
            switch (IslandFinder.lastAction) {
                case echo -> {
                    drone.radar.range = (Integer) extraInfo.getJSONArray("range").get(0);
                    drone.radar.found = extraInfo.getJSONArray("found").get(0);
                }
                case scan -> {
                    Drone.currentBiomes = extraInfo.getJSONArray("biomes");
                    Creek creek = (Creek) extraInfo.getJSONArray("creeks").get(0);
                    EmergSite site = (EmergSite) extraInfo.getJSONArray("sites").get(0);
                    Creek.creeks.add(creek);
                    EmergSite.sites.add(site);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }

}
