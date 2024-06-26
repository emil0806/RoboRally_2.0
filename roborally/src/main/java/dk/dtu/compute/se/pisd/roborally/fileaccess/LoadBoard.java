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
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.CommandCardFieldTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.elements.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Emil Lauritzen, s231331@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "Rotating Maze";
    private static final String HIGHOCTANE = "High Octane";
    private static final String FRACTIONATION = "Fractionation";
    private static final String DEATHTRAP = "Death Trap";
    private static final String JSON_EXT = "json";

    private static final int DEFAULT_WIDTH = 8;

    private static final int DEFAULT_HEIGHT = 8;

    /**
     * Loads a game board from a JSON resource file based on the given board name.
     * This method retrieves the board configuration from a JSON file and constructs a Board object,
     * setting up spaces, actions, walls, and player positions.
     * @author Emil Leonhard Lauritzen s231331
     * @param boardname the name of the board to load
     * @return Board the constructed Board object; returns a default Board if the resource file is not found or an error occurs
     */

    public static Board loadBoard(String boardname) {
        switch (boardname) {
            case DEFAULTBOARD -> boardname = DEFAULTBOARD;
            case HIGHOCTANE -> boardname = HIGHOCTANE;
            case DEATHTRAP -> boardname = DEATHTRAP;
            case FRACTIONATION -> boardname = FRACTIONATION;
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardname + "." + JSON_EXT);
        if (inputStream == null) {
            return new Board(DEFAULT_WIDTH, DEFAULT_HEIGHT, 0);
        }

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

		Board result;
		// FileReader fileReader = null;
        JsonReader reader = null;
		try {
			// fileReader = new FileReader(filename);
			reader = gson.newJsonReader(new InputStreamReader(inputStream));
			BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);
			result = new Board(template.width, template.height, template.numOfCheckpoints);
			for (SpaceTemplate spaceTemplate: template.spaces) {
			    Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
			    if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }
            if(boardname.equals("Slot1") || boardname.equals("Slot2") || boardname.equals("Slot3")) {
                result.setPriorityAntenna(result.getSpace(template.priorityAntenna.x, template.priorityAntenna.y));
                for (int i = 0; i < template.players.size(); i++) {
                    PlayerTemplate playerTemplate = template.players.get(i);
                    Player player = new Player(result, playerTemplate.color, playerTemplate.name, i);
                    if(i == template.current) {
                        result.setCurrentPlayer(player);
                    }
                    player.setSpace(result.getSpace(playerTemplate.space.x, playerTemplate.space.y));
                    player.setHeading(playerTemplate.heading);
                    for(int j = 0; j < Player.NO_REGISTERS; j++) {
                        player.getProgramField(j).setCard(playerTemplate.program[j].card);
                        player.getProgramField(j).setVisible(playerTemplate.program[j].visible);
                    }
                    for(int l = 0; l < Player.NO_CARDS; l++) {
                        player.getCardField(l).setCard(playerTemplate.cards[l].card);
                        player.getCardField(l).setVisible(playerTemplate.cards[l].visible);
                    }
                    player.setCheckpoints(playerTemplate.checkpoints);
                    result.addPlayer(player);
                }
            }
			reader.close();
			return result;
		} catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {}
            }
            if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e2) {}
			}
		}
		return null;
    }
    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param board the playing board
     * @param name, name of board
     */
    public static void saveBoard(Board board, String name) {

        String gameJson = SerializeGame.serializeGame(board);

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename = classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;
        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):

        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();
        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            writer.jsonValue(gameJson);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }
}
