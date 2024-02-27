package Responses;

public class JResponse {
    public int code, gameID;
    public String message, password, username, authToken;

    public JResponse(int code){
        this.code = code;
        gameID = 0;
    }

    @Override
    public String toString() {
        if(message==null&&password==null&&username==null&&authToken==null&&gameID==0)
            return "{\"message\": \" success \"}";

        String str = "{";

        if(message!=null){
            str+= "\"message\": \"" + message + "\",";
        } else {
            if(username!=null)
                str += "\"username\":\"" + username + "\",";
            if(password!=null)
                str += "\"password\":\"" + password + "\",";
            if(authToken!=null)
                str += "\"authToken\":\"" + authToken + "\",";
            if(gameID != 0){
                str += "\"gameID\":" + gameID + ",";
            }
        }
        str = str.substring(0, str.length()-1) + "}";//get rid of last comma

        return str;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getCode() {
        return code;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


}
