package server;

import dataAccess.DataAccess;

public class ClearService {
    public ClearService(){};

    public int clearAll(){
        DataAccess.ClearAll();
        return 200;
    }
}
