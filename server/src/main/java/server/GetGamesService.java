package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

import java.util.ArrayList;

public class GetGamesService {



    public GamesResponse getGames(String auth) throws DataAccessException {
        if(DataAccess.isAuthValid(auth)){
            //return "{\"games\": []}"; //FIXME this is wrong
            ArrayList<GameData> games = DataAccess.getGames();
            return new GamesResponse(games);
        }
        else
            throw new DataAccessException("");
    }
}
