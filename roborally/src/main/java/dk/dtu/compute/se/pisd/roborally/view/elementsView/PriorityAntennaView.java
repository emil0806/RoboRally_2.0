package dk.dtu.compute.se.pisd.roborally.view.elementsView;

import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PriorityAntennaView {
    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param spaceView, the spaceview where image should be drawed
     */
    public static void draw(SpaceView spaceView) {
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();
        try {
            Image priorityAntennaImage = new Image("elements/antenna.png", 60, 60, true, true);
            context.drawImage(priorityAntennaImage,0,0);
        } catch (Exception e) {
            System.out.println("Image for antenna not found");
        }
        spaceView.getChildren().add(canvas);
    }
}
