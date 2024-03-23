/*
List of available actions the drone can take.
 */
package ca.mcmaster.se2aa4.island.team211.controlcentre;

//The key actions must be written in lowercase letters, or else the game engine does not recognize them properly.
//Custom actions/maneuvers can be written normally in UPPERCASE, since they are not returned to the game engine.
public enum Action {
    fly,
    stop,
    heading,
    echo,
    scan,
    U_TURN,
    U_TURN_2,
    RE_ALIGN,
    RETURN_TO_RADIUS
}