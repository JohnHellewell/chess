package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.Set;

public class LogoutHandler extends Handler{
    @Override
    public Object handleReq(Request req, Response res) {

        try{
            LogoutRequest request = new LogoutRequest();
            //request.setAuthToken(req.headers().toString()); //combines all headers into one string, but there should only be one string in the first place
            //Set<String> heads = req.header("authoriza");
            //for(String s : heads){
            //    ;
            //}
            
            
            
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
