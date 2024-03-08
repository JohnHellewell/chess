package Services;

import Requests.CreateGameRequest;
import dataAccessTests.DataAccess;
import dataAccessTests.DataAccessException;
import Responses.JResponse;

public class CreateGameService extends Service{

    public JResponse createGame(String auth, CreateGameRequest req) throws DataAccessException {
        if(!validate(auth)) {
            JResponse res = new JResponse(401);
            res.setMessage("Error: unauthorized");
            return res;
        }
        else{
            int gameID = DataAccess.createGame(req.getGameName());
            JResponse res = new JResponse(200);
            res.setGameID(gameID);
            return res;
        }
    }

}
