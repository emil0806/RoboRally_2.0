package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
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

               Space spaces = gameController.board.getSpace(0,0);

                gameController.moveToSpace(space.getPlayer(),spaces, this.heading);
                return true;
            }
            catch(Exception e){
                return false;
            }

    }
}
//Space tyr = new Space(gameController.board, 0, 0);
//                gameController.moveToSpace(space.getPlayer(),tyr, this.heading);