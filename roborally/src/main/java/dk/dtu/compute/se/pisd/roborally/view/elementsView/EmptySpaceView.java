package dk.dtu.compute.se.pisd.roborally.view.elementsView;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.elements.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class EmptySpaceView {
    public static void draw(SpaceView spaceView) {
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();
        try {
            Image emptySpaceImage = new Image("elements/empty.png");
            context.drawImage(emptySpaceImage,0,0);
        } catch (Exception e) {
            System.out.println("Image for checkpoint not found");
        }
        spaceView.getChildren().add(canvas);
    }
}
