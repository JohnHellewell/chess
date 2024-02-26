package server;

import spark.*;

public class Server {



    public int run(int desiredPort) {
        Spark.port(desiredPort);

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
        Spark.put("/game", (request, response) -> { return getDefaultRes(response); });

        Spark.awaitInitialization();
        return Spark.port();
    }

    private String getDefaultRes(Response response){ //for testng only; placeholder
        response.status(200);
        response.type("application/json");
        response.body("{\"message\":\"success\"}");
        return response.body();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
