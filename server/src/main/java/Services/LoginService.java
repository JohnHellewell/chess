package Services;

import Requests.LoginRequest;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.UserData;
import Responses.JResponse;

public class LoginService {

    public JResponse login(LoginRequest req) throws DataAccessException {
        if(req.getUsername()==null||req.getPassword()==null)
            throw new DataAccessException("");

        //see if already exists
        UserData user = DataAccess.getUser(req.getUsername());
        if(user==null){
            //error, not a user
            JResponse res = new JResponse(401);
            res.setMessage("Error: unauthorized");
            return res;
        } else {
            //see if password matches
            if(!user.getPassword().equals(req.getPassword())){ //normal user login is getting an error here
                //wrong password!
                JResponse res = new JResponse(401);
                res.setMessage("Error: unauthorized");
                return res;
            } else { //correct password 
                String token = DataAccess.login(req.getUsername());
                JResponse res = new JResponse(200);
                res.setUsername(req.getUsername());
                res.setAuthToken(token);
                return res;
            }
        }
    }

}
