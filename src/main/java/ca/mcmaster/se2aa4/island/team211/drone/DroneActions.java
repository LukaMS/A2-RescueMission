/*
Actions needed to keep track of the Drone's coordinates as it performs different actions
 */
package ca.mcmaster.se2aa4.island.team211.drone;
import ca.mcmaster.se2aa4.island.team211.controlcentre.IslandFinder;
import ca.mcmaster.se2aa4.island.team211.locations.Coordinate;
import org.json.JSONObject;

public class DroneActions {
    public JSONObject getDecision(Drone drone){
        try { getSides(drone);}
        catch(Exception e){e.printStackTrace();}
        return drone.decisionMaker.makeDecision();
    }

    public static Coordinate getCoordinates(Drone drone) {
        return new Coordinate(drone.x_cord, drone.y_cord);
    }

    public void getSides(Drone drone){
        switch (drone.getDirection()){
            case "N": {drone.left = "W";drone.right = "E"; break;}
            case "S": {drone.left = "E";drone.right = "W"; break;}
            case "E": {drone.left = "N";drone.right = "S"; break;}
            case "W": {drone.left = "S";drone.right = "N"; break;}
            default:
        }
    }

    public void setStart(Drone drone){
        switch (drone.getDirection()){
            case "N", "S":
                drone.x_cord = drone.radar.range+1;
                drone.y_cord = 1;
                break;
            case "E", "W":
                drone.y_cord = drone.radar.range+1;
                drone.x_cord = 1;
                break;
            default:
        }
        drone.decisionMaker = new IslandFinder(drone);
    }

    public Integer[] printCoords(Drone drone){
        Integer[] coords = new Integer[2];
        coords[0] = drone.x_cord;
        coords[1] = drone.y_cord;
        return coords;
    }

    public void turnLeft(Drone drone){
        switch  (drone.getDirection()){
            case "N" : {
                drone.x_cord -= 1;
                drone.y_cord -= 1;
                drone.setDirection("W");
                break;
            }
            case "E" : {
                drone.x_cord += 1;
                drone.y_cord -= 1;
                drone.setDirection("N");
                break;
            }
            case "S" : {
                drone.x_cord += 1;
                drone.y_cord += 1;
                drone.setDirection("E");
                break;
            }
            case "W" : {
                drone.x_cord -= 1;
                drone.y_cord += 1;
                drone.setDirection("S");
                break;
            }
            default:
        }
    }

    public void turnRight(Drone drone){
        switch  (drone.getDirection()){
            case "N" : {
                drone.x_cord += 1;
                drone.y_cord -= 1;
                drone.setDirection("E");
                break;
            }
            case "E" : {
                drone.x_cord += 1;
                drone.y_cord += 1;
                drone.setDirection("S");
                break;
            }
            case "S" : {
                drone.x_cord -= 1;
                drone.y_cord += 1;
                drone.setDirection("W");
                break;
            }
            case "W" :{
                drone.x_cord -= 1;
                drone.y_cord -= 1;
                drone.setDirection("N");
                break;
            }
            default:
        }
    }

    public void forward(Drone drone){
        switch (drone.getDirection()){
            case "N" : drone.y_cord--; break;
            case "E" : drone.x_cord++; break;
            case "S" : drone.y_cord++; break;
            case "W" : drone.x_cord--; break;
            default:
        }
    }
}
