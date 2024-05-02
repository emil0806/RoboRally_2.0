package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ...
 * @author Emil Lauritzen, s231331@dtu.dk
 *
 */
public class CheckpointTest {

    Board board = new Board(8, 8);
    GameController gameController = new GameController(board);

    @Test
    void checkpointTest() {
        Player player = new Player(board, null,"Player");
        player.setHeading(Heading.NORTH);
        player.setSpace(gameController.board.getSpace(1,1));

        assertEquals(player.getCheckpoints(), 0);

        Checkpoint checkpoint = new Checkpoint();
        checkpoint.setCheckPointNum(1);
        checkpoint.doAction(gameController, gameController.board.getSpace(1,1));

        assertEquals(player.getCheckpoints(), 1);
    }
}
