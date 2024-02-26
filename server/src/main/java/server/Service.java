package server;

import dataAccess.DataAccess;

import javax.xml.crypto.Data;

public class Service {

    boolean validate(String auth){
        return DataAccess.isAuthValid(auth);
    }
}
