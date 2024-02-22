package server;

import spark.Request;
import spark.Response;

public class ClearHandler {

    public ClearHandler(){};

    public Object handleReq(Request req, Response res){
        // Set HTTP status code
        res.status(200);

        // Set content type
        res.type("application/json");

        // Create a JSON response
        String jsonResponse = "{\"message\": \"Hello, Spark!\"}";

        // Set the response body
        res.body(jsonResponse);

        // Return the response body (optional)
        return res.body();
    }
    /*
    LoginRequest request = (LoginRequest)gson.fromJson(reqData, LoginRequest.class);

    LoginService service = new LoginService();
    LoginResult result = service.login(request);

    return gson.toJson(result);
     */
}
