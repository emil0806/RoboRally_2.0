/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ...
 * @author Emil Lauritzen, s231331@dtu.dk
 * ConveyorBelt class
 */
public class ConveyorBelt extends FieldAction {

    private int arrows;
    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public int getArrows() {
        return this.arrows;
    }
    public void setArrows(int arrows) {
        this.arrows = arrows;
    }
    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param gameController, space
     * @return depends on if action is possible
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        try {
            Space target = gameController.board.getNeighbour(space.getPlayer().getSpace(), this.heading);

            gameController.moveToSpace(space.getPlayer(), target, this.heading);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
