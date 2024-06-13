package dk.dtu.compute.se.pisd.roborally.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

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
            String moves = playerID + ":" + gameID + ":" + chosenMoves;

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(moves))
                    .uri(URI.create(server + "/lobby/" + gameID + "/moves"))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Response Code: " + response.statusCode());
            System.out.println("HTTP Response Body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAllGameMoves(int gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(server + "/lobby/" + gameID + "/moves"))
                    .setHeader("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Response Code1: " + response.statusCode());
            System.out.println("HTTP Response Body1: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
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
