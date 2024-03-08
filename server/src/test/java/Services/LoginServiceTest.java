package Services;

import Requests.LoginRequest;
import dataAccessTests.DataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoginServiceTest {
    @Test
    public void LoginTest(){
        DataAccess.clearAll();
        LoginRequest temp = new LoginRequest();
        temp.password = "b";
        temp.username = "a";
        try {
            Assertions.assertEquals(401, (new LoginService()).login(temp).getCode());

            DataAccess.addUser("a", "b", "c");
            Assertions.assertEquals(200, (new LoginService()).login(temp).getCode());
        } catch(Exception e){
            Assertions.fail();
        }
    }

}