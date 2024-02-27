package Services;

import dataAccess.DataAccess;

public class ClearService {
    public ClearService(){};

    public int clearAll(){
        DataAccess.clearAll();
        return 200;
    }
}
