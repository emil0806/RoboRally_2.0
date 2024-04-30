package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 * @author Emil Lauritzen, s231331@dtu.dk
 * Checkpoint class
 */
public class Checkpoint extends FieldAction {

    private int checkpointNum;

    public int getCheckPointNum() {
        return checkpointNum;
    }

    public void setCheckPointNum(int num) {
        this.checkpointNum = num;
    }
    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param gameController, controller of the game
     * @param space, the actual space
     * @return depends on if action is possible
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        if(space.getPlayer().getCheckpoints() == checkpointNum - 1) {
            space.getPlayer().setCheckpoints(checkpointNum);
            return true;
        }
        return false;
    }
}
