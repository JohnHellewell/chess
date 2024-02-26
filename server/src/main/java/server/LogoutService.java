package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;

public class LogoutService {


    public JResponse logoutUser(LogoutRequest req) throws DataAccessException {
        //test
        //JResponse res = new JResponse(200);
        //return res;

        //make sure it isn't null
        if(req.getAuthToken()==null) {
            //throw new DataAccessException("");
            JResponse res = new JResponse(401);
            res.setMessage("Error: unauthorized");
            return res;
        }
        //test
        //JResponse res = new JResponse(200);
        //return res;


        //check for valid auth
        if (DataAccess.isAuthValid(req.getAuthToken())) {
            JResponse res = new JResponse(200);
            return res;
        } else { //invalid response
            JResponse res = new JResponse(401);
            res.setMessage("Error: unauthorized");
            return res;
        }



    }
}
