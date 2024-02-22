package server;

import dataAccess.DataAccess;

public class ClearService {
    public ClearService(){};

    public void clearAll(){
        DataAccess.ClearAll();
    }
}
