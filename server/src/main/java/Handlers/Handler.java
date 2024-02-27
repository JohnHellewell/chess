package Handlers;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

public abstract class Handler {

    Gson gson = new Gson();
    public abstract Object handleReq(Request req, Response res);

    String badRequest(Response res){
        res.status(400);
        res.type("application/json");
        String jsonResponse = "{ \"message\": \"Error: bad request\" }";
        res.body(jsonResponse);
        return res.body();
    }
}
