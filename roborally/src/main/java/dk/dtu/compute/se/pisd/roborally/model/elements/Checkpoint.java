package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class Checkpoint extends FieldAction {

    private int checkPointNum;

    public int getCheckPointNum() {
        return checkPointNum;
    }

    public void setCheckPointNum(int num) {
        this.checkPointNum = num;
    }
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        if(space.getPlayer().getCheckpoints() == checkPointNum - 1) {
            space.getPlayer().setCheckpoints(checkPointNum);
            return true;
        }
        return false;
    }
}
