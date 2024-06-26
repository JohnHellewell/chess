package ui;

import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.*;
import java.net.URI;


public class ServerFacade extends Endpoint{

    public static int port = 8080;
    private Session session;
    boolean checkmate = false;

    public void openWebSocket() throws Exception{
        URI uri = new URI("ws://localhost:" + port + "/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                Gson gson = new Gson();
                try{
                    ServerMessage mes = gson.fromJson(message, ServerMessage.class);
                    executeServerCommand(mes);

                }catch(Exception e){
                    System.out.println("Failed to interpret Server message: " + message);
                }
            }
        });
    }

    private void executeServerCommand(ServerMessage mes){
        switch(mes.getServerMessageType()){
            case ERROR -> { System.out.println("Server Error"); }
            case NOTIFICATION -> { printNot(mes.getMessage());}
            case LOAD_GAME -> {
                ClientMain.reloadBoard(mes.getGame().getGame());}
            default -> {}
        }
    }

    private void printNot(String str){
        if(str.toLowerCase().contains("checkmate")){
            if(!checkmate){
                System.out.println(str);
                checkmate = true;
            }
        } else
            System.out.println(str);
    }




    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        //System.out.println("Web Socket Opened");
    }

    private void send(String msg) throws Exception {
        session.getBasicRemote().sendText(msg);
    }

    public void makeMove(String auth, int gameID, ChessMove move)throws Exception{
        Gson gson = new Gson();
        UserGameCommand command = new UserGameCommand(auth, gameID, UserGameCommand.CommandType.MAKE_MOVE, getPlayerString());
        command.setMove(move);
        command.player = getPlayerString();
        send(gson.toJson(command));
    }

    private String getPlayerString(){
        if(ClientMain.player== ClientMain.PlayerType.WHITE)
            return "WHITE";
        else if (ClientMain.player== ClientMain.PlayerType.BLACK)
            return "BLACK";
        return "";


    }

    public void resign(String auth, int gameID)throws Exception{
        Gson gson = new Gson();
        UserGameCommand command = new UserGameCommand(auth, gameID, UserGameCommand.CommandType.RESIGN, null);
        send(gson.toJson(command));
    }


    public void joinGame(String auth, int gameID, boolean isPlayer)throws Exception{
        Gson gson = new Gson();
        UserGameCommand command;
        if(isPlayer){
            String player = "";
            if(ClientMain.player== ClientMain.PlayerType.WHITE)
                player = "WHITE";
            else if (ClientMain.player== ClientMain.PlayerType.BLACK)
                player = "BLACK";
            command = new UserGameCommand(auth, gameID, UserGameCommand.CommandType.JOIN_PLAYER, player);

        } else {
            command = new UserGameCommand(auth, gameID, UserGameCommand.CommandType.JOIN_OBSERVER, null);
        }

        send(gson.toJson(command));
    }


    //***** end of websocket code

    public static boolean clear(){
        int code = deletePutCommand("/db", "DELETE", null);
        if(code==200){
            //System.out.println("db cleared");
            return true;
        } else {
            System.out.println("Error code: " + code);
            return false;
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

    private static int deletePutCommand(String endpoint, String httpMethod, String authToken){ //temporary
        try {
            URI uri = new URI("http://localhost:" + port + endpoint);
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
            URI uri = new URI("http://localhost:" + port + endpoint);
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
            URI uri = new URI("http://localhost:" + port + "/game");
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
                //ListGamesResponse games = gson.fromJson(jsonResponse, ListGamesResponse.class); //this line is failing
                //return games.getGames();
                return gson.fromJson(jsonResponse, GameData[].class);
            } else {
                return null;
            }


        } catch(Exception e){
            System.out.println("Client Error: " + e.getMessage());
            return null;
        }
    }

    public static String joinGame(String gameID, String player, String authToken){
        Map<String, String> args = new HashMap<>();
        args.put("gameID", gameID);
        args.put("playerColor", player);
        Map<String, String> response = postCommand("/game", "PUT", args, authToken);
        return response.get("message");
    }

    public static String spectateGame(String gameID, String authToken){
        return joinGame(gameID, "", authToken);
    }


}
