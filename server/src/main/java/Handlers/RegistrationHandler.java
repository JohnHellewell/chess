package Handlers;

import Responses.JResponse;
import Requests.RegistrationRequest;
import Services.RegistrationService;
import spark.*;

public class RegistrationHandler extends Handler{

    public RegistrationHandler(){};

    public Object handleReq(Request req, Response res){

        try{
            RegistrationRequest request = (RegistrationRequest)gson.fromJson(req.body(), RegistrationRequest.class);

            JResponse result = (new RegistrationService()).registerUser(request);
            res.status(result.getCode());
            res.type("application/json");
            res.body(result.toString());
            return res.body();
        } catch(Exception e){ //cant parse
            res.status(400);
            res.type("application/json");
            String jsonResponse = "{ \"message\": \"Error: bad request\" }";
            res.body(jsonResponse);
            return res.body();
        }

    }
}
