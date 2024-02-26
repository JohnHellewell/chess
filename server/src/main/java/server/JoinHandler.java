package server;

import spark.Request;
import spark.Response;

public class JoinHandler extends Handler{
    @Override
    public Object handleReq(Request req, Response res) {
        try {
            JoinRequest jRequest = (JoinRequest) gson.fromJson(req.body(), JoinRequest.class);
            String auth = req.headers("authorization");

            JResponse jResponse = (new JoinService()).join(auth, jRequest);

            res.status(jResponse.getCode());
            res.type("applicaiton/json");
            res.body(jResponse.toString());
            return res.body();
        } catch(Exception e){ //bad request
            return badRequest(res);
        }
    }
}
