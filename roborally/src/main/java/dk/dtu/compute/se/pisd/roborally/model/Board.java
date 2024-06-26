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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.elements.Pits;
import dk.dtu.compute.se.pisd.roborally.model.elements.PriorityAntenna;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    private Integer gameId;

    private final Space[][] spaces;

    private List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private Space priorityAntenna;

    private int numOfCheckpoints;

    private Player winner;
    private int myPlayerID;

    public Board(int width, int height, int numOfCheckpoints) {
        this.width = width;
        this.height = height;
        this.numOfCheckpoints = numOfCheckpoints;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     * @author David Wellejus s220218
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        if (space.getWalls().contains(heading)) {
            return null;
        }
        // TODO needs to be implemented based on the actual spaces
        //      and obstacles and walls placed there. For now it,
        //      just calculates the next space in the respective
        //      direction in a cyclic way.

        // XXX an other option (not for now) would be that null represents a hole
        //     or the edge of the board in which the players can fall

        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                    y += 1;
                    break;
            case WEST:
                    x -= 1;
                    break;
            case NORTH:
                    y -= 1;
                    break;
            case EAST:
                    x += 1;
                    break;
        }
        Space result;
        if((x < 0) || (x > (width - 1)) || (y < 0) || (y > (height - 1))) {
            space.getPlayer().setSentToStartSpace(true);
            result = space.getPlayer().getStartSpace();
        } else {
            result = getSpace(x, y);
            for (FieldAction fieldAction : result.getActions()) {
                if (fieldAction instanceof Pits) {
                    space.getPlayer().setSentToStartSpace(true);
                    result = space.getPlayer().getStartSpace();
                }
            }
            if (result != null && result.getWalls().contains(heading.opposite())) {
                return null;
            }
        }
        return result;
    }

    /**
     * Retrieves the current status message of the game.
     * This method returns a string containing the current phase, the name and heading of the current player,
     * the number of checkpoints the player has, and the current step.
     * @author Emil Leonhard Lauritzen s231331
     * @return String the status message of the game
     */
    public String getStatusMessage() {

        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() + ", Heading: " + getCurrentPlayer().getHeading() +
                ", Checkpoints: " + getCurrentPlayer().getCheckpoints() +
                ", Step: " + getStep();
    }

    public Space getPriorityAntenna() {
        return this.priorityAntenna;
    }

    public void setPriorityAntenna(Space priorityAntenna) {
        this.priorityAntenna = priorityAntenna;
    }

    public int getNumOfCheckpoints() {
        return this.numOfCheckpoints;
    }

    public void setNumOfCheckpoints(int numOfCheckpoints) {
        this.numOfCheckpoints = numOfCheckpoints;
    }

    public Player getWinner() {
        return this.winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public int getMyPlayerID() {
        return myPlayerID;
    }

    public void setMyPlayerID(int playerID) {
        this.myPlayerID = playerID;
    }

}
