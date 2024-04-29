package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class PushPanel extends FieldAction {

    private Heading heading;
    private int[] activationRegisters;

    public Heading getHeading() {
        return this.heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public int[] getActivationRegisters() {
        return this.activationRegisters;
    }

    public void setActivationRegisters(int[] activationRegisters) {
        this.activationRegisters = activationRegisters;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        int currentRegister = gameController.board.getStep() + 1;
        boolean shouldActivate = false;

        for (int activationRegister : this.activationRegisters) {
            if (activationRegister == currentRegister) {
                shouldActivate = true;
                break;
            }
        }
        if(shouldActivate) {
            try {
                Space target = gameController.board.getNeighbour(space.getPlayer().getSpace(), this.heading);

                gameController.moveToSpace(space.getPlayer(), target, this.heading);
                return true;
            }
            catch(Exception e){
                return false;
            }
        }
        return false;
    }
}
