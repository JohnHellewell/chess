package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

public class LoginHandler extends Handler{


    @Override
    public Object handleReq(Request req, Response res) {
        Gson gson = new Gson();

        try{
            LoginRequest request = (LoginRequest) gson.fromJson(req.body(), LoginRequest.class);

            JResponse result = (new LoginService()).login(request);
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
