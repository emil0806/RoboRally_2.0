package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.client.Client;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class LobbyView extends VBox implements ViewObserver {

    private GridPane mainLobbyPane;
    private VBox lobbyButtonPanel;
    private FlowPane lobbyGamePanel;
    private AppController appController;

    /**
     * Constructs a LobbyView for the given AppController.
     * Sets up the main lobby pane, button panel, and game panel.
     * Initializes the buttons for creating a new game and refreshing the lobby.
     * @author David Kasper Vilmann Wellejus s220218
     * @param appController the app controller managing the application
     */
    public LobbyView(AppController appController){
        this.appController = appController;
        mainLobbyPane = new GridPane();
        lobbyButtonPanel = new VBox();
        lobbyButtonPanel.setMinWidth(600);
        lobbyButtonPanel.setAlignment(Pos.CENTER);

        lobbyGamePanel = new FlowPane();
        lobbyGamePanel.setMinHeight(300);

        this.getChildren().add(mainLobbyPane);

        Button createGameButton = new Button("New Game");
        createGameButton.setOnAction(e -> this.appController.newGame());

        Button refreshLobbyButton = new Button("Refresh");
        refreshLobbyButton.setOnAction(e -> this.appController.showAvailableGames());
        lobbyButtonPanel.getChildren().addAll(createGameButton, refreshLobbyButton);

        mainLobbyPane.add(lobbyGamePanel,0,0);
        mainLobbyPane.add(lobbyButtonPanel, 0, 1);
    }

    /**
     * Updates the list of games displayed in the lobby.
     * Clears the current game list and populates it with the new list of games.
     * Each game is displayed with its ID, board name, number of players, and a join button.
     * @author Emil Leonhard Lauritzen s231331
     * @param gamesList the list of games to display, where each game is represented by a list of strings
     */

    public void updateGamesList(ArrayList<ArrayList<String>> gamesList) {
        lobbyGamePanel.getChildren().clear();
        for(ArrayList<String> game : gamesList) {
            HBox gameBox = new HBox();
            gameBox.setSpacing(5);
            gameBox.setStyle("-fx-padding: 10; -fx-border-style: solid inside; -fx-border-width: 1; -fx-border-insets: 5; -fx-border-radius: 5; -fx-border-color: gray;");
            gameBox.setMinWidth(300);
            gameBox.setMaxWidth(300);

            VBox gameInfoBox = new VBox();
            gameInfoBox.setSpacing(5);
            gameInfoBox.setAlignment(Pos.CENTER_LEFT);
            Label gameIdText = new Label("Game ID: " + game.get(0));
            Label boardNameText = new Label("Board: " + game.get(1));
            Label playersText = new Label("Players: " + game.get(2) + " / " + game.get(3));

            VBox gameButtonBox = new VBox();
            gameButtonBox.setAlignment(Pos.CENTER);
            Button joinGameButton = new Button("Join Game");
            joinGameButton.setOnAction(e -> this.appController.joinGame(Integer.parseInt(game.get(0))));
            if(Client.getNumOfPlayers(Integer.parseInt(game.get(0))) == Client.getMaxNumOfPlayers(Integer.parseInt(game.get(0)))) {
                joinGameButton.setDisable(true);
            }
            gameButtonBox.getChildren().add(joinGameButton);

            gameInfoBox.getChildren().addAll(gameIdText, boardNameText, playersText);
            gameBox.getChildren().addAll(gameInfoBox, gameButtonBox);
            lobbyGamePanel.getChildren().add(gameBox);
        }
    }

    /**
     * Updates the view based on changes in the observed subject.
     * This method is called when the subject notifies its observers of a change.
     * @author David Kasper Vilmann Wellejus s220218
     * @param subject the subject being observed for changes
     */
    @Override
    public void updateView(Subject subject) {
        // Implementation
    }

}
