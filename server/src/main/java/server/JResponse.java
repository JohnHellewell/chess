package server;

public class JResponse {
    public int code;
    public String message, password, username, authToken;

    public JResponse(int code){
        this.code = code;
    }

    @Override
    public String toString() {
        String str = "{";

        if(message!=null){
            str+= "\"message\": \"" + message + "\",";
        } else {
            if(username!=null)
                str += "\"username\": \"" + username + "\",";
            if(password!=null)
                str += "\"password\": \"" + password + "\",";
            if(authToken!=null)
                str += "\"authToken\": \"" + authToken + "\",";
        }
        str = str.substring(0, str.length()-1) + "}";//get rid of last comma

        return str;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


}
