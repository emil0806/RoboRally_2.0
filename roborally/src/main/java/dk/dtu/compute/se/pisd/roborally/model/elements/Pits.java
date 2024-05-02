package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class Pits extends FieldAction {

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
            try {
                space.getPlayer().setSpace(space.getPlayer().getStartSpace());
                return true;
            }
            catch(Exception e){
                return false;
            }

    }
}