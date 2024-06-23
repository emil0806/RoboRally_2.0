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
public class GearsTest {

    Board board = new Board(8, 8, 4);
    GameController gameController = new GameController(board);

    @Test
    void gearsTest() {
        Player player = new Player(board, null,"Player", 3);
        player.setHeading(Heading.SOUTH);
        player.setSpace(gameController.board.getSpace(2,2));

        Gears gears = new Gears();
        gears.setDirection("right");
        gears.doAction(gameController, gameController.board.getSpace(2,2));

        assertEquals(player.getHeading(), Heading.WEST);

        gears.setDirection("left");
        gears.doAction(gameController, gameController.board.getSpace(2,2));

        assertEquals(player.getHeading(), Heading.SOUTH);
    }
}
