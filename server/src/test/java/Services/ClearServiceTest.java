package Services;

import dataAccess.DataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClearServiceTest {
    @Test
    public void clearTest(){
        DataAccess.addUser("a", "b", "c");
        Assertions.assertEquals("a", DataAccess.getUser("a").getUsername());

        ClearService cs = new ClearService();
        cs.clearAll();
        Assertions.assertNull(DataAccess.getUser("a"));

    }

}