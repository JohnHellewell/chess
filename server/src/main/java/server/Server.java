package server;

import Handlers.*;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import spark.Spark;
@WebSocket
public class Server {

    public int run(){ //default, run at port 8080
        return run(8080);
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        //define web socket endpoint
        Spark.webSocket("/connect", Server.class);

        Spark.staticFiles.location("web");

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
        System.out.printf("Received: %s", message);
        //session.getRemote().sendString("WebSocket response: " + message);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
