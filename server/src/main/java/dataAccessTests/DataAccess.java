package dataAccessTests;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.util.ArrayList;
import java.util.UUID;

public class DataAccess {

    public DataAccess(){
        try {
            DatabaseManager.createDatabase();
        } catch(Exception e){
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

    public static void clearAll(){
        runSQL("DELETE FROM userdata");
        runSQL("DELETE FROM authdata");
        runSQL("DELETE FROM gamedata");
    }

    public static UserData getUser(String username){
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM userdata WHERE username=?")) {
                preparedStatement.setString(1, username);
                try(var result = preparedStatement.executeQuery()){
                    while(result.next()) {
                        String user = result.getString("username");
                        String pass = result.getString("password");
                        String email = result.getString("email");

                        if (user != null && pass != null && email != null) {
                            return new UserData(user, pass, email);
                        } else {
                            return null;
                        }
                    }
                    return null;
                }
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }


    }

    public static String login(String username){
        String token = generateAuthToken();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO authdata (authtoken, username) VALUES(?, ?)")) {
                preparedStatement.setString(1, token);
                preparedStatement.setString(2, username);

                preparedStatement.executeUpdate();
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
        return token;
    }

    public static String addUser(String username, String password, String email){ //returns auth data
        try (var conn = DatabaseManager.getConnection()) {
            //check that the username is not already taken
            try (var preparedStatement = conn.prepareStatement("SELECT username FROM userdata WHERE username=?")){
                preparedStatement.setString(1, username);
                try(var result = preparedStatement.executeQuery()){
                    while(result.next()){ //there should be no results
                        throw new DataAccessException("Username already taken!");
                    }
                }
            }

            //add user to userData
            try (var preparedStatement = conn.prepareStatement("INSERT INTO userdata (username, password, email) VALUES(?, ?, ?)")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);

                preparedStatement.executeUpdate();
            }

            String authToken = generateAuthToken();
            //add user & new authToken to authData
            try (var preparedStatement = conn.prepareStatement("INSERT INTO authdata (authtoken, username) VALUES(?, ?)")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);

                preparedStatement.executeUpdate();
            }

            return authToken;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static boolean isAuthValid(String authToken){ //done
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username FROM authdata WHERE authtoken=?")){
                preparedStatement.setString(1, authToken);
                try(var result = preparedStatement.executeQuery()){
                    while(result.next()){ //if something is returned, return true
                        return true;
                    }
                    return false;
                }
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static String generateAuthToken(){
        return UUID.randomUUID().toString();
    }

    public static void logout(String authToken){
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM authdata WHERE authtoken=?")) {
                preparedStatement.setString(1, authToken);

                preparedStatement.executeUpdate();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<GameData> getGames(){ //call getGame
        ArrayList<GameData> temp = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT gameid FROM gamedata")) {
                try(var result = preparedStatement.executeQuery()){
                    while(result.next()){
                        temp.add(getGame(result.getInt("gameid")));
                    }
                    return temp;
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static int createGame(String gameName){ //don't worry about checking for duplicates just yet
        int gameID = generateGameID(gameName);
        String newGame = gameToString(new ChessGame());
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO gamedata (gameid, whiteusername, blackusername, gamename, game) " +
                    "VALUES(?, ?, ?, ?, ?)")) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(4, gameName);
                preparedStatement.setString(5, newGame);

                preparedStatement.executeUpdate();

                return gameID;
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return -1;
        }
    }

    private static int generateGameID(String name){
        return Math.abs(name.hashCode()%10000); //generates a 4-digit gameID based on the name. I figured this was better than random()
    }

    public static GameData getGame(int gameID){
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM gamedata WHERE gameid=?")) {
                preparedStatement.setInt(1, gameID);

                try(var result = preparedStatement.executeQuery()){
                    result.next();
                    return new GameData(gameID,
                            result.getString("whiteusername"),
                            result.getString("blackusername"),
                            result.getString("gamename"),
                            stringToGame(result.getString("game")));
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static ChessGame stringToGame(String str){
        var serializer = new Gson();
        return serializer.fromJson(str, ChessGame.class);
    }

    private static String gameToString(ChessGame game){
        var serializer = new Gson();
        return serializer.toJson(game);
    }


    public static String findUser(String authToken){
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username FROM authdata WHERE authtoken=?")) {
                preparedStatement.setString(1, authToken);

                try(var result = preparedStatement.executeQuery()){
                    while(result.next()){ //if something is returned, return true
                        return result.getString("username");
                    }
                    return null;
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

}
