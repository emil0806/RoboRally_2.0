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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.client.Client;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PlayerView extends Tab implements ViewObserver {

    private Player player;

    private VBox top;

    private Label programLabel;
    private GridPane programPane;
    private Label cardsLabel;
    private GridPane cardsPane;

    private CardFieldView[] programCardViews;
    private CardFieldView[] cardViews;

    private VBox buttonPanel;

    private Button finishButton;
    private Button executeButton;
    private Button stepButton;

    private VBox playerInteractionPanel;

    private GameController gameController;
    private Timer timer;
    private TimerTask task;

    /**
     * Constructs a PlayerView for the given GameController and Player.
     * Initializes the view with the player's name, color, program fields, command cards, and buttons.
     * Attaches this view as an observer to the player's board and updates the view.
     * @author Emil Leonhard Lauritzen s231331
     * @param gameController the game controller managing the game
     * @param player the player associated with this view
     */
    public PlayerView(@NotNull GameController gameController, @NotNull Player player) {
        super(player.getName());
        this.setStyle("-fx-text-base-color: " + player.getColor() + ";");

        top = new VBox();
        this.setContent(top);

        this.gameController = gameController;
        this.player = player;

        programLabel = new Label("Program");

        programPane = new GridPane();
        programPane.setVgap(2.0);
        programPane.setHgap(2.0);
        programCardViews = new CardFieldView[Player.NO_REGISTERS];
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            CommandCardField cardField = player.getProgramField(i);
            if (cardField != null) {
                programCardViews[i] = new CardFieldView(gameController, cardField);
                programPane.add(programCardViews[i], i, 0);
            }
        }

        // XXX  the following buttons should actually not be on the tabs of the individual
        //      players, but on the PlayersView (view for all players). This should be
        //      refactored.

        finishButton = new Button("Finish Programming");
        finishButton.setOnAction( e -> gameController.finishProgrammingPhase());

        executeButton = new Button("Execute Program");
        executeButton.setOnAction( e-> gameController.executePrograms());

        stepButton = new Button("Execute Current Register");
        stepButton.setOnAction( e-> gameController.executeStep());

        buttonPanel = new VBox(finishButton, executeButton, stepButton);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);
        buttonPanel.setSpacing(3.0);
        // programPane.add(buttonPanel, Player.NO_REGISTERS, 0); done in update now

        playerInteractionPanel = new VBox();
        playerInteractionPanel.setAlignment(Pos.CENTER_LEFT);
        playerInteractionPanel.setSpacing(3.0);

        cardsLabel = new Label("Command Cards");
        cardsPane = new GridPane();
        cardsPane.setVgap(2.0);
        cardsPane.setHgap(2.0);
        cardViews = new CardFieldView[Player.NO_CARDS];
        for (int i = 0; i < Player.NO_CARDS; i++) {
            CommandCardField cardField = player.getCardField(i);
            if (cardField != null) {
                cardViews[i] = new CardFieldView(gameController, cardField);
                cardsPane.add(cardViews[i], i, 0);
            }
        }

        top.getChildren().add(programLabel);
        top.getChildren().add(programPane);
        top.getChildren().add(cardsLabel);
        top.getChildren().add(cardsPane);

        if (player.board != null) {
            player.board.attach(this);
            update(player.board);
        }

    }

    /**
     * Updates the view based on changes in the observed subject.
     * Adjusts the background colors of the program card fields and handles the visibility and state of action buttons.
     * Manages the interaction phase by displaying interaction options or waiting for opponent interactions.
     * @author Emil Leonhard Lauritzen s231331
     * @param subject the subject being observed for changes
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == player.board) {
            for (int i = 0; i < Player.NO_REGISTERS; i++) {
                CardFieldView cardFieldView = programCardViews[i];
                if (cardFieldView != null) {
                    if (player.board.getPhase() == Phase.PROGRAMMING ) {
                        cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                    } else {
                        if (i < player.board.getStep()) {
                            cardFieldView.setBackground(CardFieldView.BG_DONE);
                        } else if (i == player.board.getStep()) {
                            if (player.board.getCurrentPlayer() == player) {
                                cardFieldView.setBackground(CardFieldView.BG_ACTIVE);
                            } else if (player.board.getPlayerNumber(player.board.getCurrentPlayer()) > player.board.getPlayerNumber(player)) {
                                cardFieldView.setBackground(CardFieldView.BG_DONE);
                            } else {
                                cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                            }
                        } else {
                            cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                        }
                    }
                }
            }

            if (player.board.getPhase() != Phase.PLAYER_INTERACTION) {
                if (!programPane.getChildren().contains(buttonPanel)) {
                    programPane.getChildren().remove(playerInteractionPanel);
                    programPane.add(buttonPanel, Player.NO_REGISTERS, 0);
                }

                cancelTimer();

                switch (player.board.getPhase()) {
                    case INITIALISATION:
                        finishButton.setDisable(true);
                        // XXX just to make sure that there is a way for the player to get
                        //     from the initialization phase to the programming phase somehow!
                        executeButton.setDisable(false);
                        stepButton.setDisable(true);
                        break;

                    case PROGRAMMING:
                        timer = new Timer();
                        task = new TimerTask() {
                            @Override
                            public void run() {
                                Platform.runLater(() -> {
                                    finishButton.setDisable(!allProgramSlotsFilled());
                                });
                            }
                        };
                        timer.schedule(task, 0, 500);

                        executeButton.setDisable(true);
                        stepButton.setDisable(true);
                        break;

                    case ACTIVATION:
                        finishButton.setDisable(true);
                        executeButton.setDisable(false);
                        stepButton.setDisable(false);
                        break;

                    default:
                        finishButton.setDisable(true);
                        executeButton.setDisable(true);
                        stepButton.setDisable(true);
                }

            } else if (player.board.getPhase() == Phase.PLAYER_INTERACTION) {
                if(player.getPlayerID() == gameController.board.getCurrentPlayer().getPlayerID()) {
                    if (!programPane.getChildren().contains(playerInteractionPanel)) {
                        programPane.getChildren().remove(buttonPanel);
                        programPane.add(playerInteractionPanel, Player.NO_REGISTERS, 0);
                    }
                    playerInteractionPanel.getChildren().clear();

                    if (player.board.getCurrentPlayer() == player) {

                        Button optionButton = new Button("Left");
                        optionButton.setOnAction( e -> {
                             Client.sendInteraction(gameController.board.getGameId(), player.getPlayerID(), gameController.board.getStep(), "Turn Left"); gameController.executeCommandOption(Command.LEFT);
                        });
                        optionButton.setDisable(false);
                        playerInteractionPanel.getChildren().add(optionButton);

                        optionButton = new Button("Right");
                        optionButton.setOnAction( e -> {
                             Client.sendInteraction(gameController.board.getGameId(), player.getPlayerID(), gameController.board.getStep(), "Turn Right"); gameController.executeCommandOption(Command.RIGHT);
                        });
                        optionButton.setDisable(false);
                        playerInteractionPanel.getChildren().add(optionButton);
                    }
                } else {
                    Alert waitingForInteraction = new Alert(Alert.AlertType.WARNING);
                    waitingForInteraction.setTitle("RoboRally");
                    waitingForInteraction.setHeaderText(null);
                    waitingForInteraction.getDialogPane().getButtonTypes().clear();  // Remove all buttons
                    waitingForInteraction.setContentText("Waiting for an opponent to choose interaction");
                    waitingForInteraction.show();

                    Client.waitForInteraction(gameController.board.getGameId(), gameController.board.getCurrentPlayer().getPlayerID(), gameController.board.getStep()).thenAccept(allReady -> {
                        if (allReady) {
                            Platform.runLater(() -> {
                                waitingForInteraction.setResult(ButtonType.OK);
                                waitingForInteraction.close();
                                gameController.setupMoves();
                                gameController.board.setPhase(Phase.ACTIVATION);
                                if (gameController.board.isStepMode()) {
                                    gameController.executeStep();
                                } else {
                                    gameController.executePrograms();
                                }
                            });
                        }
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
                }
            }
        }
        if (timer != null && player.board.getPhase() != Phase.PROGRAMMING) {
            timer.cancel();
        }

    }

    /**
     * Checks if all program slots for the player are filled with command cards.
     * @author David Kasper Vilmann Wellejus s220218
     * @return boolean true if all program slots are filled, false otherwise
     */
    private boolean allProgramSlotsFilled() {
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            if (player.getProgramField(i).getCard() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cancels the timer if it is running and sets it to null.
     * @author David Kasper Vilmann Wellejus s220218
     */
    public void cancelTimer() {
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }
}
