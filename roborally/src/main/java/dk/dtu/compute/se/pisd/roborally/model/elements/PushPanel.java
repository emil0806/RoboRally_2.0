package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 * @author Emil Lauritzen, s231331@dtu.dk
 * PushPanel class
 */
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

    /**
     * ...
     *
     * @param gameController, the controller for the game
     * @param space,          the actual space
     * @return depends on if action is possible
     * @author Emil Lauritzen, s231331@dtu.dk
     */
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
        if (shouldActivate) {
            try {
                Space targetSpace = gameController.board.getNeighbour(space, this.heading);
                if (targetSpace == null) {
                    space.getPlayer().setSpace(space.getPlayer().getStartSpace());
                } else {
                    gameController.moveToSpace(space.getPlayer(), targetSpace, this.heading);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
