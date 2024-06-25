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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.client.Client;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import dk.dtu.compute.se.pisd.roborally.model.elements.PriorityAntenna;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.stream.IntStream;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> SAVE_SLOT_OPTIONS = Arrays.asList("Slot1", "Slot2", "Slot3");
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    final private List<String> boardNames = Arrays.asList("Rotating Maze", "High Octane", "Fractionation", "Death Trap");
    final private RoboRally roboRally;
    private int count = 0;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }


    /*
    * @author David Wellejus s220218
    * @author Emil Lauritzen, s231331@dtu.dk
    */
    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }
            ChoiceDialog<String> dialogS = new ChoiceDialog<>(boardNames.get(0), boardNames);
            dialogS.setTitle("Choose Board");
            dialogS.setHeaderText("Select a board to play:");
            dialogS.setContentText("Available boards:");


            Optional<String> resultS = dialogS.showAndWait();
            if(resultS.isPresent()) {
                Client.uploadGame(resultS.get(), 0, result.get(),0);
                showAvailableGames();
            }
        }
    }
    public void showAvailableGames() {
        ArrayList<ArrayList<String>> listOfGames = Client.getGames();
        roboRally.updateLobbyView(listOfGames);
    }

    public void joinGame(int gameID) {
        TextInputDialog dialogName = new TextInputDialog();
        dialogName.setTitle("Player Information");
        dialogName.setHeaderText("Enter your name");
        Optional<String> resultName = dialogName.showAndWait();
        List<Integer> ages = IntStream.rangeClosed(1, 100).boxed().toList();
        ChoiceDialog<Integer> dialogAge = new ChoiceDialog<>(ages.get(0), ages);
        dialogAge.setTitle("Player Information");
        dialogAge.setHeaderText("Enter your age");
        Optional<Integer> resultAge = dialogAge.showAndWait();
        if(resultName.isPresent() && resultAge.isPresent()) {
            int myPlayerID = Client.getNumOfPlayers(gameID);

            Client.joinGame(gameID, myPlayerID, resultName.get(), resultAge.get());

            Alert waitingToFillGame = new Alert(AlertType.INFORMATION, "Cancel", ButtonType.CANCEL);
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            waitingToFillGame.setTitle("Waiting for players to join game");
            waitingToFillGame.setHeaderText("Game ID: " + gameID);
            String playersName = "";
            for (ArrayList<String> player : Client.getPlayers(gameID)) {
                playersName += player.get(1) + ", ";
            }
            waitingToFillGame.setContentText("Players: " + playersName);
            waitingToFillGame.setOnShown(e -> delay.playFromStart());
            waitingToFillGame.setOnCloseRequest(e -> waitingToFillGame.close());
            delay.setOnFinished(e -> {
                showAvailableGames();
                String playersName2 = "";
                for (ArrayList<String> player : Client.getPlayers(gameID)) {
                    playersName2 += player.get(1) + ", ";
                }
                waitingToFillGame.setContentText("Players: " + playersName2);
                delay.playFromStart();
                if (Client.getNumOfPlayers(gameID) == Client.getMaxNumOfPlayers(gameID)) {
                    waitingToFillGame.close();
                    waitingToFillGame.setResult(ButtonType.OK);
                    delay.stop();
                }
            });
            waitingToFillGame.showAndWait();
            if (waitingToFillGame.getResult() == ButtonType.CLOSE || waitingToFillGame.getResult() == ButtonType.CANCEL) {
                Client.leaveGame(gameID, myPlayerID);
            }
            if(Client.getNumOfPlayers(gameID) == Client.getMaxNumOfPlayers(gameID)) {
                setupGame(gameID, myPlayerID);
            }
        }
    }

    public void setupGame(int gameID, int myPlayerID) {
        ArrayList<String> gameInfo = Client.getGame(gameID);
        Board board = LoadBoard.loadBoard(gameInfo.get(1));
        if (board != null) {
            board.setGameId(gameID);
            gameController = new GameController(board);
            board.setMyPlayerID(myPlayerID);
        }

        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                for (FieldAction fieldAction : board.getSpace(i, j).getActions()) {
                    if (fieldAction instanceof PriorityAntenna) {
                        board.setPriorityAntenna(board.getSpace(i, j));
                    }
                }
            }
        }
        ArrayList<Double> Start_Place = new ArrayList<>(Client.getAvailableStartSpaces(gameID));
        ChoiceDialog<Double> waitingForStartPosition = new ChoiceDialog<>(Start_Place.get(0), Start_Place);
        waitingForStartPosition.setTitle("Waiting for players to choose start position");
        waitingForStartPosition.setHeaderText("Start position");
        waitingForStartPosition.setContentText("Waiting for players to choose start position");
        waitingForStartPosition.setOnCloseRequest(e -> {
            waitingForStartPosition.close(); Client.leaveGame(gameID, myPlayerID);
        });
        List<ArrayList<String>> players = Client.getPlayers(gameID);
        Map<Integer, Integer> playerTurnList = new HashMap<>();
        for (ArrayList<String> player : players) {
            int playerID = Integer.parseInt(player.get(0));
            int age = Integer.parseInt(player.get(2));
            playerTurnList.put(playerID, age);
        }
        List<Map.Entry<Integer, Integer>> sortedPlayers = new ArrayList<>(playerTurnList.entrySet());
        sortedPlayers.sort(Map.Entry.comparingByValue());

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    for(Double place : Client.getRemovedStartingPlace(gameID)) {
                        waitingForStartPosition.getItems().remove(place);
                    }
                    int currentTurnPlayerID = sortedPlayers.get(Client.getTurnID(gameID)).getKey();

                    if (currentTurnPlayerID == myPlayerID) {
                        if (count == 0){
                            waitingForStartPosition.setSelectedItem(Client.getAvailableStartSpaces(gameID).get(0));
                            count ++;
                        }
                        waitingForStartPosition.getDialogPane().lookup(".combo-box").setDisable(false);
                        for (ButtonType buttonType : waitingForStartPosition.getDialogPane().getButtonTypes()) {
                            Node button = waitingForStartPosition.getDialogPane().lookupButton(buttonType);
                            button.setDisable(false);
                        }
                        waitingForStartPosition.setContentText("It is your turn to choose");
                        waitingForStartPosition.setTitle("Choose place to start");
                        waitingForStartPosition.setHeaderText("Select place to start");
                    } else {
                        waitingForStartPosition.getDialogPane().lookup(".combo-box").setDisable(true);
                        for (ButtonType buttonType : waitingForStartPosition.getDialogPane().getButtonTypes()) {
                            Node button = waitingForStartPosition.getDialogPane().lookupButton(buttonType);
                            button.setDisable(true);
                        }
                        waitingForStartPosition.setContentText("Waiting for players to choose start position");
                    }
                });
            }
        };

        timer.schedule(task, 0, 2000);

        Optional<Double> result = waitingForStartPosition.showAndWait();
        result.ifPresent(sec -> {
            Client.setStartSpace(gameID, myPlayerID, result.get());
            Client.setAvailableStartSpaces(gameID, sec);
            Client.setTurnID(gameID);
            timer.cancel();
        });
        Alert waitingAlert = new Alert(AlertType.WARNING);
        waitingAlert.setTitle("Waiting for Players");
        waitingAlert.setHeaderText(null);
        waitingAlert.getDialogPane().getButtonTypes().clear();  // Remove all buttons
        waitingAlert.setContentText("Waiting for the others to choose a start place");
        waitingAlert.show();
        Client.waitForAllUsersToBeReady(gameID).thenAccept(allReady -> {
            if (allReady) {
                Platform.runLater(() -> {
                    waitingAlert.setResult(ButtonType.OK);
                    waitingAlert.close();
                    createPlayers(board, gameID);
                    gameController.startProgrammingPhase();
                    roboRally.createBoardView(gameController);
                });
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
    public void createPlayers(Board board, int gameID) {
        ArrayList<ArrayList<String>> players = Client.getPlayers(gameID);
        for (int i = 0; i < players.size(); i++) {
            ArrayList<String> playerInfo = players.get(i);
            Player player = new Player(board, PLAYER_COLORS.get(i), playerInfo.get(1), Integer.parseInt(playerInfo.get(0)));
            Double startSpace = Double.parseDouble(playerInfo.get(3));
            int x = startSpace.intValue();
            int y = (int) Math.round((startSpace - x) * 10); // Convert decimal part to y
            player.setStartSpace(board.getSpace(x, y));
            player.setSpace(board.getSpace(x, y));
            board.addPlayer(player);
        }
    }

    public void saveGame() {
        // XXX needs to be implemented eventually
        if(this.gameController != null) {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(SAVE_SLOT_OPTIONS.get(0), SAVE_SLOT_OPTIONS);
            dialog.setTitle("Save game");
            dialog.setHeaderText("Choose a saving slot");
            Optional<String> result = dialog.showAndWait();
            if(result.isPresent()) {
                LoadBoard.saveBoard(this.gameController.board, result.get());
            }
        }
    }

    public void loadGame() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(SAVE_SLOT_OPTIONS.get(0), SAVE_SLOT_OPTIONS);
        dialog.setTitle("Load game");
        dialog.setHeaderText("Choose loading slot");
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) {
            Board board = LoadBoard.loadBoard(result.get());
            gameController = new GameController(board);
            gameController.startProgrammingPhase();
            roboRally.createBoardView(gameController);
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }

    static void showWinner(Player player, Board board) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if(player.getPlayerID() == board.getMyPlayerID()) {
            alert.setTitle("WINNER");
            alert.setContentText("Congratulations!\n" + "You have won the game!");
        } else {
            alert.setTitle("LOSER");
            alert.setContentText("You lost!\n" + player.getName() + " have won the game!");
        }
        alert.showAndWait();
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }
}
