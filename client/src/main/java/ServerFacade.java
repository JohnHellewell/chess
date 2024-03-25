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

            //connection.setDoOutput(true);

            //OutputStream outStream = connection.getOutputStream();

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get HTTP response headers, if necessary
                // Map<String, List<String>> headers = connection.getHeaderFields();

                // OR

                //connection.getHeaderField("Content-Length");

                InputStream responseBody = connection.getInputStream();
            } else {
                // SERVER RETURNED AN HTTP ERROR

                InputStream responseBody = connection.getErrorStream();
                // Read and process error response body from InputStream ...
                if (responseBody != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody))) {
                        String line;
                        StringBuilder errorMessage = new StringBuilder();

                        // Read each line from the InputStream
                        while ((line = reader.readLine()) != null) {
                            errorMessage.append(line).append("\n");
                        }

                        // Print the error message
                        System.err.println("Error Message: " + errorMessage.toString());
                    } catch (IOException e) {
                        e.printStackTrace(); // Handle IOException if any
                    } finally {
                        try {
                            responseBody.close(); // Close the InputStream
                        } catch (IOException e) {
                            e.printStackTrace(); // Handle IOException if any
                        }
                    }
                } else {
                    System.err.println("No error message available."); // No error message available
                }
            }


        } catch(Exception e){
            System.out.println("Client Error: " + e.getMessage());
        }
    }

    public static String registerUser(String username, String password, String email){ //returns authToken
        try {
            URI uri = new URI("http://localhost:8080/user");
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            //http.setRequestMethod("GET");

            // Make the request
            //connection.connect();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");//register user

            //create map of variables
            Map<String, String> reqData = new HashMap<>();
            reqData.put("username", username);
            reqData.put("password", password);
            reqData.put("email", email);

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
                }

                InputStream responseBody = connection.getInputStream();
                // Read and process response body from InputStream ...
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody));
                String jsonResponse = reader.readLine();//store the json response

                Map<String, String> responseVars = gson.fromJson(jsonResponse, Map.class);
                //System.out.println("username: " + responseVars.get("username"));
                //System.out.println("authToken: " + responseVars.get("authToken"));
                String authToken = responseVars.get("authToken");
                //temporary
                //System.out.println(jsonResponse);

                // Close resources
                reader.close();
                responseBody.close();
                connection.disconnect();

                return authToken;
            } else {
                // SERVER RETURNED AN HTTP ERROR

                InputStream responseBody = connection.getErrorStream();
                // Read and process error response body from InputStream ...

            }


        } catch(Exception e){
            System.out.println("Client Error: " + e.getMessage());
        }

        return "";
    }


}
