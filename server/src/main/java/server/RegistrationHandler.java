package server;

import spark.Request;
import spark.Response;

public class RegistrationHandler extends Handler{

    public RegistrationHandler(){};

    public Object handleReq(Request req, Response res){

        //only one request
        int result = (new RegistrationService()).registerUser("test", "test", "test");
        res.status(result);
        return res;
    }
}
