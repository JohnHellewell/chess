package dataAccessTests;

import dataAccess.DataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataAccessTest {
    @Test
    public void registerTestA(){ //check that it can register a new user
        DataAccess.clearAll();
        String token = DataAccess.addUser("nathan","123", "john@gmail.com");
        Assertions.assertNotNull(token);;
    }

    @Test
    public void registerTestB(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("nathan","123", "john@gmail.com");

        //register duplicate user
        token = DataAccess.addUser("nathan","123", "john@gmail.com");

        Assertions.assertNull(token);
    }

    @Test
    public void isAuthValidTestA(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");

        Assertions.assertTrue(DataAccess.isAuthValid(token));
    }

    @Test
    public void isAuthValidTestB(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");

        Assertions.assertFalse(DataAccess.isAuthValid("fake token"));
    }

    @Test
    public void createGameTestA(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        Assertions.assertEquals(4, ("" + (DataAccess.createGame("duck"))).length());
    }

    @Test
    public void createGameTestB(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        DataAccess.createGame("duck");
        Assertions.assertEquals(-1, DataAccess.createGame("duck"));
    }

    @Test
    public void getGamesTestA(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        int id = DataAccess.createGame("duck");
        Assertions.assertEquals(id, DataAccess.getGames().get(0).getGameID());
    }

    @Test
    public void getGamesTestB(){ //check that it returns nothing
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        Assertions.assertEquals(0, DataAccess.getGames().size());
    }

    @Test
    public void getGamesTestC(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        int id = DataAccess.createGame("duck");
        DataAccess.createGame("goose");
        Assertions.assertEquals(2, DataAccess.getGames().size());
    }

    @Test
    public void getGamesTestD(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        int id = DataAccess.createGame("duck");
        int id2 = DataAccess.createGame("goose");
        Assertions.assertEquals(id2, DataAccess.getGames().get(1).getGameID());
    }

}