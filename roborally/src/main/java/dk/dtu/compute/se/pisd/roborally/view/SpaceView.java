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
import dk.dtu.compute.se.pisd.roborally.StartRoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.elements.*;
import dk.dtu.compute.se.pisd.roborally.model.elements.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.elements.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.elements.Pits;
import dk.dtu.compute.se.pisd.roborally.model.elements.PushPanel;
import dk.dtu.compute.se.pisd.roborally.view.elementsView.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 75;
    final public static int SPACE_WIDTH = 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        Player player = space.getPlayer();
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();
        SnapshotParameters parameters = new SnapshotParameters();
        String robot;
        if (player != null) {
            if(player.getColor().equals("red")) {
                robot = "r1";
            } else if (player.getColor().equals("green")) {
                robot = "r2";
            } else if (player.getColor().equals("blue")) {
                robot = "r3";
            } else if (player.getColor().equals("orange")) {
                robot = "r4";
            } else if (player.getColor().equals("grey")) {
                robot = "r5";
            } else {
                robot = "r6";
            }
            try {
                Image robotImage = new Image("elements/" + robot + ".png", 50, 50, true, true);
                ImageView robotImageView = new ImageView(robotImage);
                if(player.getHeading() == Heading.EAST) {
                    robotImageView.setRotate(270);
                    parameters.setFill(Color.TRANSPARENT);
                    robotImage = robotImageView.snapshot(parameters, null);
                } else if(player.getHeading() ==  Heading.WEST) {
                    robotImageView.setRotate(90);
                    parameters.setFill(Color.TRANSPARENT);
                    robotImage = robotImageView.snapshot(parameters, null);
                } else if (player.getHeading() == Heading.NORTH) {
                    robotImageView.setRotate(180);
                    parameters.setFill(Color.TRANSPARENT);
                    robotImage = robotImageView.snapshot(parameters, null);
                }
                context.drawImage(robotImage,6,6);
            } catch (Exception e) {
                System.out.println("Image for checkpoint not found");
            }
            this.getChildren().add(canvas);
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            EmptySpaceView.draw(this);
            for(FieldAction fieldAction : space.getActions()) {
                if(fieldAction instanceof Checkpoint) {
                    CheckpointView.draw(this, fieldAction);
                } else if (fieldAction instanceof ConveyorBelt) {
                    ConveyorBeltView.draw(this, fieldAction);
                } else if (fieldAction instanceof PushPanel) {
                    PushPanelView.draw(this, fieldAction);
                } else if (fieldAction instanceof Pits) {
                    PitView.draw(this, fieldAction);
                } else if (fieldAction instanceof StartSpace) {
                    StartSpaceView.draw(this);
                } else if (fieldAction instanceof PriorityAntenna) {
                    PriorityAntennaView.draw(this);
                } else if (fieldAction instanceof Gears) {
                    GearsView.draw(this, fieldAction);
                }
            }
            WallView.draw(this);
            updatePlayer();
        }
    }

}
