package Services;

import Requests.RegistrationRequest;
import dataAccessTests.DataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RegistrationServiceTest {
    @Test
    public void RegisterTest(){
        DataAccess.clearAll();
        RegistrationRequest req = new RegistrationRequest();
        req.username = "a";
        req.password = "b";
        req.email = "c.gmail.com";
        try{
            Assertions.assertEquals(200, (new RegistrationService()).registerUser(req).getCode());
            Assertions.assertEquals(403, (new RegistrationService()).registerUser(req).getCode());

        } catch(Exception e){
            Assertions.fail();
        }
    }
}