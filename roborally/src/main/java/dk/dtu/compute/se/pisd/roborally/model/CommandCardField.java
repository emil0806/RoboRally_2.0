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

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class CommandCardField extends Subject {

    final public Player player;

    private CommandCard card;

    private boolean visible;

    /**
     * Constructs a CommandCardField for the given player.
     * Initializes the field with the player, sets the card to null, and makes the field visible.
     * @author Emil Leonhard Lauritzen s231331
     * @param player the player to whom this command card field belongs
     */

    public CommandCardField(Player player) {
        this.player = player;
        this. card = null;
        this.visible = true;
    }

    public CommandCard getCard() {
        return card;
    }

    /**
     * Sets the command card for this field.
     * If the new card is different from the current card, it updates the card and notifies of the change.
     * @author Emil Leonhard Lauritzen s231331
     * @param card the new command card to set
     */

    public void setCard(CommandCard card) {
        if (card != this.card) {
            this.card = card;
            notifyChange();
        }
    }

    /**
     * Checks if the command card field is visible.
     * @author Emil Leonhard Lauritzen s231331
     * @return boolean true if the field is visible; false otherwise
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visibility of the command card field.
     * If the new visibility state is different from the current state, it updates the visibility and notifies of the change.
     * @author Emil Leonhard Lauritzen s231331
     * @param visible the new visibility state to set
     */
    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            notifyChange();
        }
    }
}
