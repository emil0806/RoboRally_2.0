package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

public class PlayerTemplate {
    public String name;
    public String color;

    public SpaceTemplate space;

    public Heading heading;
    public CommandCardFieldTemplate[] program;
    public CommandCardFieldTemplate[] cards;
    public int checkpoints;
}
