package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.elements.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.elements.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.elements.Gears;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LoadTest {
    @Test
    void loadTest() {
        Board board = LoadBoard.loadBoard("testBoard");

        GameController gameController = new GameController(board);

        assertEquals(board.width, 8);
        assertEquals(board.height, 8);

        assertEquals(board.getSpace(0,0).getWalls().size(), 1);
        assertEquals(board.getSpace(0,0).getWalls().get(0), Heading.SOUTH);

        assert(board.getSpace(0,0).getActions().get(0) instanceof ConveyorBelt);
        assertEquals(((ConveyorBelt)board.getSpace(0,0).getActions().get(0)).getHeading(), Heading.WEST);

        assertEquals(board.getSpace(2,2).getWalls().size(), 1);
        assertEquals(board.getSpace(2,2).getWalls().get(0), Heading.NORTH);

        assert(board.getSpace(2,2).getActions().get(0) instanceof Gears);
        assertEquals(((Gears)board.getSpace(2,2).getActions().get(0)).getDirection(), "left");
    }

}