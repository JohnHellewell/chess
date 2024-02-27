package Handlers;

import Responses.JResponse;
import Requests.LoginRequest;
import Services.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler extends Handler{


    @Override
    public Object handleReq(Request req, Response res) {

        try{
            LoginRequest request = (LoginRequest) gson.fromJson(req.body(), LoginRequest.class);

            JResponse result = (new LoginService()).login(request);
            res.status(result.getCode());
            res.type("application/json");
            res.body(result.toString());
            return res.body();
        } catch(Exception e){ //cant parse
            return badRequest(res);
        }
    }
}
