package Services;

import Requests.LogoutRequest;
import dataAccess.DataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest {
    @Test
    public void LogoutTest(){
        DataAccess.clearAll();
        String authToken = DataAccess.addUser("a", "b", "c");
        LogoutRequest req = new LogoutRequest();
        req.authToken = authToken;
        try{
            Assertions.assertEquals(200, (new LogoutService()).logoutUser(req).getCode());

            Assertions.assertEquals(401, (new LogoutService()).logoutUser(req).getCode());
        } catch(Exception e){
            Assertions.fail();
        }
    }

}