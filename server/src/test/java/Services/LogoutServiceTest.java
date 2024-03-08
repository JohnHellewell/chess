package Services;

import Requests.LogoutRequest;
import dataAccessTests.DataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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