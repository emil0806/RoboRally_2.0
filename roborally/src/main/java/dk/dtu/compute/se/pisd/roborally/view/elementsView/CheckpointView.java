package dk.dtu.compute.se.pisd.roborally.view.elementsView;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.elements.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


import java.awt.*;

public class CheckpointView {
    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param spaceView, the spaceview where image should be drawed
     * @param fieldAction, the action of that space
     */
    public static void draw(SpaceView spaceView, FieldAction fieldAction) {
        Checkpoint checkpoint = (Checkpoint) fieldAction;
        int checkpointNum = checkpoint.getCheckPointNum();
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();
        try {
            Image checkpointImage = new Image("elements/" + checkpointNum + ".png", 60, 60, true, true);
            context.drawImage(checkpointImage,0,0);
        } catch (Exception e) {
            System.out.println("Image for checkpoint not found");
        }
        spaceView.getChildren().add(canvas);
    }
}
