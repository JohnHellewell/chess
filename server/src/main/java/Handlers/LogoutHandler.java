package Handlers;

import Responses.JResponse;
import Requests.LogoutRequest;
import Services.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handler{
    @Override
    public Object handleReq(Request req, Response res) {

        try{
            LogoutRequest request = new LogoutRequest();

            request.setAuthToken(req.headers("authorization"));

            JResponse result = (new LogoutService()).logoutUser(request);//error here

            if(result.getCode()==200){
                res.status(result.getCode());
                res.type("application/json");
                res.body("{\"message\": \"success\"}");
                return res.body();
            }

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
