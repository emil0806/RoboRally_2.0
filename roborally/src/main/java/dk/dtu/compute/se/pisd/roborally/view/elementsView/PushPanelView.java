package dk.dtu.compute.se.pisd.roborally.view.elementsView;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.elements.PushPanel;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class PushPanelView {
    /**
     * ...
     * @author Emil Lauritzen, s231331@dtu.dk
     * @param spaceView, the spaceview where image should be drawed
     * @param fieldAction, the action of that space
     */
    public static void draw(SpaceView spaceView, FieldAction fieldAction) {
        PushPanel pushPanel = (PushPanel) fieldAction;
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();
        try {
            Image pushPanelImage = new Image("elements/pushOne.png", 60, 60, true, true);
            ImageView pushPanelImageView;
            SnapshotParameters parameters = new SnapshotParameters();

            if(pushPanel.getActivationRegisters()[0] == 2) {
                pushPanelImage = new Image("elements/pushTwo.png", 60, 60, true, true);
            }
            pushPanelImageView = new ImageView(pushPanelImage);

            switch (pushPanel.getHeading()) {
                case EAST:
                    context.drawImage(pushPanelImage, 0, 0);
                    break;
                case WEST:
                    pushPanelImageView.setRotate(180);
                    parameters.setFill(Color.TRANSPARENT);
                    pushPanelImage = pushPanelImageView.snapshot(parameters, null);
                    context.drawImage(pushPanelImage, 45, 0);
                    break;
                case SOUTH:
                    pushPanelImageView.setRotate(90);
                    parameters.setFill(Color.TRANSPARENT);
                    pushPanelImage = pushPanelImageView.snapshot(parameters, null);
                    context.drawImage(pushPanelImage, 0, 0);
                    break;
                case NORTH:
                    pushPanelImageView.setRotate(270);
                    parameters.setFill(Color.TRANSPARENT);
                    pushPanelImage = pushPanelImageView.snapshot(parameters, null);
                    context.drawImage(pushPanelImage, 0, 45);
            }
        } catch (Exception e) {
            System.out.println("Image for pushPanel not found");
        }
        spaceView.getChildren().add(canvas);
    }
}
