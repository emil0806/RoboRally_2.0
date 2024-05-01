package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class Gears extends FieldAction {
    private String direction;

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction.toLowerCase();
    }
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        try {
            if(direction.equals("right")) {
                gameController.turnRight(space.getPlayer());
            } else {
                gameController.turnLeft(space.getPlayer());
            }
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
