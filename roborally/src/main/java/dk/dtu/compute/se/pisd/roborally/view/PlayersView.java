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
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.scene.control.TabPane;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PlayersView extends TabPane implements ViewObserver {

    private Board board;

    private PlayerView[] playerViews;

    /**
     * Constructs a PlayersView for the given GameController.
     * Initializes the view for the current player and sets the tab closing policy.
     * Attaches this view as an observer to the board and updates the view.
     * @author David Kasper Vilmann Wellejus s220218
     * @param gameController the game controller managing the game
     */
    public PlayersView(GameController gameController) {
        board = gameController.board;
        int myPlayerID = board.getMyPlayerID();
        Player currentPlayer = null;

        for(Player player : board.getPlayers()){
            if(player.getPlayerID() == myPlayerID){
                currentPlayer = player;
                break;
            }
        }
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        if(currentPlayer != null){
            PlayerView playerView = new PlayerView(gameController, currentPlayer);
            this.getTabs().add(playerView);
        }

        board.attach(this);
        update(board);
    }

    /**
     * Updates the view based on changes in the observed subject.
     * If the subject is the board, selects the tab corresponding to the current player.
     * @author Emil Leonhard Lauritzen s231331
     * @param subject the subject being observed for changes
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            Player current = board.getCurrentPlayer();
            this.getSelectionModel().select(board.getPlayerNumber(current));
        }
    }

}
