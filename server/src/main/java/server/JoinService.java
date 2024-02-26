package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

public class JoinService extends Service{

    public JResponse join(String authToken, JoinRequest req) throws DataAccessException {
        if(!validate(authToken)){
            return unathorized();
        }
        //valid auth

        //check if game exists
        GameData game = DataAccess.getGame(req.getGameID());

        if(game == null){
            throw new DataAccessException("Game doesn't exist!");
        }

        //game exists
        if(req.getPlayerColor().equals("BLACK")||req.getPlayerColor().equals("WHITE")){
            if(req.getPlayerColor().equals("BLACK")){ //black
                if(game.getBlackUsername().equals("")){ //spot open
                    game.setBlackUsername(DataAccess.findUser(authToken));
                    return success();
                } else {
                    return playerTaken();
                }
            } else { //white
                if(game.getWhiteUsername().equals("")){ //spot open
                    game.setWhiteUsername(DataAccess.findUser(authToken));
                    return success();
                } else {
                    return playerTaken();
                }
            }
        } else { //spectator
            return success();
        }
    }

    private JResponse playerTaken(){
        JResponse res = new JResponse(403);
        res.setMessage("Error: already taken");
        return res;
    }

    private JResponse success(){
        JResponse res = new JResponse(200);
        return res;
    }


}
