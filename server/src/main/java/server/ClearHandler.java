package server;

import spark.Request;
import spark.Response;

public class ClearHandler {

    public ClearHandler(){};

    public Object handleReq(Request req, Response res){
        res.status(200);
        return res.body();
    }
}
