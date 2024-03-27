package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.util.ArrayList;
import java.util.UUID;

public class DataAccess {



    private static void createIfNeeded(){
        try {
            DatabaseManager.createDatabase();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

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
        createIfNeeded();
        runSQL("DELETE FROM userdata");
        runSQL("DELETE FROM authdata");
        runSQL("DELETE FROM gamedata");
    }

    public static UserData getUser(String username){
        createIfNeeded();
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
        createIfNeeded();
        String token = generateAuthToken();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authtoken FROM authdata WHERE username=?")) {
                preparedStatement.setString(1, username);
                try(var result = preparedStatement.executeQuery()){
                    if(result.next()){ //user already logged in; update their auth to a new one

                        //return result.getString("authtoken");
                        /*
                        try (var updateStatement = conn.prepareStatement("UPDATE authdata SET authtoken = ? WHERE username = ?")) {
                            updateStatement.setString(1, token);
                            updateStatement.setString(2, username);
                            updateStatement.executeUpdate();
                            return token;
                        }

                         */

                        try (var updateStatement = conn.prepareStatement("DELETE FROM authdata WHERE username = ?")) {
                            updateStatement.setString(1, username);
                            updateStatement.executeUpdate();
                        }
                        try (var updateStatement = conn.prepareStatement("INSERT INTO authdata (authtoken, username) VALUES(?, ?)")) {
                            updateStatement.setString(1, token);
                            updateStatement.setString(2, username);
                            updateStatement.executeUpdate();
                        }
                        return token;
                    } //else carry on
                }
            }
        }catch(Exception e){
            return null;
        }
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
        createIfNeeded();
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
        createIfNeeded();
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
        createIfNeeded();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM authdata WHERE authtoken=?")) {
                preparedStatement.setString(1, authToken);

                preparedStatement.executeUpdate();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<GameData> getGames(){
        createIfNeeded();
        ArrayList<GameData> temp = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM gamedata")) {
                try(var result = preparedStatement.executeQuery()){
                    while(result.next()){
                        //temp.add(getGame(result.getInt("gameid")));
                        int id = result.getInt("gameid");
                        String white, black;
                        try{
                            white = result.getString("whiteusername");
                        } catch(Exception e){
                            white = "";
                        }
                        try{
                            black = result.getString("blackusername");
                        } catch(Exception e){
                            black = "";
                        }
                        String name = result.getString("gamename");
                        ChessGame g = stringToGame(result.getString("game"));
                        GameData gd = new GameData(id, white, black, name, g);

                        temp.add(gd);
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
        createIfNeeded();
        int gameID = generateGameID(gameName);
        ChessGame game = new ChessGame();
        game.resetBoard();
        String newGame = gameToString(game);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO gamedata (gameid, whiteusername, blackusername, gamename, game) " +
                    "VALUES(?, ?, ?, ?, ?)")) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(2, "");
                preparedStatement.setString(3, "");
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

    public static void updateGame(int gameID, GameData game){
        createIfNeeded();
        String json = gameToString(game.getGame());
        //delete and replace game with matching ID
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM gamedata WHERE gameid=?")) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("INSERT INTO gamedata (gameid, whiteusername, blackusername, gamename, game) " +
                    "VALUES(?, ?, ?, ?, ?)")) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(2, game.getWhiteUsername());
                preparedStatement.setString(3, game.getBlackUsername());
                preparedStatement.setString(4, game.getGameName());
                preparedStatement.setString(5, json);

                preparedStatement.executeUpdate();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static int generateGameID(String name){
        return Math.abs(name.hashCode()%9000)+1000; //generates a 4-digit gameID based on the name. I figured this was better than random()
    }

    public static GameData getGame(int gameID){
        createIfNeeded();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM gamedata WHERE gameid=?")) {
                preparedStatement.setInt(1, gameID);

                try(var result = preparedStatement.executeQuery()){
                    if(result.next()) {
                        return new GameData(gameID,
                                result.getString("whiteusername"),
                                result.getString("blackusername"),
                                result.getString("gamename"),
                                stringToGame(result.getString("game")));
                    } else
                        return null;
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static ChessGame stringToGame(String str){
        Gson gson = new Gson();
        return gson.fromJson(str, ChessGame.class);
    }

    private static String gameToString(ChessGame game){
        var serializer = new Gson();
        return serializer.toJson(game);
    }


    public static String findUser(String authToken){
        createIfNeeded();
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
