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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static java.lang.Double.parseDouble;

public class Client {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static String server = "http://localhost:8080";

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    public static void joinGame(int gameID, int myPlayerID, String name, int age) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerID", myPlayerID);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("age", age);
        jsonObject.addProperty("startSpace", "null");
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
}
