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
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param player player who should be moved
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

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

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

    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

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

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }
    static class ImpossibleMoveException extends Exception {

        public ImpossibleMoveException() {
            super("Move impossible");
        }
    }

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