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
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

    private static final int DEFAULT_WIDTH = 8;

    private static final int DEFAULT_HEIGHT = 8;


    public static Board loadBoard(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
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
                    Player player = new Player(result, playerTemplate.color, playerTemplate.name);
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

    public static void saveBoard(Board board, String name) {
        List<PlayerTemplate> playerTemplates = createPlayerTemplates(board);

        BoardTemplate template = createBoardTemplate(board, playerTemplates);
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
            gson.toJson(template, template.getClass(), writer);
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

    public static BoardTemplate createBoardTemplate(Board board, List<PlayerTemplate> playerTemplates) {
        BoardTemplate boardTemplate = new BoardTemplate();
        boardTemplate.width = board.width;
        boardTemplate.height = board.height;
        boardTemplate.phase = board.getPhase();
        boardTemplate.players = playerTemplates;
        boardTemplate.current = board.getPlayerNumber(board.getCurrentPlayer());
        boardTemplate.priorityAntenna = new SpaceTemplate();
        boardTemplate.priorityAntenna.x = board.getPriorityAntenna().x;
        boardTemplate.priorityAntenna.y = board.getPriorityAntenna().y;
        boardTemplate.priorityAntenna.actions = board.getPriorityAntenna().getActions();
        boardTemplate.priorityAntenna.walls = board.getPriorityAntenna().getWalls();
        boardTemplate.numOfCheckpoints = board.getNumOfCheckpoints();

        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space space = board.getSpace(i, j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty()) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    boardTemplate.spaces.add(spaceTemplate);
                }
            }
        }
        return boardTemplate;
    }

    public static List<PlayerTemplate> createPlayerTemplates(Board board) {
        List<PlayerTemplate> playerTemplates = new ArrayList<>();
        for(int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            PlayerTemplate playerTemplate = new PlayerTemplate();
            playerTemplate.name = player.getName();
            playerTemplate.color = player.getColor();
            playerTemplate.space = new SpaceTemplate();
            playerTemplate.space.walls = player.getSpace().getWalls();
            playerTemplate.space.actions = player.getSpace().getActions();
            playerTemplate.space.x = player.getSpace().x;
            playerTemplate.space.y = player.getSpace().y;
            playerTemplate.heading = player.getHeading();
            playerTemplate.program = new CommandCardFieldTemplate[5];
            for(int k = 0; k < playerTemplate.program.length; k++) {
                playerTemplate.program[k] = new CommandCardFieldTemplate();
                playerTemplate.program[k].card = player.getProgramField(k).getCard();
                playerTemplate.program[k].visible = player.getProgramField(k).isVisible();
            }
            playerTemplate.cards = new CommandCardFieldTemplate[8];
            for(int j = 0; j < playerTemplate.cards.length; j++) {
                playerTemplate.cards[j] = new CommandCardFieldTemplate();
                playerTemplate.cards[j].card = player.getCardField(j).getCard();
                playerTemplate.cards[j].visible = player.getCardField(j).isVisible();
            }
            playerTemplates.add(playerTemplate);
        }
    return playerTemplates;
    }
}
