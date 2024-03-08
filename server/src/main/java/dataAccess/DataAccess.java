package dataAccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.UUID;

public class DataAccess {
    private static ArrayList<UserData> userData = new ArrayList<UserData>();
    private static ArrayList<GameData> gameData = new ArrayList<GameData>();
    private static ArrayList<AuthData> authData = new ArrayList<AuthData>();
    public DataAccess(){
        try {
            DatabaseManager.createDatabase();
        } catch(Exception e){
            //nothing..?
            System.out.println(e.getMessage());
        }
    };

    private static void runSQL(String code){
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(code)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e){
            System.out.println("Error running code: " + e.getMessage());
        }
    }

    private static String querySQL(String code){
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(code)) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                return rs.getString(0);
            }
        } catch (Exception e){
                return "";
        }
    }

    /*
    public void example() throws Exception {
       try (var conn = DatabaseManager.getConnection()) {
          try (var preparedStatement = conn.prepareStatement("SELECT 1+1")) {
             var rs = preparedStatement.executeQuery();
             rs.next();
             System.out.println(rs.getInt(1));
          }
       }
    }
     */

    public static void clearAll(){
        runSQL("DELETE FROM userdata");
        runSQL("DELETE FROM authdata");
        runSQL("DELETE FROM gamedata");
    }



    public static UserData getUser(String username){
        for(UserData u : userData){
            if(u.getUsername().equals(username))
                return u;
        }
        return null;
    }

    public static String login(String username){
        String token = generateAuthToken();
        authData.add(new AuthData(token, username));
        return token;
    }

    public static String addUser(String username, String password, String email){ //returns auth data
        runSQL("INSERT into userdata(\"" + username + "\", \"" + password + "\", \"" + email + "\")");


        String authToken = generateAuthToken();
        runSQL("INSERT into authdata(\"" + authToken + "\", \"" + username +")");
        return authToken;
    }

    public static boolean isAuthValid(String authToken){
        for(AuthData a : authData){
            if(a.getAuthToken().equals(authToken))
                return true;
        }
        return false;
    }

    private static String generateAuthToken(){
        return UUID.randomUUID().toString();
    }

    public static void logout(String authToken){
        for(AuthData a : authData){
            if(a.getAuthToken().equals(authToken)){
                authData.remove(a);
                break;
            }
        }
    }

    public static ArrayList<GameData> getGames(){
        return gameData;
    }

    public static int createGame(String gameName){ //don't worry about checking for duplicates just yet
        int gameID = generateGameID(gameName);
        gameData.addLast(new GameData(gameID, "", "", gameName, new ChessGame()));
        return gameID;
    }

    private static int generateGameID(String name){
        return Math.abs(name.hashCode()%10000); //generates a 4-digit gameID based on the name. I figured this was better than random()
    }

    public static GameData getGame(int gameID){
        for(GameData game : gameData){
            if(game.getGameID()==gameID){
                return game;
            }
        }
        return null;
    }

    public static String findUser(String authToken){
        for(AuthData a : authData){
            if(a.getAuthToken().equals(authToken)){
                return a.getUsername();
            }
        }
        return null;
    }

}
