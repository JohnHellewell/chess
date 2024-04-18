package Handlers;

import Responses.GamesResponse;
import Services.GetGamesService;
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

            GamesResponse gamesResponse = (new GetGamesService()).getGames(auth);
            ArrayList<GameData> gameDataList = gamesResponse.games;
            //GameData[] temp = new GameData[gameDataList.size()];
            //gameDataList.toArray(temp);
            //String games = gson.toJson(temp, GameData[].class);
            GamesResponse response = new GamesResponse(gameDataList);
            //String games = gson.toJson(response, GamesResponse.class);
            String games = response.toString();



            res.status(200);
            res.type("application/json");
            res.body(games);
            //res.body("{\"games\": []}");//TEST
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
