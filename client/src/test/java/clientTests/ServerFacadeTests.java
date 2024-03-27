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
    public void registerTestA(){
        ServerFacade.clear();
        //test that a new user can be registered
        Assertions.assertTrue(ServerFacade.registerUser("john", "h", "email.com")!=null);
        }

    @Test
    public void registerTestB(){
        ServerFacade.clear();
        ServerFacade.registerUser("john", "h", "email.com");

        //test that it won't allow two identical usernames to join, throws message
        Assertions.assertTrue(ServerFacade.registerUser("john", "h", "email.com").get("message").equals("Error: already taken"));
    }

    @Test
    public void logoutTestA(){
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck", "emial.com").get("authToken");
        Assertions.assertTrue(ServerFacade.logout(authToken)); //test that a user can logout
    }

    @Test
    public void logoutTestB(){
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck", "emial.com").get("authToken");
        authToken = "fake_auth_token";
        Assertions.assertFalse(ServerFacade.logout(authToken)); //test that an unauthorized token cannot be logged out
    }

    @Test
    public void loginTestA(){ //needs negative test
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck", "emial.com").get("authToken");
        ServerFacade.logout(authToken);

        //assert that a valid login returns the username and an authtoken
        Assertions.assertEquals("john", ServerFacade.loginUser("john", "duck").get("username"));
        Assertions.assertNotNull(ServerFacade.loginUser("john", "duck").get("authToken"));
    }

    @Test
    public void loginTestB(){
        ServerFacade.clear();

        //assert that a user who has not been registered cannot login
        Assertions.assertNull(ServerFacade.loginUser("john", "duck").get("authToken"));
    }

    @Test
    public void createGameTestA(){
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck", "emial.com").get("authToken");

        //assert that it returns a gameID
        int gameID = Integer.parseInt(ServerFacade.createGame("testGame", authToken).get("gameID"));
        Assertions.assertNotEquals(gameID, -1);
    }

    @Test
    public void createGameTestB(){
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck", "emial.com").get("authToken");

        authToken = "false_token";
        //assert that it does not return a gameID
        Assertions.assertNull(ServerFacade.createGame("testGame", authToken).get("gameID"));
    }

    @Test
    public void clearTest(){
        Assertions.assertTrue(ServerFacade.clear());
    }

    @Test
    public void listGamesTestA(){
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck123", "email.com").get("authToken");
        ServerFacade.createGame("gameA", authToken);
        ServerFacade.createGame("gameB", authToken);

        //ensure that two gamedata objects are returned
        Assertions.assertEquals(ServerFacade.listGames(authToken).length, 2);
    }

    @Test
    public void listGamesTestB(){
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck123", "email.com").get("authToken");
        ServerFacade.createGame("gameA", authToken);
        //ServerFacade.createGame("gameB", authToken);

        //ensure that two gamedata objects are returned
        Assertions.assertNotEquals(ServerFacade.listGames(authToken).length, 2);
    }

    @Test
    public void joinTestA(){
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck123", "email.com").get("authToken");
        String gameID = ServerFacade.createGame("gameA", authToken).get("gameID");

        String message = ServerFacade.joinGame(gameID, "BLACK", authToken).trim();
        Assertions.assertEquals("success", message);
    }

    @Test
    public void joinTestB(){
        ServerFacade.clear();
        String authToken = ServerFacade.registerUser("john", "duck123", "email.com").get("authToken");
        String gameID = ServerFacade.createGame("gameA", authToken).get("gameID");

        String message = ServerFacade.joinGame(gameID, "INVALID_PLAYER_TYPE", authToken).trim();
        Assertions.assertNotEquals("success", message);
    }

}
