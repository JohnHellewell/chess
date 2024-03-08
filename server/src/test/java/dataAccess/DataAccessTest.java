package dataAccess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {
    @Test
    public void addTest(){ //check that it can register a new user
        //String token = DataAccess.addUser("John","duck123", "john@gmail.com");
        //System.out.print("Token: ");
        //System.out.println(token);

        DataAccess.clearAll();
    }

}