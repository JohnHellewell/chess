import model.GameData;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class ClientMain {

    private static boolean loggedIn = false;

    private static String authToken = "";
    private static String username = "";
    public static void main(String[] args) {
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Client: " + piece);
        System.out.println("♕ 240 Chess Client ♕");
        help();
        awaitInput();
    }

    private static void awaitInput(){
        if(loggedIn){
            System.out.print("[LOGGED_IN] "+ username +" >>> ");
        } else {
            System.out.print("[LOGGED_OUT] >>> ");
        }
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        parseInput(input);

        awaitInput();//calls back on itself to await another command
    }

    private static void parseInput(String input){
        String command;
        if(input.indexOf(' ')==-1){
            command = input;
        } else {
            command = input.substring(0, input.indexOf(' '));
        }

        switch (command.toUpperCase()) {
            case "HELP": {
                help();
                break;
            }
            case "QUIT": { //later, make sure to log out when this is called!
                System.exit(0);
                break; //i shouldn't need this line but i included it
            }
            case "REGISTER": { //logged out exclusive
                if(!loggedIn) {
                    register(input.split(" "));
                    break;
                }
            }
            case "LOGIN": { //logged out exclusive
                if(!loggedIn) {
                    login(input.split(" "));
                    break;
                }
            }
            case "CREATE":{ //logged in exclusive
                if(loggedIn){
                    create(input.split(" "));
                    break;
                }
            }
            case "LOGOUT":{//logged in exclusive
                if(loggedIn){
                    logout();
                    break;
                }
            }
            case "LIST":{//logged in exclusive
                if(loggedIn){
                    list();
                    break;
                }
            }
            case "CLEAR":{
                ServerFacade.clear();
                break;
            }
            default: {
                unrecognizedCommand(new String[]{command});
                break;
            }
        }

    }

    private static void unrecognizedCommand(String[] str){
        String temp = "";
        for(String s : str)
            temp+=s+" ";
        System.out.println("ERROR: Invalid command \'" + temp.trim() + "\'. Please try again. Type \'help\' for a list of commands");
    }

    private static void register(String[] args){
        // expecting "<USERNAME>", "<PASSWORD>", "<EMAIL>"
        if(args.length!=4) {
            unrecognizedCommand(args);
            return;
        }


        Map<String, String> response = ServerFacade.registerUser(args[1], args[2], args[3]);
        authToken = response.get("authToken");
        if(authToken==null){
            try{
                System.out.println(response.get("message"));
            }catch(Exception f){
                System.out.println("Error: no error msg available");
            }
        } else {
            loginSuccess(args[1]);
        }
    }

    private static void login(String[] args) {
        // expecting "login <USERNAME> <PASSWORD>"
        if (args.length != 3){
            unrecognizedCommand(args);
            return;
        }

        Map<String, String> response = ServerFacade.loginUser(args[1], args[2]);
        authToken = response.get("authToken");
        if (authToken == null) {
            try {
                System.out.println(response.get("message"));
            } catch (Exception f) {
                System.out.println("Error: no error msg available");
            }
        } else {
            loginSuccess(args[1]);
        }

    }


    private static void loginSuccess(String u){
        if(authToken!=null) {
            loggedIn = true;
            username = u;
            System.out.println(authToken);//remove later
        } else {
            System.out.println("Error, authToken is null");
        }
        help();
    }

    private static void help(){
        System.out.println("Commands:");
        if(!loggedIn) { //Logged OUT menu
            System.out.println("\tregister <USERNAME> <PASSWORD> <EMAIL>\t-create a new account");
            System.out.println("\tlogin <USERNAME> <PASSWORD>\t-login into an existing account");
        } else { //Logged IN menu
            System.out.println("\tcreate <NAME>\t-create a new game");
            System.out.println("\tjoin <GAME ID> [\"WHITE\"|\"BLACK\"|blank]\t-join a game as white, black, or spectator");
            System.out.println("\tlist\t-list all games");
            System.out.println("\tobserve <GAME ID>\t-spectate a game");
            System.out.println("\tlogout\t-logout");
        }
        System.out.println("\thelp\t-display a list of commands");
        System.out.println("\tquit\t-close chess program");
    }

    private static void create(String[] args){ //returns the gameID
        if(args.length!=2) {
            unrecognizedCommand(args);
            return;
        }
        Map<String, String> response = ServerFacade.createGame(args[1], authToken);
        if(response.containsKey("message")){
            System.out.println(response.get("message"));
        }
        //Double id = Double.valueOf(response.get("gameID"));
        System.out.println("\"" + args[1] + "\" created successfully with gameID: " + response.get("gameID"));
    }

    private static void logout(){
        if(ServerFacade.logout(authToken)){
            loggedIn=false;
            authToken=null;
            username=null;
            help();
        }

    }

    private static void list(){
        GameData[] games = ServerFacade.listGames(authToken);
        if(games!=null && games.length>0) {
            for (GameData game : games) {
                System.out.println("\tGame Name: " + game.getGameName() + "\tGame ID: " + game.getGameID()
                        + "\tWhite Player: " + game.getWhiteUsername() + "\tBlack Player: " + game.getBlackUsername());
            }
        } else
            System.out.println("No games found.");
    }
}