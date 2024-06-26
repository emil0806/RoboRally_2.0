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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.client.Client;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(Board board) {
        this.board = board;
    }


    /**
     * Moves the player forward in the direction they are currently facing.
     * If the player encounters the edge of the board or a pit, they are moved back to their starting space.
     * If the target space is valid, the player is moved to that space.
     * @author Klavs Medvee Pommer Blankensteiner s213383
     * @param player the player to be moved forward
     * @return void This method does not return a value but moves the player on the board.
     */

    public void moveForward(@NotNull Player player) {
        if (player.board == board && !player.isSentToStartSpace()) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Moves the player forward twice in the direction they are currently facing.
     * If the player encounters a pit after the first move, they are moved back to their starting space.
     * If the player encounters a pit after the second move, they are moved back to their starting space and their heading is set to south.
     * @author Emil Leonhard Lauritzen s231331
     * @param player the player to be moved forward twice
     * @return void This method does not return a value but moves the player on the board.
     */

    public void fastForward(@NotNull Player player) {
            moveForward(player);
            moveForward(player);
    }

    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param player player whose heading should turn right
     */
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
    }

    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param player player whose heading should turn left
     */
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
    }

    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param player player who should move three forward
     */
    public void fastThreeForward(@NotNull Player player) {
            moveForward(player);
            moveForward(player);
            moveForward(player);
    }

    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param player player who should move back
     */
    public void moveBackward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading().next().next();

            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here  for now; we just catch the
                    // exception so that we do no pass it on to the caller
                    // (which would be very bad style).
                }
            }
        }
    }

    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param player player who should make U-Turn
     */
    public void makeUTurn(@NotNull Player player) {
        turnLeft(player);
        turnLeft(player);
    }

    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param player player whose programming should be repeated
     */
    public void repeatPrevProgramming(@NotNull Player player) {
        if (board.getStep() != 0) {
            int i = board.getStep();
            int j = 0;
            if(player.getProgramField(i - 1).getCard() != null) {
                if(player.getProgramField(i - 1).getCard().command == Command.OPTION_LEFT_RIGHT) {
                    setupMoves();
                }
                while (player.getProgramField(i).getCard().command == Command.AGAIN && board.getStep() != 0) {
                    if(i > 0) {
                        i--;
                        j++;
                    } else {
                        return;
                    }
                }
                executeCommand(player, player.getProgramField(board.getStep() - j).getCard().command);
            }
        }
    }


    /**
     * Moves the player to the specified space in the given heading direction.
     * If another player occupies the target space, it attempts to move the other player to the next space in the same direction.
     * If the next space is invalid, the other player is moved to their starting space.
     * @author Klavs Medvee Pommer Blankensteiner s213383
     * @param player the player to be moved
     * @param space the target space to move the player to
     * @param heading the direction the player is moving
     * @throws ImpossibleMoveException if the move cannot be completed
     * @return void This method does not return a value but moves the player on the board.
     */

    public void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space;
        Player other = space.getPlayer();
        if (other != null) {
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                moveToSpace(other, target, heading);
                assert target.getPlayer() == null : target;
            }
        }
        player.setSpace(space);
        space.setPlayer(player);
    }


    /**
     * ...
     * //@author David Wellejus, s220218@dtu.dk
     * //@param space player who should be moved
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        if(space.getPlayer() == null){
            Player currentPlayer = board.getCurrentPlayer();

            if(currentPlayer.getSpace() != null){
                currentPlayer.getSpace().setPlayer(null);
            }
            currentPlayer.setSpace(space);
            space.setPlayer(currentPlayer);
        }
    }

    /**
     * Makes the program fields visible for all players at the specified register.
     * This method sets the visibility of the command card field for each player based on the given register index.
     * @author Emil Leonhard Lauritzen s231331
     * @param register the index of the program register to make visible
     * @return void This method does not return a value but changes the visibility of the program fields.
     */

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * Makes all program fields invisible for all players.
     * This method sets the visibility of all command card fields to false for each player.
     * @author Emil Leonhard Lauritzen s231331
     * @return void This method does not return a value but changes the visibility of the program fields.
     */

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }



    /**
     * Completes the programming phase by making the program fields invisible,
     * setting the phase to activation, and uploading the chosen moves for each player to the server.
     * It waits for all players to choose their moves before proceeding.
     * @author David Kasper Vilmann Wellejus s220218
     * @return void This method does not return a value but performs necessary updates to transition to the next game phase.
     */

    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for(Player player : board.getPlayers()){
            ArrayList<String> chosenMoves = player.getChosenMoves();
            if(board.getMyPlayerID() == player.getPlayerID()){
                Client.uploadMoves(chosenMoves, player.getPlayerID(), board.getGameId());
            }
        }

        Alert waitForAllMovesToBeChosen = new Alert(Alert.AlertType.WARNING);
        waitForAllMovesToBeChosen.setTitle("RoboRally");
        waitForAllMovesToBeChosen.setHeaderText(null);
        waitForAllMovesToBeChosen.getDialogPane().getButtonTypes().clear();
        waitForAllMovesToBeChosen.setContentText("Waiting for all players to choose their moves");
        waitForAllMovesToBeChosen.show();

        new Thread(() -> {
            if(Client.waitForAllUsersChosen(board.getGameId())){
                Platform.runLater(() -> {
                    waitForAllMovesToBeChosen.setResult(ButtonType.OK);
                    waitForAllMovesToBeChosen.close();
                    for(Player player : board.getPlayers()) {
                        ArrayList<String> playerMoves = Client.getMovesByPlayerID(board.getGameId(), player.getPlayerID());
                        int i = 0;
                        assert playerMoves != null;
                        for(String move : playerMoves){
                            player.getProgramField(i).setCard(new CommandCard(convertToCommand(move)));
                            i++;
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Sets up the moves for each player by retrieving their chosen moves from the server
     * and updating their program fields with the corresponding command cards.
     * @author David Kasper Vilmann Wellejus s220218
     * @return void This method does not return a value but initializes the program fields for each player with their moves.
     */

    public void setupMoves() {
        for(Player player : board.getPlayers()) {
            ArrayList<String> playerMoves = Client.getMovesByPlayerID(board.getGameId(), player.getPlayerID());
            int i = 0;
            assert playerMoves != null;
            for(String move : playerMoves){
                player.getProgramField(i).setCard(new CommandCard(convertToCommand(move)));
                i++;
            }
        }
    }

    /**
     * Executes the program cards for all players by setting the board to non-step mode
     * and initiating the continuation of program execution.
     * @author Emil Leonhard Lauritzen s231331
     * @return void This method does not return a value but starts the execution of player programs.
     */

    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Executes a single step of the program cards for all players by setting the board to step mode
     * and initiating the continuation of program execution.
     * @author Emil Leonhard Lauritzen s231331
     * @return void This method does not return a value but starts the execution of a single step of player programs.
     */

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * Continues the execution of program cards for all players.
     * This method repeatedly executes the next step while the board is in the activation phase
     * and not in step mode.
     * @author Emil Leonhard Lauritzen s231331
     * @return void This method does not return a value but continues the execution of player programs.
     */

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Executes the next step of the current player's program.
     * This method handles the activation phase, executing commands for each player in sequence,
     * and manages the transition to the player interaction phase if necessary.
     * It also updates the current player and step, performs field actions, and checks for a winner.
     * @author Emil Leonhard Lauritzen s231331
     * @return void This method does not return a value but progresses the game state through program execution.
     */

    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if(command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                    if(board.getPhase() == Phase.PLAYER_INTERACTION) {
                        return;
                    }
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                board.getCurrentPlayer().setSentToStartSpace(false);
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    for (int i = 0; i < board.getPlayersNumber(); i++) {
                        for (FieldAction action : board.getPlayer(i).getSpace().getActions()) {
                            action.doAction(this, board.getPlayer(i).getSpace());
                        }
                    }
                    if(checkForWinner()) {
                        return;
                    }
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        Client.incrementPlayersReady(board.getGameId());
                        Client.incrementPlayerRoundNumber(board.getGameId(), board.getMyPlayerID());
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * Executes the given command for the specified player.
     * This method performs the corresponding action based on the command type,
     * such as moving forward, turning, or performing special movements.
     * @author Emil Leonhard Lauritzen s231331
     * @param player the player for whom the command is to be executed
     * @param command the command to be executed
     * @return void This method does not return a value but executes the specified command for the player.
     */

    private void executeCommand(@NotNull Player player, Command command) {
        if (player.board == board && command != null) {
            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case FAST_THREE_FORWARD:
                    this.fastThreeForward(player);
                    break;
                case BACKWARD:
                    this.moveBackward(player);
                    break;
                case U_TURN:
                    this.makeUTurn(player);
                    break;
                case AGAIN:
                    this.repeatPrevProgramming(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     * Executes a given command option for the current player during the player interaction phase.
     * This method transitions the game phase back to activation, executes the command, and updates the current player and step.
     * If all steps are completed, it increments the round number and starts a new programming phase.
     * If not in step mode, it continues the execution of programs.
     * @author Emil Leonhard Lauritzen s231331
     * @param option the command option to be executed
     * @return void This method does not return a value but executes the specified command option and progresses the game state.
     */

    public void executeCommandOption(@NotNull Command option) {
        Player currentPlayer = board.getCurrentPlayer();
        int step = board.getStep();
        if(currentPlayer != null && board.getPhase() == Phase.PLAYER_INTERACTION) {
            board.setPhase(Phase.ACTIVATION);
            executeCommand(currentPlayer, option);
            int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
            if (nextPlayerNumber < board.getPlayersNumber()) {
                board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
            } else {
                step++;
                if (step < Player.NO_REGISTERS) {
                    makeProgramFieldsVisible(step);
                    board.setStep(step);
                    board.setCurrentPlayer(board.getPlayer(0));
                } else {
                    Client.incrementPlayersReady(board.getGameId());
                    Client.incrementPlayerRoundNumber(board.getGameId(), board.getMyPlayerID());
                    startProgrammingPhase();
                }
            }
            if(!board.isStepMode()) {
                continuePrograms();
            }
        }
    }

    /**
     * Moves a command card from the source field to the target field.
     * If the source field has a card and the target field is empty, the card is moved and the source is cleared.
     * @author Klavs Medvee Pommer Blankensteiner s213383
     * @param source the command card field from which the card is to be moved
     * @param target the command card field to which the card is to be moved
     * @return boolean true if the card was successfully moved; false otherwise
     */

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Initiates the programming phase of the game.
     * This method waits for all players to be ready, sets the game phase to programming, and initializes the command card fields for each player.
     * It also clears all previous moves and sets up the current player and step.
     * @author Emil Leonhard Lauritzen s231331
     * @return void This method does not return a value but starts the programming phase and prepares the game state.
     */

    public void startProgrammingPhase() {
        Alert waitingForSameRound = new Alert(Alert.AlertType.WARNING);
        waitingForSameRound.setTitle("RoboRally");
        waitingForSameRound.setHeaderText(null);
        waitingForSameRound.getDialogPane().getButtonTypes().clear();
        waitingForSameRound.setContentText("Waiting for all opponents to be ready");
        waitingForSameRound.show();
        Client.allPlayersAtSameRound(board.getGameId()).thenAccept(allReady -> {
            if (allReady) {
                Platform.runLater(() -> {
                    waitingForSameRound.setResult(ButtonType.OK);
                    waitingForSameRound.close();
                });
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
        board.setPhase(Phase.PROGRAMMING);
        board.getPriorityAntenna().getActions().get(0).doAction(this, board.getPriorityAntenna());
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
        Client.clearAllMoves(board.getGameId());
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    /**
     * Generates a random command card.
     * This method selects a random command from the available commands and creates a command card with it.
     * @author Emil Leonhard Lauritzen s231331
     * @return CommandCard a randomly generated command card
     */

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Exception thrown when a move is impossible to execute.
     * This exception is used to indicate that a player's move cannot be completed due to game rules or conditions.
     * @author Emil Leonhard Lauritzen s231331
     */
    static class ImpossibleMoveException extends Exception {

        public ImpossibleMoveException() {
            super("Move impossible");
        }
    }

    /**
     * Checks if any player has reached all checkpoints and declares the winner.
     * This method iterates through all players to check if any player has collected all checkpoints.
     * If a winner is found, the game phase is set to finished, the winner is set, and a winner message is displayed.
     * @author Emil Leonhard Lauritzen s231331
     * @return boolean true if a winner is found; false otherwise
     */

    public boolean checkForWinner() {
        for(Player player : board.getPlayers()) {
            if(player.getCheckpoints() == board.getNumOfCheckpoints()) {
                board.setPhase(Phase.FINISHED);
                board.setWinner(player);
                AppController.showWinner(player, board);
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a string representation of a move to its corresponding Command enum.
     * This method maps specific move strings to their respective Command values.
     * @author David Kasper Vilmann Wellejus s220218
     * @param move the string representation of the move
     * @return Command the corresponding Command enum value; null if the move string does not match any command
     */

    public Command convertToCommand(String move){

        return switch (move) {
            case "Fwd" -> Command.FORWARD;
            case "Turn Right" -> Command.RIGHT;
            case "Turn Left" -> Command.LEFT;
            case "Fast Fwd" -> Command.FAST_FORWARD;
            case "Fast 3 Fwd" -> Command.FAST_THREE_FORWARD;
            case "Move back" -> Command.BACKWARD;
            case "Make a U-Turn" -> Command.U_TURN;
            case "Repeat programming of previous register" -> Command.AGAIN;
            case "Left OR Right" -> Command.OPTION_LEFT_RIGHT;
            default -> null;
        };
    }
}