import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {

    public ServerFacade(){

    }

    public static void clear(){ //temporary
        try {
            URI uri = new URI("http://localhost:8080/db");
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("DELETE");//clear db

            connection.connect();

            int responseCode = connection.getResponseCode();
            if(responseCode==200)
                System.out.println("db cleared");
            else
                System.out.println("Response Code: " + responseCode);
        } catch(Exception e){
            System.out.println("Client Error: " + e.getMessage());
        }
    }

    private static Map<String, String> postCommand(String endpoint, Map<String, String> reqData, String authToken){ //creates http connection, sends Map of variables as json, returns response as Map of variables
        try {
            URI uri = new URI("http://localhost:8080" + endpoint);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");//register user

            if(authToken!=null){
                connection.setRequestProperty("authorization", authToken);
            }

            Gson gson = new Gson();
            String json = gson.toJson(reqData); //turn variables into json
            byte[] jsonDataBytes = json.getBytes(StandardCharsets.UTF_8); //translate json into bytes

            connection.setDoOutput(true);

            OutputStream outStream = connection.getOutputStream();
            outStream.write(jsonDataBytes);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) { //success
                int resCode = connection.getResponseCode();
                if(resCode == 200){
                    System.out.println("success");
                } else {
                    System.out.println("error code " + resCode);
                }

                InputStream responseBody = connection.getInputStream();
                // Read and process response body from InputStream ...
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody));
                String jsonResponse = reader.readLine();//store the json response

                Map<String, String> responseVars = gson.fromJson(jsonResponse, Map.class);

                if(jsonResponse.contains("gameID")){ //patched problem with converting int to string in json
                    String id = jsonResponse.substring(jsonResponse.indexOf("gameID")+8, jsonResponse.indexOf("gameID")+12);
                    responseVars.replace("gameID", id);
                }

                // Close resources
                reader.close();
                responseBody.close();
                connection.disconnect();

                return responseVars;
            } else {
                // SERVER RETURNED AN HTTP ERROR
                InputStream responseBody = connection.getErrorStream();
                // Read and process error response body from InputStream ...
                //FIXME add a way to display the error

                BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody));
                String jsonResponse = reader.readLine();//store the json response

                Map<String, String> responseVars = gson.fromJson(jsonResponse, Map.class);



                return responseVars;
            }
        } catch(Exception e){
            System.out.println("Client Error: " + e.getMessage());
        }
        return null;
    }

    public static Map<String, String> registerUser(String username, String password, String email){ //returns authToken
        Map<String, String> args = new HashMap<>();
        args.put("username", username);
        args.put("password", password);
        args.put("email", email);

        Map<String, String> resVars = postCommand("/user", args, null);

        return  resVars;
    }

    public static Map<String, String> loginUser(String username, String password){
        Map<String, String> args = new HashMap<>();
        args.put("username", username);
        args.put("password", password);

        Map<String, String> resVars = postCommand("/session", args, null);

        return  resVars;
    }

    public static Map<String, String> createGame(String name, String authToken){
        Map<String, String> args = new HashMap<>();
        args.put("gameName", name);

        return postCommand("/game", args, authToken);
    }
}
