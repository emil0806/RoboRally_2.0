package dk.dtu.compute.se.pisd.roborally.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;

public class Client {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private String server = "http://localhost:8080";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void uploadGame(String boardName, int numOfPlayers, int maxPlayers, int turnID){
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

    public ArrayList<String> getGame(int gameID) {
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

    public ArrayList<ArrayList<String>> getGames() {
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

    public void joinGame(int gameID, int myPlayerID, String name, int age) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerID", myPlayerID);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("age", age);
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

    public void leaveGame(int gameID, int playerID) {
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

    public ArrayList<ArrayList<String>> getPlayers(int gameID) {
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
                result.add(playerInfo);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getNumOfPlayers(int gameID) {
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

    public int getMaxNumOfPlayers(int gameID) {
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

    public int getTurnID(int gameID) {
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

    public void setTurnID(int gameID, int turnID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(turnID)))
                    .uri(URI.create(server + "/lobby/" + gameID + "/setTurnID"))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Boolean> waitForAllUsersToBeReady(int gameID) {
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

}
