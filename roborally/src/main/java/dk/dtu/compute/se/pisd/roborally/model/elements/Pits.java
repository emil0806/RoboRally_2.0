package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class Pits extends FieldAction {

    private Heading heading;

    public Heading getHeading() {
        return this.heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
            try {
                gameController.moveToSpace(space.getPlayer(),space.getPlayer().getStartSpace(), this.heading);
                return true;
            }
            catch(Exception e){
                return false;
            }

    }
}