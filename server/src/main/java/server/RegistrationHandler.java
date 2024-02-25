package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import spark.*;

public class RegistrationHandler extends Handler{

    public RegistrationHandler(){};

    public Object handleReq(Request req, Response res){
        Gson gson = new Gson();

        try{
            RegistrationRequest request = (RegistrationRequest)gson.fromJson(req.body(), RegistrationRequest.class);

            JResponse result = (new RegistrationService()).registerUser(request);
            res.status(result.getCode());
            res.type("application/json");
            res.body(result.getMessage());
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
