package dataAccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {
    @Test
    public void registerTest(){ //check that it can register a new user
        DataAccess.clearAll();
        String token = DataAccess.addUser("nathan","123", "john@gmail.com");
        System.out.print("Token: ");
        System.out.println(token);

        token = DataAccess.addUser("nathan","123", "john@gmail.com");
    }

    @Test
    public void isAuthValidTest(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        Assertions.assertFalse(DataAccess.isAuthValid("duck"));
        Assertions.assertTrue(DataAccess.isAuthValid(token));
    }


}