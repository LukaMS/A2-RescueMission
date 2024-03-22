package ca.mcmaster.se2aa4.island.team211;

import ca.mcmaster.se2aa4.island.team211.drone.Drone;
import ca.mcmaster.se2aa4.island.team211.locations.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DistanceCalculatorTest {
    @Test
    void testSetSiteXY() {
        Drone mockDrone = new Drone();
        Coordinate coordinate = new Coordinate(10,20);
        mockDrone.emergencySites.put("SiteID", coordinate);
        DistanceCalculator distanceCalculator = new DistanceCalculator(mockDrone);

        distanceCalculator.setSiteXY();

        Assertions.assertEquals(10, distanceCalculator.getSiteX());
        Assertions.assertEquals(20, distanceCalculator.getSiteY());
    }

    @Test
    void testDistanceToSite() {
        Drone mockDrone = new Drone();
        Coordinate coordinate = new Coordinate(3,4);
        mockDrone.emergencySites.put("SiteID", coordinate);
        mockDrone.y_cord = 0;
        mockDrone.x_cord = 0;
        DistanceCalculator distanceCalculator = new DistanceCalculator(mockDrone);
        distanceCalculator.setSiteXY();

        Assertions.assertEquals(5.0,distanceCalculator.distanceToSite(mockDrone));
    }

    @Test
    void testCalculateDistances_DetermineClosest() {
        Drone mockDrone = new Drone();
        Coordinate coordinate1 = new Coordinate(3,4);
        Coordinate coordinate2 = new Coordinate(6,8);

        mockDrone.emergencySites.put("SiteID", coordinate1);
        mockDrone.creeks.put("CreekID", coordinate2);

        DistanceCalculator distanceCalculator = new DistanceCalculator(mockDrone);
        distanceCalculator.calculateDistances();

        Assertions.assertEquals((float) 5.0, distanceCalculator.getDistances().get("CreekID"));
        Assertions.assertEquals("CreekID", distanceCalculator.determineClosest());
    }
}
