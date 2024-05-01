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
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import dk.dtu.compute.se.pisd.roborally.model.elements.PriorityAntenna;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    final private List<String> boardNames = Arrays.asList("defaultboard", "testBoard");
    private List<Double> Start_Place = new ArrayList<>(Arrays.asList(1.1, 2.0, 3.1, 4.1, 5.0, 7.1));

    final private RoboRally roboRally;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

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
            resultS.ifPresent(boardName -> {
                Board board = LoadBoard.loadBoard(boardName);
                gameController = new GameController(board);

                for(int i = 0; i < board.width; i++) {
                    for(int j = 0; j < board.height; j++) {
                        for(FieldAction fieldAction : board.getSpace(i, j).getActions()) {
                            if(fieldAction instanceof PriorityAntenna) {
                                board.setPriorityAntenna(board.getSpace(i, j));
                            }
                        }
                    }
                }
                int no = result.get();
                for (int i = 0; i < no; i++) {
                    Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                    ChoiceDialog<Double> choose = new ChoiceDialog<>(Start_Place.get(0), Start_Place);
                    choose.setTitle("Choose place to start ");
                    choose.setHeaderText(" Player number " + (i + 1)  + " \n Select place to start: ");
                    Optional<Double> startPlace = choose.showAndWait();
                    board.addPlayer(player);
                    Double sec = startPlace.get();
                    int x = sec.intValue();
                    int y = (int) Math.round((sec -x) * 10); // Convert decimal part to y
                    player.setSpace(board.getSpace(x, y));
                    Start_Place.remove(sec);
                }
                gameController.startProgrammingPhase();

                roboRally.createBoardView(gameController);
            });
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

            if (!result.isPresent() || result.get() != ButtonType.OK) {
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


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }
}
