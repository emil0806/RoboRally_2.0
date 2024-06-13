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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Client {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private String server = "http://localhost:8080";


    public void uploadGame(String gameString){
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gameString))
                    .uri(URI.create(server + "/lobby"))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getGame(String gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Response Code: " + response.statusCode());
            System.out.println("HTTP Response Body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getGames() {
        String listOfGames = "";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby"))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Response Code: " + response.statusCode());
            System.out.println("HTTP Response Body: " + response.body());
            try {
                listOfGames = response.headers().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listOfGames;
    }

    public void uploadMoves(String chosenMoves, int playerID, int gameID) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("playerID", playerID);
            jsonObject.addProperty("chosenMoves", chosenMoves);
            String json = gson.toJson(jsonObject);

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(URI.create(server + "/lobby/" + gameID + "/moves"))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Response Body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getAllGameMoves(int gameID) {
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
            ArrayList<String> result = new ArrayList<>();

            for (JsonNode node : rootNode) {
                String chosenMoves = node.get("chosenMoves").asText();
                result.add(chosenMoves);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public void getMovesByPlayerID(int gameID, int playerID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/moves/" + playerID))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Response Code: " + response.statusCode());
            System.out.println("HTTP Response Body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
