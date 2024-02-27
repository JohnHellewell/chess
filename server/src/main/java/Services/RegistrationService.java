package Services;

import Requests.RegistrationRequest;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import Responses.JResponse;

public class RegistrationService {

    public RegistrationService(){}

    public JResponse registerUser(RegistrationRequest req) throws DataAccessException { //make this return some sort of status, perhaps json
        //check that the request fields are valid
        if(req.getUsername()==null||req.getPassword()==null||req.getEmail()==null)
            throw new DataAccessException("");

        if(DataAccess.getUser(req.getUsername())==null){
            String auth = DataAccess.addUser(req.getUsername(), req.getPassword(), req.getEmail());
            JResponse res = new JResponse(200);
            res.setUsername(req.getUsername());
            res.setAuthToken(auth);
            return res;
        } else { //username already exists!//works
            JResponse res = new JResponse(403);
            res.setMessage("Error: already taken");

            return res;
        }
    }
}
