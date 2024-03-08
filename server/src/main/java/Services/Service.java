package Services;
import dataAccessTests.DataAccess;
import Responses.JResponse;

public class Service {

    boolean validate(String auth){
        return DataAccess.isAuthValid(auth);
    }

    JResponse unathorized() {
        JResponse res = new JResponse(401);
        res.setMessage("Error: unauthorized");
        return res;
    }
}
