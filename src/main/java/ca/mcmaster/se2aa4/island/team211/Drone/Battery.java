package ca.mcmaster.se2aa4.island.team211.Drone;

public class Battery {
    public int batteryLevel;

    public void discharge(Integer value){
        batteryLevel -= value;
    }

}
