package server;

import spark.Request;
import spark.Response;

public class CreateGameHandler extends Handler{
    @Override
    public Object handleReq(Request req, Response res) {
        try{
            CreateGameRequest jRequest = (CreateGameRequest)gson.fromJson(req.body(), CreateGameRequest.class);

            String auth = req.headers("authorization");

            JResponse jResponse = (new CreateGameService()).createGame(auth, jRequest);

            res.status(jResponse.getCode());
            res.type("application/json");
            res.body(jResponse.toString());
            return res.body();

        } catch(Exception e){ //
            res.status(400);
            res.type("application/json");
            res.body("{\"message\":\"Error: bad request\"}");
            return res.body();
        }
    }
}
