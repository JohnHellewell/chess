package Services;

import dataAccessTests.DataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class GetGamesServiceTest {
    @Test
    public void getGamesTest(){
        DataAccess.clearAll();
        String authToken = DataAccess.addUser("a", "b", "c");
        try{
            Assertions.assertEquals(0, (new GetGamesService()).getGames(authToken).games.size());

            try{
                (new GetGamesService()).getGames("banana"); //should throw an error
                Assertions.fail();
            } catch(Exception e){
                //pass
            }
        } catch (Exception e){
            Assertions.fail();
        }
    }

}