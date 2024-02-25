package server;

import dataAccess.DataAccess;

public class RegistrationService {

    public RegistrationService(){};

    public int registerUser(String username, String password, String email){ //make this return some sort of status, perhaps json
        if(DataAccess.getUser(username)==null){
            DataAccess.addUser(username, password, email);
            return 200;
        } else { //username already exists!
            return 401; //add an error message to this in the future FIXME
        }
    }
}
