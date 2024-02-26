package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;

public class GetGamesService {



    public String getGames(String auth) throws DataAccessException {
        if(DataAccess.isAuthValid(auth))
            return "{\"games\": []}"; //FIXME this is wrong
        else
            throw new DataAccessException("");
    }
}
