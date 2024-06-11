package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

public class LobbyView extends VBox implements ViewObserver {

    private GridPane mainLobbyPane;
    private VBox lobbyButtonPanel;
    private VBox lobbyGamePanel;
    private AppController appController;

    public LobbyView(AppController appController){
        this.appController = appController;
        mainLobbyPane = new GridPane();

        lobbyButtonPanel = new VBox();
        lobbyButtonPanel.setMinWidth(600);
        lobbyButtonPanel.setAlignment(Pos.CENTER);

        lobbyGamePanel = new VBox();
        lobbyGamePanel.setMinHeight(300);

        this.getChildren().add(mainLobbyPane);

        Button createGameButton = new Button("New Game");
        createGameButton.setOnAction(e -> this.appController.newGame());

        Button refreshLobbyButton = new Button("Refresh");
        refreshLobbyButton.setOnAction(e -> this.appController.printGames());
        lobbyButtonPanel.getChildren().addAll(createGameButton, refreshLobbyButton);

        mainLobbyPane.add(lobbyGamePanel,0,0);
        mainLobbyPane.add(lobbyButtonPanel, 0, 1);
    }

    @Override
    public void updateView(Subject subject) {

    }
}
