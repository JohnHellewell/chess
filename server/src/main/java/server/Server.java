package server;

import Handlers.*;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import spark.Spark;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.util.ArrayList;

@WebSocket
public class Server {

    static ArrayList<Session> sessions = new ArrayList<Session>();

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
        Gson gson = new Gson();
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
                    //load game
                    ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                    response.setGame(DataAccess.getGame(ugc.getGameID()));
                    session.getRemote().sendString(gson.toJson(response));



                    break;
                }
                case JOIN_OBSERVER:{
                    ServerMessage response = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                    response.setNotification("you joined the game");
                    session.getRemote().sendString(gson.toJson(response));
                    break;
                }
                case LEAVE:{
                    break;
                }
                case MAKE_MOVE:{
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
            String errorMsg = gson.toJson(new ServerMessage(ServerMessage.ServerMessageType.ERROR));
            session.getRemote().sendString(errorMsg);
        }
    }



    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
