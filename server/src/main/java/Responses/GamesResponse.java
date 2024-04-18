package Responses;

import model.GameData;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GamesResponse {

    public ArrayList<GameData> games;

    public GamesResponse(ArrayList<GameData> games){
        this.games = games;
    }

    @Override
    public String toString(){
        String str = "{\"games\":[";

        for(GameData game : games){
            String temp = "{";
            temp += "\"gameID\":" + game.getGameID() + ", ";
            if(game.getWhiteUsername()==null|| game.getWhiteUsername().isEmpty()){
                temp += "\"whiteUsername\": null, ";
            } else {
                temp += "\"whiteUsername\": " + game.getWhiteUsername() +", ";
            }

            if(game.getBlackUsername()==null|| game.getBlackUsername().isEmpty()){
                temp += "\"blackUsername\": null, ";
            } else {
                temp += "\"blackUsername\": " + game.getBlackUsername() +", ";
            }

            temp += "\"gameName\": \"" + game.getGameName() + "\"";

            temp += "},";
            str += temp;
        }
        if(!games.isEmpty())
            str = str.substring(0,str.length()-1);//remove last comma

        str += "] }";
        return str;
    }


}
