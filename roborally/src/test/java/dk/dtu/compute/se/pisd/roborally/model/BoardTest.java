package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BoardTest {
    Board board = new Board(8, 8, 2);
    GameController gameController = new GameController(board);

    @Test
    void getNeighborTest() {
        Space startSpace = board.getSpace(4, 4);

        Space northNeighbor = board.getNeighbour(startSpace, Heading.NORTH);
        assertEquals(board.getSpace(4, 3), northNeighbor);

        Space southNeighbor = board.getNeighbour(startSpace, Heading.SOUTH);
        assertEquals(board.getSpace(4, 5), southNeighbor);

        Space eastNeighbor = board.getNeighbour(startSpace, Heading.EAST);
        assertEquals(board.getSpace(5,4), eastNeighbor);

        Space westNeighbor = board.getNeighbour(startSpace, Heading.WEST);
        assertEquals(board.getSpace(3,4), westNeighbor);


        board = LoadBoard.loadBoard("Rotating Maze");
        startSpace = board.getSpace(6, 0);
        southNeighbor = board.getNeighbour(startSpace, Heading.EAST);
        assertNull(southNeighbor);
    }
}
