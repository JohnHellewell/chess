package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

public class ClearHandler extends Handler{

    public ClearHandler(){};

    public Object handleReq(Request req, Response res){//FIXME, always returns 200



        // Set HTTP status code
        res.status(200);

        // Set content type
        res.type("application/json");

        // Create a JSON response
        String jsonResponse = "{\"message\": \"success\"}";

        // Set the response body
        res.body(jsonResponse);

        return res.body();
    }
    /*
    LoginRequest request = (LoginRequest)gson.fromJson(reqData, LoginRequest.class);

    LoginService service = new LoginService();
    LoginResult result = service.login(request);

    return gson.toJson(result);
     */
}
