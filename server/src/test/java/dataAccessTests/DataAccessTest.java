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
    public void isAuthValidTest(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        Assertions.assertFalse(DataAccess.isAuthValid("duck"));
        Assertions.assertTrue(DataAccess.isAuthValid(token));
    }


}