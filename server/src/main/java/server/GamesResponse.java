package server;

import model.GameData;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GamesResponse {

    public ArrayList<GameData> games = new ArrayList<GameData>();

    public GamesResponse(ArrayList<GameData> games){
        this.games = games;
    }

    @Override
    public String toString(){
        String str = "{\"games\":[";

        for(GameData game : games){
            String temp = "{";
            temp += "\"gameID\":" + game.getGameID() + ", ";
            if(game.getWhiteUsername()==null){
                temp += "\"whiteUsername\": \"\", ";
            } else {
                temp += "\"whiteUsername\": " + game.getWhiteUsername() +", ";
            }

            if(game.getBlackUsername()==null){
                temp += "\"blackUsername\": \"\", ";
            } else {
                temp += "\"blackUsername\": " + game.getBlackUsername() +", ";
            }

            temp += "\"gameName\": \"" + game.getGameName() + "\"";

            temp += "}";
            str += temp;
        }

        str += "] }";
        return str;
    }


}
