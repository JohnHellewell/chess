package server;

import Handlers.*;
import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.*;
import spark.Spark;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.util.ArrayList;
import java.util.Map;

@WebSocket
public class Server {

    static ArrayList<Session> sessions = new ArrayList<Session>();
    Gson gson = new Gson();
    //Map<Integer, GamePlayers> games;

    public int run(){ //default, run at port 8080
        return run(8080);
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        //define web socket endpoint
        Spark.webSocket("/connect", Server.class);


        Spark.staticFiles.location("web");

        //Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));


        // Register your endpoints and handle exceptions here.

        //Clear
        Spark.delete("/db", (request, response) -> (new ClearHandler()).handleReq(request, response));

        //Register user
        Spark.post("/user", (request, response) -> (new RegistrationHandler()).handleReq(request, response));

        //Login
        Spark.post("/session", (request, response) -> (new LoginHandler()).handleReq(request, response));

        //Logout
        Spark.delete("/session", (request, response) -> (new LogoutHandler()).handleReq(request, response));

        //List Games
        Spark.get("/game", (request, response) -> (new GetGamesHandler()).handleReq(request, response));

        //Create Game
        Spark.post("/game", (request, response) -> (new CreateGameHandler().handleReq(request, response)));

        //Join Game
        Spark.put("/game", (request, response) -> (new JoinHandler().handleReq(request, response)));

        Spark.awaitInitialization();
        return Spark.port();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            UserGameCommand ugc = gson.fromJson(message, UserGameCommand.class);
            //authenticate the auth first
            if(!DataAccess.isAuthValid(ugc.getAuthString())){
                ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                response.setErrorMessage("ERROR: Invalid auth token");
                session.getRemote().sendString(gson.toJson(response));
                return;
            }
            if(DataAccess.getGame(ugc.getGameID())==null){
                ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                response.setErrorMessage("ERROR: Invalid gameID");
                session.getRemote().sendString(gson.toJson(response));
                return;
            }

            //add this as a valid session if not added
            if(!sessions.contains(session))
                sessions.add(session);


            switch(ugc.getCommandType()){
                case JOIN_PLAYER:{
                    joinPlayer(session, ugc);
                    break;
                }
                case JOIN_OBSERVER:{
                    ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                    response.setGame(DataAccess.getGame(ugc.getGameID()));
                    session.getRemote().sendString(gson.toJson(response));

                    sendNotificationAllExcept("player joined the game", session);
                    break;
                }
                case LEAVE:{
                    break;
                }
                case MAKE_MOVE:{
                    makeMove(session, ugc);
                    break;
                }
                case RESIGN:{
                    break;
                }
                default:{
                    ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                    response.setErrorMessage("ERROR");
                    session.getRemote().sendString(gson.toJson(response));
                }
            }




        }catch(Exception e){ //failed to interpret message
            sendError(session, "Error: "+e.getMessage());
        }
    }

    private void sendError(Session session, String str)throws Exception{
        ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        msg.setErrorMessage(str);
        String errorMsg = gson.toJson(msg);

        session.getRemote().sendString(errorMsg);
    }

    private void makeMove(Session session, UserGameCommand ugc)throws Exception{
        //check that the player is making a valid move



        GameData gameData = DataAccess.getGame(ugc.getGameID());
        ChessGame game = gameData.getGame();
        try {
            game.makeMove(ugc.getMove());
        }catch(Exception e){
            //invalid move
            sendError(session, "Error: invalid move");
            return;
        }
        gameData.setGame(game);
        DataAccess.updateGame(ugc.getGameID(), gameData);

        loadGameAll(gameData);
        sendNotificationAllExcept("player made a move", session);
    }

    private void joinPlayer(Session session, UserGameCommand ugc) throws Exception{ //handles any attempts to join a game
        //check that the spot is not taken by another player
        GameData gameData = DataAccess.getGame(ugc.getGameID());
        if(gameData==null){
            sendError(session, "Error: invalid game");
            return;
        }
        if((gameData.getBlackUsername()!=null && !gameData.getBlackUsername().equals(authToUsername(ugc.getAuthString()))) &&
                (gameData.getWhiteUsername()!=null && !gameData.getWhiteUsername().equals(authToUsername(ugc.getAuthString())))){
            sendError(session, "Error: already taken");
            return;
        }
        if(gameData.getWhiteUsername()!=null && gameData.getBlackUsername()!=null &&
                gameData.getBlackUsername().equals(gameData.getWhiteUsername())){
            sendError(session, "Error: wrong team");
            return;
        }


        //load game
        ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        response.setGame(DataAccess.getGame(ugc.getGameID()));
        session.getRemote().sendString(gson.toJson(response));

        sendNotificationAllExcept("player joined the game", session);
    }


    private void sendNotificationAllExcept(String str, Session playerException){
        Gson gson = new Gson();
        ServerMessage mes = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        mes.setMessage(str);
        for(Session s : sessions){
            try {
                if(!s.equals(playerException))
                    s.getRemote().sendString(gson.toJson(mes));
            } catch(Exception e){}
        }
    }

    private void loadGameAll(GameData gd){
        Gson gson = new Gson();
        ServerMessage mes = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        mes.setGame(gd);
        for(Session s : sessions){
            try{
                s.getRemote().sendString(gson.toJson(mes));
            }catch(Exception e){}
        }
    }

    private String authToUsername(String auth){
        return DataAccess.findUser(auth);
    }



    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
