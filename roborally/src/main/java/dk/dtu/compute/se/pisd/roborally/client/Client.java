package dk.dtu.compute.se.pisd.roborally.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.lang.Double.parseDouble;

public class Client {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String server = "http://localhost:8080";

    private static final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Uploads game details to the server.
     * Sends a POST request with the game details as JSON to the specified server endpoint.
     * @author Emil Leonhard Lauritzen s231331
     * @param boardName the name of the board
     * @param numOfPlayers the current number of players
     * @param maxPlayers the maximum number of players allowed
     * @param turnID the ID of the current turn
     */
    public static void uploadGame(String boardName, int numOfPlayers, int maxPlayers, int turnID){
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("boardName", boardName);
            jsonObject.addProperty("numberOfPlayers", numOfPlayers);
            jsonObject.addProperty("maxNumberOfPlayers", maxPlayers);
            jsonObject.addProperty("turnID", turnID);
            String json = gson.toJson(jsonObject);
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(URI.create(server + "/lobby"))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Retrieves game details from the server based on a specific game ID. This method constructs an HTTP GET request
     * to query the "lobby" endpoint with the game ID, and parses the JSON response to extract game details.
     * @author Klavs Medvee Pommer Blankensteiner s213383
     * @param gameID the unique ID of the game to be retrieved
     * @return ArrayList<String> a list containing game data as text; returns null if an error occurs
     */


    public static ArrayList<String> getGame(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID))
                    .setHeader("Content-Type", "application/json")
                    .build();

            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);

            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            ArrayList<String> result = new ArrayList<>();
            for (JsonNode node : rootNode) {
                String game = node.asText();
                result.add(game);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a list of all games stored on the server from the "lobby" endpoint.
     * This method constructs an HTTP GET request, receives data as a JSON string,
     * and parses this string into a nested list, with each sublist containing specific details about a game.
     * @author Emil Leonhard Lauritzen s231331
     * @return ArrayList<ArrayList<String>> a nested list of game details in text format; returns null if an error occurs
     */


    public static ArrayList<ArrayList<String>> getGames() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            ArrayList<ArrayList<String>> result = new ArrayList<>();
            for (JsonNode node : rootNode) {
                ArrayList<String> gameInfo = new ArrayList<>();
                String gameID = node.get("gameID").asText();
                gameInfo.add(gameID);
                String boardName = node.get("boardName").asText();
                gameInfo.add(boardName);
                String numOfPlayers = node.get("numberOfPlayers").asText();
                gameInfo.add(numOfPlayers);
                String maxNumOfPlayers = node.get("maxNumberOfPlayers").asText();
                gameInfo.add(maxNumOfPlayers);
                String players = node.get("players").asText();
                gameInfo.add(players);
                String turnID = node.get("turnID").asText();
                gameInfo.add(turnID);
                result.add(gameInfo);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Joins a player to a specific game on the server using the player's details. This method constructs a JSON object
     * with the player's ID, name, and age, and sends it as a POST request to the "lobby" endpoint associated with the game ID.
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game to join
     * @param myPlayerID the player's unique ID
     * @param name the player's name
     * @param age the player's age
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void joinGame(int gameID, int myPlayerID, String name, int age) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerID", myPlayerID);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("age", age);
        jsonObject.addProperty("startSpace", "null");
        //jsonObject.addProperty("round", 0);
        String json = gson.toJson(jsonObject);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(URI.create(server + "/lobby/" + gameID))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Removes a player from a specific game on the server. This method constructs an HTTP DELETE request
     * and sends it to the "lobby" endpoint, specifying the game ID and player ID in the URL to target the correct player and game.
     * @author Klavs Medvee Pommer Blankensteiner s213383
     * @param gameID the unique ID of the game from which the player is leaving
     * @param playerID the ID of the player who is leaving the game
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void leaveGame(int gameID, int playerID) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(URI.create(server + "/lobby/" + gameID + "/" + playerID))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Retrieves a list of all players participating in a specific game from the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint, specifying the game ID and adding "/players" to fetch player details.
     * It then parses the JSON response into a nested list, where each sublist contains details about a player.
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game for which player details are being retrieved
     * @return ArrayList<ArrayList<String>> a nested list containing player details such as ID, name, age, and starting space; returns null if an error occurs
     */

    public static ArrayList<ArrayList<String>> getPlayers(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/players"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            ArrayList<ArrayList<String>> result = new ArrayList<>();
            for (JsonNode node : rootNode) {
                ArrayList<String> playerInfo = new ArrayList<>();
                String playerID = node.get("playerID").asText();
                playerInfo.add(playerID);
                String name = node.get("name").asText();
                playerInfo.add(name);
                String age = node.get("age").asText();
                playerInfo.add(age);
                String startSpace = node.get("startSpace").asText();
                playerInfo.add(startSpace);
                result.add(playerInfo);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Retrieves the number of players currently registered in a specific game from the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appends "/getNumberOfPlayers" to specifically request the count of players, and parses the response to return an integer.
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game for which the number of players is being retrieved
     * @return int the number of players in the game; returns 0 if an error occurs
     */

    public static int getNumOfPlayers(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/getNumberOfPlayers"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            return Integer.parseInt(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Retrieves the maximum number of players allowed in a specific game from the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appends "/getMaxNumberOfPlayers" to specifically request the maximum player capacity, and parses the response to return an integer.
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game for which the maximum number of players is being retrieved
     * @return int the maximum number of players allowed in the game; returns 0 if an error occurs
     */

    public static int getMaxNumOfPlayers(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/getMaxNumberOfPlayers"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            return Integer.parseInt(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * Retrieves the current turn ID for a specific game from the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appends "/getTurnID" to specifically request the turn ID, and parses the response to return an integer.
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game for which the turn ID is being retrieved
     * @return int the current turn ID of the game; returns 0 if an error occurs
     */

    public static int getTurnID(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/getTurnID"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            return Integer.parseInt(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * Sets or updates the turn ID for a specific game on the server.
     * This method constructs an HTTP POST request to the "lobby" endpoint for the specified game ID,
     * appends "/setTurnID" to specifically target the turn ID setting operation, and sends the request with no body.
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game for which the turn ID is being set or updated
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void setTurnID(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(server + "/lobby/" + gameID + "/setTurnID"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Checks periodically if all users in a specific game on the server are ready to proceed.
     * This method schedules a task that repeatedly sends an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appended with "/allUsersReady". It evaluates the response to determine if all users are ready.
     * If so, it completes the CompletableFuture with a value of true.
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game for which readiness of all users is being checked
     * @return CompletableFuture<Boolean> a future that will be completed with true if all users are ready, or exceptionally if an error occurs
     */

    public static CompletableFuture<Boolean> waitForAllUsersToBeReady(int gameID) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create(server + "/lobby/" + gameID + "/allUsersReady"))
                            .setHeader("Content-Type", "application/json")
                            .build();
                    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                    boolean allUsersReady = Boolean.parseBoolean(response.body());

                    if (allUsersReady) {
                        scheduler.shutdown();
                        future.complete(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
        return future;
    }


    /**
     * Uploads a list of chosen moves for a player in a specific game to the server.
     * This method constructs a JSON object with the player's ID and the list of chosen moves,
     * then sends it as a POST request to the "lobby" endpoint for the specified game ID, appended with "/moves".
     * The server processes these moves as part of the game's ongoing activities.
     * @author David Kasper Vilmann Wellejus s220218
     * @param chosenMoves an ArrayList of Strings representing the moves selected by the player
     * @param playerID the unique ID of the player submitting the moves
     * @param gameID the unique ID of the game where the moves need to be recorded
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void uploadMoves(ArrayList<String> chosenMoves, int playerID, int gameID) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("playerID", playerID);
            jsonObject.add("chosenMoves", gson.toJsonTree(chosenMoves));
            String json = gson.toJson(jsonObject);
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(URI.create(server + "/lobby/" + gameID + "/moves"))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Retrieves a list of all moves made in a specific game from the server. Each sublist in the returned list contains
     * a combined string of player ID and their chosen moves, separated by commas.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game ID, appended with "/moves".
     * It processes the JSON response to extract move details for each player.
     * @author David Kasper Vilmann Wellejus s220218.
     * @param gameID the unique ID of the game for which moves are being retrieved
     * @return ArrayList<ArrayList<String>> a list of lists, where each sublist contains a string of player ID and moves; returns an empty list if an error occurs
     */

    public static ArrayList<ArrayList<String>> getAllGameMoves(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/moves"))
                    .setHeader("Content-Type", "application/json")
                    .build();

            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);

            // Use ObjectMapper to parse the JSON string and extract chosenMoves
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            ArrayList<ArrayList<String>> result = new ArrayList<>();

            for (JsonNode node : rootNode) {
                ArrayList<String> gameMoves = new ArrayList<>();
                String playerID = node.get("playerID").asText();
                JsonNode chosenMovesNode = node.get("chosenMoves");
                ArrayList<String> chosenMovesList = new ArrayList<>();
                if (chosenMovesNode.isArray()) {
                    for (JsonNode moveNode : chosenMovesNode) {
                        chosenMovesList.add(moveNode.asText());
                    }
                }
                String chosenMoves = String.join(",", chosenMovesList);
                // Combine playerID and chosenMoves without spaces
                String combinedMoves = playerID + "," + chosenMoves;
                gameMoves.add(combinedMoves);
                result.add(gameMoves);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    /**
     * Retrieves a list of all moves made by a specific player in a specific game from the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appending the player ID to target the moves of a specific player.
     * It processes the JSON response to extract and return a list of moves.
     * @author Klavs Medvee Pommer Blankensteiner s213383
     * @param gameID the unique ID of the game for which moves are being retrieved
     * @param playerID the unique ID of the player whose moves are being retrieved
     * @return ArrayList<String> a list of moves made by the specified player; returns null if an error occurs
     */

    public static ArrayList<String> getMovesByPlayerID(int gameID, int playerID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/moves/" + playerID))
                    .setHeader("Content-Type", "application/json")
                    .build();

            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);

            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            ArrayList<String> result = new ArrayList<>();
            for(JsonNode node : rootNode) {
                String move = node.asText();
                result.add(move);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the starting space for a specific player in a specific game on the server.
     * This method constructs an HTTP POST request, sending the starting space value as the body of the request,
     * to the "lobby" endpoint for the specified game and player IDs, appended with "/setStartSpace".
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game
     * @param playerID the unique ID of the player
     * @param startSpace the starting space value to be set for the player
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void setStartSpace(int gameID, int playerID, double startSpace) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(startSpace)))
                    .uri(URI.create(server + "/lobby/" + gameID + "/" + playerID + "/setStartSpace"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the starting space for a specific player in a specific game from the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game and player IDs,
     * appending "/getStartSpace" to the URL. It processes the JSON response to return the starting space as a double.
     * @author Mikkel Lau Petersen s235082
     * @param gameID the unique ID of the game
     * @param playerID the unique ID of the player
     * @return double the starting space value for the player; returns 0 if an error occurs
     */

    public static double getStartSpace(int gameID, int playerID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/" + playerID + "/getStartSpace"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            return parseDouble(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Sets the available starting spaces for a specific game on the server.
     * This method constructs an HTTP POST request, sending the starting space value as the body of the request,
     * to the "lobby" endpoint for the specified game ID, appended with "/setAvailableStartSpaces".
     * @author Mikkel Lau Petersen s235082
     * @param gameID the unique ID of the game
     * @param startSpace the starting space value to be set as available
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void setAvailableStartSpaces(int gameID, double startSpace) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(startSpace)))
                    .uri(URI.create(server + "/lobby/" + gameID + "/setAvailableStartSpaces"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the available starting spaces for a specific game from the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appending "/getAvailableStartSpaces" to the URL. It processes the JSON response to return a list of available starting spaces.
     * @author Mikkel Lau Petersen s235082
     * @param gameID the unique ID of the game
     * @return ArrayList<Double> a list of available starting spaces for the game; returns an empty list if an error occurs
     */

    public static ArrayList<Double> getAvailableStartSpaces(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/getAvailableStartSpaces"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            ArrayList<Double> startSpaces = objectMapper.readValue(jsonResponse, new TypeReference<ArrayList<Double>>(){});
            return startSpaces;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves the list of removed starting places for a specific game from the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appending "/getRemovedStartingPlace" to the URL. It processes the JSON response to return a list of removed starting places.
     * @author Mikkel Lau Petersen s235082
     * @param gameID the unique ID of the game
     * @return List<Double> a list of removed starting places for the game; returns an empty list if an error occurs
     */

    public static List<Double> getRemovedStartingPlace(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/getRemovedStartingPlace"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            ArrayList<Double> startSpaces = new ArrayList<>();
            if (rootNode != null || rootNode.size() >= 1) {
                for(JsonNode node : rootNode) {
                    Double place = node.asDouble();
                    startSpaces.add(place);
                }
            }
            return startSpaces;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Waits for all players in a specific game to have chosen their actions.
     * This method periodically sends an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appending "/allPlayersChosen" to check if all players have made their choices.
     * It returns true once all players have chosen, otherwise it will keep checking at a fixed interval.
     * @author David Kasper Vilmann Wellejus s220218
     * @param gameID the unique ID of the game
     * @return boolean true if all players have chosen their actions; throws a RuntimeException if an error occurs
     */

    public static boolean waitForAllUsersChosen(int gameID) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create(server + "/lobby/" + gameID + "/allPlayersChosen"))
                            .setHeader("Content-Type", "application/json")
                            .build();
                    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                    boolean allUsersReady = Boolean.parseBoolean(response.body());

                    if (allUsersReady) {
                        scheduler.shutdown();
                        future.complete(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a player's interaction for a specific step in a specific game to the server.
     * This method constructs an HTTP POST request with the interaction string as the body,
     * and sends it to the "lobby" endpoint for the specified game ID, player ID, and step,
     * appending "/setInteraction" to the URL.
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game
     * @param playerID the unique ID of the player
     * @param step the step number for which the interaction is being sent
     * @param interaction the interaction data to be sent
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void sendInteraction(int gameID, int playerID, int step, String interaction) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(interaction))
                    .uri(URI.create(server + "/lobby/" + gameID + "/moves/" + playerID + "/" + step + "/setInteraction"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Waits for a specific interaction to be completed by a player at a specific step in a specific game.
     * This method periodically sends an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * player ID, and step, appending "/waitForInteraction" to check if the interaction is completed.
     * It returns a CompletableFuture that will be completed with true once the interaction is done.
     * @author Klavs Medvee Pommer Blankensteiner s213383
     * @param gameID the unique ID of the game
     * @param playerID the unique ID of the player
     * @param step the step number for which the interaction is being checked
     * @return CompletableFuture<Boolean> a future that will be completed with true if the interaction is done; or exceptionally if an error occurs
     */

    public static CompletableFuture<Boolean> waitForInteraction(int gameID, int playerID, int step) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create(server + "/lobby/" + gameID + "/moves/" + playerID + "/waitForInteraction/" + step))
                            .setHeader("Content-Type", "application/json")
                            .build();
                    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                    boolean interactionDone = Boolean.parseBoolean(response.body());

                    if (interactionDone) {
                        scheduler.shutdown();
                        future.complete(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
        return future;
    }

    /**
     * Clears all moves for a specific game on the server.
     * This method constructs an HTTP DELETE request and sends it to the "lobby" endpoint for the specified game ID,
     * appending "/clearAllMoves" to the URL.
     * @author David Kasper Vilmann Wellejus s220218
     * @param gameID the unique ID of the game
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void clearAllMoves(int gameID){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(URI.create(server + "/lobby/" + gameID + "/clearAllMoves"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the number of players that are ready in a specific game on the server.
     * This method constructs an HTTP POST request, sending the number of players ready as the body of the request,
     * to the "lobby" endpoint for the specified game ID, appended with "/playersReady".
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game
     * @param playersReady the number of players that are ready
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void setPlayersReady(int gameID, int playersReady) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(playersReady)))
                    .uri(URI.create(server + "/lobby/" + gameID + "/playersReady"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Increments the number of players that are ready in a specific game on the server.
     * This method constructs an HTTP PUT request with no body, and sends it to the "lobby" endpoint for the specified game ID,
     * appended with "/playersReady".
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void incrementPlayersReady(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(server + "/lobby/" + gameID + "/playersReady"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Periodically checks if all players are ready in a specific game on the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appended with "/playersReady". It repeatedly sends the request at fixed intervals and checks the response.
     * If all players are ready, it completes the CompletableFuture with true.
     * @author Emil Leonhard Lauritzen s231331
     * @param gameID the unique ID of the game
     * @return CompletableFuture<Boolean> a future that will be completed with true if all players are ready, or exceptionally if an error occurs
     */

    public static CompletableFuture<Boolean> getPlayersReady(int gameID) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create(server + "/lobby/" + gameID + "/playersReady"))
                            .setHeader("Content-Type", "application/json")
                            .build();
                    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                    boolean interactionDone = Boolean.parseBoolean(response.body());

                    if (interactionDone) {
                        scheduler.shutdown();
                        future.complete(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
        return future;
    }

    /**
     * Increments the round number for a specific game on the server.
     * This method constructs an HTTP PUT request with no body, and sends it to the "lobby" endpoint for the specified game ID,
     * appended with "/round".
     * @author David Kasper Vilmann Wellejus s220218
     * @param gameID the unique ID of the game
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void incrementRoundNumber(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(server + "/lobby/" + gameID + "/round"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the current round number for a specific game from the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appended with "/round". It processes the JSON response to return the round number as an integer.
     * @author David Kasper Vilmann Wellejus s220218
     * @param gameID the unique ID of the game
     * @return int the current round number of the game; returns 0 if an error occurs
     */

    public static int getRoundNumber(int gameID){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/round"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            return Integer.parseInt(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Increments the round number for a specific player in a specific game on the server.
     * This method constructs an HTTP PUT request with no body, and sends it to the "lobby" endpoint for the specified game and player IDs,
     * appended with "/round".
     * @author David Kasper Vilmann Wellejus s220218
     * @param gameID the unique ID of the game
     * @param playerID the unique ID of the player
     * @return void This method does not return a value but may print an error stack trace if an exception occurs.
     */

    public static void incrementPlayerRoundNumber(int gameID, int playerID){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(server + "/lobby/" + gameID + "/" + playerID + "/round"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Periodically checks if all players in a specific game are at the same round number on the server.
     * This method constructs an HTTP GET request to the "lobby" endpoint for the specified game ID,
     * appended with "/round/allReady". It repeatedly sends the request at fixed intervals and checks the response.
     * If all players are at the same round, it completes the CompletableFuture with true.
     * @author David Kasper Vilmann Wellejus s220218
     * @param gameID the unique ID of the game
     * @return CompletableFuture<Boolean> a future that will be completed with true if all players are at the same round, or exceptionally if an error occurs
     */

    public static CompletableFuture<Boolean> allPlayersAtSameRound(int gameID) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create(server + "/lobby/" + gameID + "/round/allReady"))
                            .setHeader("Content-Type", "application/json")
                            .build();
                    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                    boolean interactionDone = Boolean.parseBoolean(response.body());

                    if (interactionDone) {
                        scheduler.shutdown();
                        future.complete(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
        return future;
    }
}
