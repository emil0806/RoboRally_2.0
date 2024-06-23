package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriorityAntennaTest {
    Board board = new Board(8, 8, 3);
    GameController gameController = new GameController(board);

    @Test
    void priorityAntennaTest() {
        Player player1 = new Player(board, null,"Player1", 1);
        player1.setHeading(Heading.NORTH);
        player1.setSpace(gameController.board.getSpace(1,1));
        board.addPlayer(player1);

        Player player2 = new Player(board, null,"Player2", 2);
        player2.setHeading(Heading.NORTH);
        player2.setSpace(gameController.board.getSpace(3,3));
        board.addPlayer(player2);

        PriorityAntenna priorityAntenna = new PriorityAntenna();
        priorityAntenna.doAction(gameController, gameController.board.getSpace(2,2));

        assertEquals(board.getPlayers().get(0), player1);

        gameController.fastThreeForward(player1);

        priorityAntenna.doAction(gameController, gameController.board.getSpace(2,2));

        assertEquals(board.getPlayers().get(0), player2);
    }
}
