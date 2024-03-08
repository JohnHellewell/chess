package Services;

import Requests.JoinRequest;
import dataAccessTests.DataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoinServiceTest {
    @Test
    public void JoinTest(){
        DataAccess.clearAll();
        String authToken = DataAccess.addUser("a", "b", "c");
        int gameID = DataAccess.createGame("banana");
        JoinRequest req  = new JoinRequest();
        req.gameID = gameID;
        req.playerColor="WHITE";

        try {
            assertEquals(200, (new JoinService()).join(authToken, req).getCode());//should pass

            String authTok2 = DataAccess.addUser("d", "e", "f");

            assertEquals(403, (new JoinService()).join(authTok2, req).getCode());
        } catch(Exception e){
            Assertions.fail();
        }

    }

}