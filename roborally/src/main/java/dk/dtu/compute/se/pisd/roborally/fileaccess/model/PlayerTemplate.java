package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.Heading;

/**
 * ...
 * @author Emil Lauritzen, s231331@dtu.dk
 */
public class PlayerTemplate {
    public String name;
    public String color;
    public int playerID;
    public SpaceTemplate space;

    public Heading heading;
    public CommandCardFieldTemplate[] program;
    public CommandCardFieldTemplate[] cards;
    public int checkpoints;
}
