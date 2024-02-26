package server;

import model.GameData;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class GetGamesHandler extends Handler{


    @Override
    public Object handleReq(Request req, Response res) {
        try {
            String auth = req.headers("authorization");
            if (auth == null) {
                throw new RuntimeException();
            }
            //has token
            String games = (new GetGamesService()).getGames(auth).toString();

            res.status(200);
            res.type("application/json");
            res.body(games);
            return res.body();

        } catch(Exception e){
            //cant parse
            res.status(401);
            res.type("application/json");
            res.body("{\"message\":\"Error: unauthorized\"}");
            return res.body();
        }
    }
}
