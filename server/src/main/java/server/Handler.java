package server;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

public abstract class Handler {

    Gson gson = new Gson();
    public abstract Object handleReq(Request req, Response res);


}
