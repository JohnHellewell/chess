import com.google.gson.Gson;
import model.GameData;
import ui.ListGamesResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {

    public ServerFacade(){

    }

    public static void clear(){
        int code = deletePutCommand("/db", "DELETE", null);
        if(code==200){
            System.out.println("db cleared");

        } else {
            System.out.println("Error code: " + code);
        }
    }

    public static boolean logout(String authToken){
        int code = deletePutCommand("/session", "DELETE", authToken);
        if(code==200){
            System.out.println("logged out.");
            return true;
        } else {
            System.out.println("Error code: " + code);
            return false;
        }
    }

    public static int deletePutCommand(String endpoint, String httpMethod, String authToken){ //temporary
        try {
            URI uri = new URI("http://localhost:8080" + endpoint);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod(httpMethod);//clear db

            if(authToken!=null){
                connection.setRequestProperty("authorization", authToken);
            }

            connection.connect();

            return connection.getResponseCode();
        } catch(Exception e){
            System.out.println("Client Error: " + e.getMessage());
            return -1;
        }
    }

    private static Map<String, String> postCommand(String endpoint, String httpMethod, Map<String, String> reqData, String authToken){ //creates http connection, sends Map of variables as json, returns response as Map of variables
        try {
            URI uri = new URI("http://localhost:8080" + endpoint);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setReadTimeout(5000);
            connection.setRequestMethod(httpMethod);//register user

            if(authToken!=null){
                connection.setRequestProperty("authorization", authToken);
            }

            Gson gson = new Gson();
            String json = gson.toJson(reqData); //turn variables into json
            byte[] jsonDataBytes = json.getBytes(StandardCharsets.UTF_8); //translate json into bytes

            connection.setDoOutput(true);

            OutputStream outStream = connection.getOutputStream();
            outStream.write(jsonDataBytes);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) { //success
                InputStream responseBody = connection.getInputStream();
                // Read and process response body from InputStream ...
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody));
                String jsonResponse = reader.readLine();//store the json response

                Map<String, String> responseVars = gson.fromJson(jsonResponse, Map.class);

                if(jsonResponse.contains("gameID") && endpoint.equals("/game") && httpMethod.equals("POST")){ //patched problem with converting int to string in json
                    String id = jsonResponse.substring(jsonResponse.indexOf("gameID")+8, jsonResponse.indexOf("gameID")+12);
                    responseVars.replace("gameID", id);
                }

                // Close resources
                reader.close();
                responseBody.close();
                connection.disconnect();

                return responseVars;
            } else {
                // SERVER RETURNED AN HTTP ERROR
                InputStream responseBody = connection.getErrorStream();
                // Read and process error response body from InputStream ...
                //FIXME add a way to display the error

                BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody));
                String jsonResponse = reader.readLine();//store the json response

                Map<String, String> responseVars = gson.fromJson(jsonResponse, Map.class);



                return responseVars;
            }
        } catch(Exception e){
            System.out.println("Client Error: " + e.getMessage());
        }
        return null;
    }

    public static Map<String, String> registerUser(String username, String password, String email){ //returns authToken
        Map<String, String> args = new HashMap<>();
        args.put("username", username);
        args.put("password", password);
        args.put("email", email);

        Map<String, String> resVars = postCommand("/user", "POST", args, null);

        return  resVars;
    }

    public static Map<String, String> loginUser(String username, String password){
        Map<String, String> args = new HashMap<>();
        args.put("username", username);
        args.put("password", password);

        Map<String, String> resVars = postCommand("/session", "POST", args, null);

        return  resVars;
    }

    public static Map<String, String> createGame(String name, String authToken){
        Map<String, String> args = new HashMap<>();
        args.put("gameName", name);

        return postCommand("/game", "POST", args, authToken);
    }

    public static GameData[] listGames(String authToken){
        try {
            URI uri = new URI("http://localhost:8080" + "/game");
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");//list games

            connection.setRequestProperty("authorization", authToken);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = connection.getInputStream();
                // Read and process response body from InputStream ...
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody));
                String jsonResponse = reader.readLine();//store the json response
                Gson gson = new Gson();
                ListGamesResponse games = gson.fromJson(jsonResponse, ListGamesResponse.class);
                return games.getGames();
            } else {
                return null;//FIXME
            }


        } catch(Exception e){
            System.out.println("Client Error: " + e.getMessage());
            return null;
        }
    }

    public static void joinGame(String gameID, String player, String authToken){
        Map<String, String> args = new HashMap<>();
        args.put("gameID", gameID);
        args.put("playerColor", player);
        Map<String, String> response = postCommand("/game", "PUT", args, authToken);

    }
}
