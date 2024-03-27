package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;



public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        int desiredPort = 8070;
        server = new Server();
        var port = server.run(desiredPort);
        System.out.println("Started test HTTP server on " + port);

        ServerFacade.PORT = desiredPort;
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTest(){
        ServerFacade.clear();

        //test that a new user can be registered
        Assertions.assertTrue(ServerFacade.registerUser("john", "h", "email.com")!=null);

        //test that it won't allow two identical usernames to join, throws message
        Assertions.assertTrue(ServerFacade.registerUser("john", "h", "email.com").get("message").equals("Error: already taken"));
    }

    @Test
    public void logoutTest(){
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck", "emial.com").get("authToken");
        Assertions.assertTrue(ServerFacade.logout(authToken)); //test that a user can logout

        ServerFacade.clear();
        authToken = ServerFacade.registerUser("john", "duck", "emial.com").get("authToken");
        authToken = "fake_auth_token";
        Assertions.assertFalse(ServerFacade.logout(authToken)); //test that an unauthorized token cannot be logged out
    }

    @Test
    public void loginTest(){
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck", "emial.com").get("authToken");
        ServerFacade.logout(authToken);

        Assertions.assertTrue(ServerFacade.loginUser("john", "duck").get("username").equals("john"));
        Assertions.assertNotNull(ServerFacade.loginUser("john", "duck").get("authToken"));
    }



}
