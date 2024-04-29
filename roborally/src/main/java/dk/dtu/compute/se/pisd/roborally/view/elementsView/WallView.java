package dk.dtu.compute.se.pisd.roborally.view.elementsView;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class WallView {
    public static void draw(SpaceView spaceView) {
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();
        try {
            Image wallImage = new Image("elements/wall.png", 60, 60, true, true);
            ImageView wallImageView = new ImageView(wallImage);
            SnapshotParameters parameters = new SnapshotParameters();
            for(Heading heading : spaceView.space.getWalls()) {
                switch (heading) {
                    case EAST:
                        context.drawImage(wallImage, 0,0);
                        break;
                    case WEST:
                        context.drawImage(wallImage, 50,0);
                        break;
                    case SOUTH:
                        wallImageView.setRotate(90);
                        parameters.setFill(Color.TRANSPARENT);
                        wallImage = wallImageView.snapshot(parameters, null);
                        context.drawImage(wallImage, 0,50);
                        break;
                    case NORTH:
                        wallImageView.setRotate(270);
                        parameters.setFill(Color.TRANSPARENT);
                        wallImage = wallImageView.snapshot(parameters, null);
                        context.drawImage(wallImage, 0,0);
                }
            }
        } catch (Exception e) {
            System.out.println("Image for wall not found");
        }
        spaceView.getChildren().add(canvas);
    }
}
