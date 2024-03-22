/*
Keeps track of the Battery level of the Drone.
 */

package ca.mcmaster.se2aa4.island.team211.drone;
public class Battery {
    public int batteryLevel;

    public void discharge(Integer value){
        if (batteryLevel - value > 0) {
            batteryLevel -= value;
        }else{
            batteryLevel = 0;
        }
    }

}
