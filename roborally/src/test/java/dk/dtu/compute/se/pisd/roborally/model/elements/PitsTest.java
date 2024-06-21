package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PitsTest {
    Board board = new Board(8, 8, 2);
    GameController gameController = new GameController(board);

    @Test
    void pitsTest() {
        Player player = new Player(board, null,"Player", 0);
        player.setHeading(Heading.SOUTH);
        player.setStartSpace(gameController.board.getSpace(1,1));

        player.setSpace(gameController.board.getSpace(5,5));

        assertEquals(player.getSpace().x, 5);
        assertEquals(player.getSpace().y, 5);

        Pits pits = new Pits();
        pits.doAction(gameController, board.getSpace(5,5));

        assertEquals(player.getSpace().x, 1);
        assertEquals(player.getSpace().y, 1);
    }
}
