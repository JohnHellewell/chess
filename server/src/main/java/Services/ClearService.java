package Services;

import dataAccessTests.DataAccess;

public class ClearService {
    public ClearService(){};

    public int clearAll(){
        DataAccess.clearAll();
        return 200;
    }
}
