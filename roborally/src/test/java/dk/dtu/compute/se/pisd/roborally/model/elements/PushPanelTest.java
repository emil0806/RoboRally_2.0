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
public class PushPanelTest {
    Board board = new Board(8, 8, 3);
    GameController gameController = new GameController(board);

    @Test
    void pushPanelTest() {
        Player player = new Player(board, null,"Player", 3);
        player.setHeading(Heading.NORTH);
        player.setSpace(gameController.board.getSpace(3,3));

        PushPanel pushPanel = new PushPanel();
        pushPanel.setHeading(Heading.NORTH);
        pushPanel.setActivationRegisters(new int[] {1, 3, 5});
        pushPanel.doAction(gameController, gameController.board.getSpace(3,3));

        assertEquals(player.getSpace(), board.getSpace(3,2));
    }
}
