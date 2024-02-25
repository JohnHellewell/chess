package server;

import dataAccess.DataAccess;

public class RegistrationService {

    public RegistrationService(){};

    public JResponse registerUser(RegistrationRequest req){ //make this return some sort of status, perhaps json
        if(DataAccess.getUser(req.getUsername())==null){
            String auth = DataAccess.addUser(req.getUsername(), req.getPassword(), req.getEmail());
            return new JResponse(200, "{\"username\": \""+req.getUsername()+"\", \"authToken\":\"" + auth + "\"}");//FIXME should return multiple items
        } else { //username already exists!//works
            return new JResponse(403, "{\"message\": \"Error: already taken\"}"); //add an error message to this in the future FIXME
        }
    }
}
