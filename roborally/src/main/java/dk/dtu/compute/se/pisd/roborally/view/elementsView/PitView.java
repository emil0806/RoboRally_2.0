package dk.dtu.compute.se.pisd.roborally.view.elementsView;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.elements.Pits;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class PitView {
    /**
     * ...
     * @author Mikkel Lau, s235082@dtu.dk
     * @param spaceView, the spaceview where image should be drawed
     * @param fieldAction, the action of that space
     */
    public static void draw(SpaceView spaceView, FieldAction fieldAction) {
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();

        try {
            Image pitImage = new Image("elements/hole.png", 60,60,true, true);
            ImageView pitImageView = new ImageView(pitImage);
            SnapshotParameters parameters = new SnapshotParameters();

            parameters.setFill(Color.TRANSPARENT);
            pitImageView.snapshot(parameters, null);
                context.drawImage(pitImage,0,0);
        } catch (Exception e) {
            System.out.println("Image for pit not found");
        }
        spaceView.getChildren().add(canvas);
    }
}
