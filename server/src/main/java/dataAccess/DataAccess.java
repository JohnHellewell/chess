package dataAccess;

import model.*;

import java.util.ArrayList;
import java.util.UUID;

public class DataAccess {
    private static ArrayList<UserData> userData = new ArrayList<UserData>();
    private static ArrayList<GameData> gameData = new ArrayList<GameData>();
    private static ArrayList<AuthData> authData = new ArrayList<AuthData>();
    public DataAccess(){
        userData = new ArrayList<UserData>();
        gameData = new ArrayList<GameData>();
        authData = new ArrayList<AuthData>();
    };



    public static void ClearAll(){
        userData = new ArrayList<UserData>();
        gameData = new ArrayList<GameData>();
        authData = new ArrayList<AuthData>();
    }

    public static UserData getUser(String username){
        for(UserData u : userData){
            if(u.getUsername().equals(username))
                return u;
        }
        return null;
    }

    public static String addUser(String username, String password, String email){ //returns auth data
        userData.add(new UserData(username, password, email));
        String token = UUID.randomUUID().toString();
        authData.add(new AuthData(token, username));
        return token;
    }

}
