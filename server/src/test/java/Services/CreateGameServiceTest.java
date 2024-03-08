package Services;

import Requests.CreateGameRequest;
import dataAccessTests.DataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CreateGameServiceTest {
    @Test
    public void createGameTest(){
        DataAccess.clearAll();
        String authToken = DataAccess.addUser("a", "b", "c");

        try {
            CreateGameRequest req = new CreateGameRequest();
            req.gameName = "hello";
            Assertions.assertEquals(401, (new CreateGameService()).createGame("banana", req).getCode());

            Assertions.assertEquals(200, (new CreateGameService()).createGame(authToken, req).getCode());
        } catch(Exception e){
            Assertions.fail();
        }
    }

}