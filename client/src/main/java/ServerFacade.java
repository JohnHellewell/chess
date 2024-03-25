import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.*;

public class ServerFacade {

    public ServerFacade(){

    }

    public void registerUser(String username, String password){
        try {
            URI uri = new URI("http://localhost:8080/user");
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            //http.setRequestMethod("GET");

            // Make the request
            connection.connect();

            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");//register user

            // Set HTTP request headers, if necessary
            connection.addRequestProperty("username", "test");
            connection.addRequestProperty("password", "test");

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get HTTP response headers, if necessary
                // Map<String, List<String>> headers = connection.getHeaderFields();

                // OR

                //connection.getHeaderField("Content-Length");

                InputStream responseBody = connection.getInputStream();
                // Read and process response body from InputStream ...
            } else {
                // SERVER RETURNED AN HTTP ERROR

                InputStream responseBody = connection.getErrorStream();
                // Read and process error response body from InputStream ...
            }


        } catch(Exception e){
            System.out.println("Client Error: " + e.getMessage());
        }
    }


}
