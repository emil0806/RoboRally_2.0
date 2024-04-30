package dk.dtu.compute.se.pisd.roborally.model.elements;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;


public class PriorityAntenna extends FieldAction {
    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param gameController, controller of the game
     * @param space, the actual space
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        determinePlayerOrder(gameController.board, space.x, space.y);
        return true;
    }

    public void determinePlayerOrder(Board board, int x, int y) {
        // Calculating distance to priority antenna for each player
        for(int i = 0; i < board.getPlayersNumber(); i++) {
            Space tempSpace = board.getPlayer(i).getSpace();
            board.getPlayer(i).setDistanceToPriorityAntenna(Math.abs(tempSpace.x - x) + Math.abs(tempSpace.y - y));
        }

        // Sort the players list
        Player tempPlayer;
        boolean isSorted = false;
        while(!isSorted) {
            isSorted = true;
            for(int i = 0; i < board.getPlayersNumber() - 1; i++) {
                if(board.getPlayers().get(i).getDistanceToPriorityAntenna() > board.getPlayers().get(i + 1).getDistanceToPriorityAntenna()) {
                    tempPlayer = board.getPlayer(i);
                    board.getPlayers().set(i, board.getPlayers().get(i + 1));
                    board.getPlayers().set(i + 1, tempPlayer);
                    isSorted =  false;
                }
            }
        }
    }
}
