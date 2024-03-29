package Handlers;

import Services.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler extends Handler{

    public ClearHandler(){};

    public Object handleReq(Request req, Response res){

        int code = (new ClearService()).clearAll();

        // Set HTTP status code
        res.status(code);

        // Set content type
        res.type("application/json");


        // Set the response body
        res.body("{\"message\": \"success\"}");

        return res.body();
    }

}
