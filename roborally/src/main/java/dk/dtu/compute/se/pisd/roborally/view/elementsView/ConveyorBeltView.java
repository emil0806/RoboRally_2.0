package dk.dtu.compute.se.pisd.roborally.view.elementsView;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.elements.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class ConveyorBeltView {

    public static void draw(SpaceView spaceView, FieldAction fieldAction) {
        ConveyorBelt conveyorBelt = (ConveyorBelt) fieldAction;
        Heading heading = conveyorBelt.getHeading();
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();

        try {
            Image conveyorBeltImage = new Image("elements/green.png", 60,60,true, true);
            ImageView conveyorBeltImageView = new ImageView(conveyorBeltImage);
            SnapshotParameters parameters = new SnapshotParameters();

            switch (heading) {
                case EAST:
                    conveyorBeltImageView.setRotate(90);
                    parameters.setFill(Color.TRANSPARENT);
                    conveyorBeltImage = conveyorBeltImageView.snapshot(parameters, null);
                    context.drawImage(conveyorBeltImage, 0,0);
                    break;
                case WEST:
                    conveyorBeltImageView.setRotate(270);
                    parameters.setFill(Color.TRANSPARENT);
                    conveyorBeltImage = conveyorBeltImageView.snapshot(parameters, null);
                    context.drawImage(conveyorBeltImage, 0,0);
                    break;
                case SOUTH:
                    conveyorBeltImageView.setRotate(180);
                    parameters.setFill(Color.TRANSPARENT);
                    conveyorBeltImage = conveyorBeltImageView.snapshot(parameters, null);
                    context.drawImage(conveyorBeltImage, 0,0);
                    break;
                case NORTH:
                    context.drawImage(conveyorBeltImage, 0,0);
            }
        } catch (Exception e) {
            System.out.println("Image for conveyorBelt not found");
        }
        spaceView.getChildren().add(canvas);
    }
}
