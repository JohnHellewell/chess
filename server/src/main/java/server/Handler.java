package server;

import spark.Request;
import spark.Response;

public abstract class Handler {
    public abstract Object handleReq(Request req, Response res);
}
