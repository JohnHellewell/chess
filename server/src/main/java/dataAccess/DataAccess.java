package dataAccess;

import model.*;

import java.util.ArrayList;

public class DataAccess {
    private static ArrayList<UserData> userData;
    private static ArrayList<GameData> gameData;
    private static ArrayList<AuthData> authData;
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

    public static void addUser(String username, String password, String email){
        userData.add(new UserData(username, password, email));
    }

}
