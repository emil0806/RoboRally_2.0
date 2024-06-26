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
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;
    final public Board board;
    private String name;
    private String color;
    private int playerID;
    private Space space;
    private Heading heading = SOUTH;
    private CommandCardField[] program;
    private CommandCardField[] cards;
    private int checkpoints;
    private int distanceToPriorityAntenna;
    private Space startSpace;

    private boolean sentToStartSpace = false;

    /**
     * Constructs a Player with the specified board, color, name, and player ID.
     * Initializes the program and card fields for the player.
     * @author Emil Leonhard Lauritzen s231331
     * @param board the game board the player is on
     * @param color the color of the player
     * @param name the name of the player
     * @param playerID the unique ID of the player
     */
    public Player(@NotNull Board board, String color, @NotNull String name, int playerID) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.playerID = playerID;
        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    /**
     * Gets the name of the player.
     * @author Emil Leonhard Lauritzen s231331
     * @return String the name of the player
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name of the player and notifies of the change.
     * @author Emil Leonhard Lauritzen s231331
     * @param name the new name of the player
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Gets the color of the player.
     * @author Emil Leonhard Lauritzen s231331
     * @return String the color of the player
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of the player and notifies of the change.
     * @author Emil Leonhard Lauritzen s231331
     * @param color the new color of the player
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * Gets the current space of the player.
     * @author Emil Leonhard Lauritzen s231331
     * @return Space the current space of the player
     */
    public Space getSpace() {
        return space;
    }

    /**
     * Sets the current space of the player, updating the old and new spaces accordingly.
     * @author Emil Leonhard Lauritzen s231331
     * @param space the new space of the player
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    /**
     * Gets the heading direction of the player.
     * @author Emil Leonhard Lauritzen s231331
     * @return Heading the heading direction of the player
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * Sets the heading direction of the player and notifies of the change.
     * @author Emil Leonhard Lauritzen s231331
     * @param heading the new heading direction of the player
     */
    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Gets the command card field at the specified index in the program array.
     * @author Emil Leonhard Lauritzen s231331
     * @param i the index of the command card field
     * @return CommandCardField the command card field at the specified index
     */
    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    public int getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(int checkpoints) {
        this.checkpoints = checkpoints;
    }

    public int getDistanceToPriorityAntenna() {
        return this.distanceToPriorityAntenna;
    }

    public void setDistanceToPriorityAntenna(int distanceToPriorityAntenna) {
        this.distanceToPriorityAntenna = distanceToPriorityAntenna;
    }

    public Space getStartSpace() {
        return this.startSpace;
    }

    public void setStartSpace(Space startSpace) {
        this.startSpace = startSpace;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * Gets the chosen moves of the player from the program fields.
     * @author Emil Leonhard Lauritzen s231331
     * @return ArrayList<String> a list of the names of the chosen command cards
     */
    public ArrayList<String> getChosenMoves() {
        ArrayList<String> chosenMoves = new ArrayList<>();
        for (CommandCardField field : program) {
            if (field != null && field.getCard() != null) {
                chosenMoves.add(field.getCard().getName());
            }
        }
        return chosenMoves;
    }

    public boolean isSentToStartSpace() {
        return sentToStartSpace;
    }

    public void setSentToStartSpace(boolean sentBack) {
        this.sentToStartSpace = sentBack;
    }
}
