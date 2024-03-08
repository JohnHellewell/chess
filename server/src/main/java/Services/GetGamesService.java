package Services;

import dataAccessTests.DataAccess;
import dataAccessTests.DataAccessException;
import model.GameData;
import Responses.GamesResponse;

import java.util.ArrayList;

public class GetGamesService {



    public GamesResponse getGames(String auth) throws DataAccessException {
        if(DataAccess.isAuthValid(auth)){
            ArrayList<GameData> games = DataAccess.getGames();
            return new GamesResponse(games);
        }
        else
            throw new DataAccessException("");

    }
}
