package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import ui.DrawBoard;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class ClientMain {

    private static boolean loggedIn = false;
    private static boolean gamePlay = false;

    public static String authToken = "";
    private static String username = "";

    public static int gameID = -1; //gameID of what should be displayed
    public static ChessGame game = null;

    enum playerType{WHITE, BLACK, SPECTATOR};
    public static playerType player =  playerType.SPECTATOR;
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
        if(loggedIn){
            if(gamePlay)
                parseGamePlayInput(input.split(" "));
            else{
                switch (command.toUpperCase()) {
                    case "HELP": {
                        help();
                        break;
                    }
                    case "QUIT": {
                        System.exit(0);
                    }
                    case "CREATE": { //logged in exclusive
                        create(input.split(" "));
                        break;
                    }
                    case "LOGOUT": {//logged in exclusive
                        logout();
                        break;
                    }
                    case "LIST": {//logged in exclusive
                        list(-1);
                        break;
                    }
                    case "JOIN": {
                        join(input.split(" "));
                        break;
                    }
                    case "CLEAR": {
                        ServerFacade.clear();
                        break;
                    }
                    default: {
                        unrecognizedCommand(new String[]{command});
                        break;
                    }
                }
            }
        } else {
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
                    register(input.split(" "));
                    break;
                }
                case "LOGIN": { //logged out exclusive
                    login(input.split(" "));
                    break;
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
    }

    public static void reloadBoard(ChessGame g){
        game = g;
        System.out.println("");
        drawBoard();
    }

    private static void parseGamePlayInput(String[] input){
        String command = input[0].toUpperCase();
        switch(command){ //this switch statement is for the shared functions
            case "HIGHLIGHT":{
                if(input.length!=2 || !verifySquare(input[1]))
                    unrecognizedCommand(input);
                //correct command format, and the square is a valid square
                drawBoard(input[1]);
                break;
            }
            case "REDRAW":{
                drawBoard();
                break;
            }
            case "HELP":{
                help();
                break;
            }
            case "QUIT":{
                System.exit(0);
            }
            default:{ //for player/spectator specific functions
                if(player == playerType.SPECTATOR){
                    switch(command){
                        default: {
                            unrecognizedCommand(new String[]{command});
                            break;
                        }
                    }
                } else { //player is black or white
                    switch(command){
                        default: {
                            unrecognizedCommand(new String[]{command});
                            break;
                        }
                    }
                }
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
            //System.out.println(authToken);//remove later
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
        } else {
            if (gamePlay){ //gameplay menu
                System.out.println("\tReDraw\t-redraw the chess board");
                System.out.println("\tHighlight <SQUARE>\t-show all legal moves for a specified piece");
                if(player == playerType.SPECTATOR){ //spectator options
                    System.out.println("\tLeave\t-Leave the game");
                } else { //player options
                    System.out.println("\tMove <START SQUARE> <END SQUARE>\t-move a piece");
                    System.out.println("\tResign\t-Resign the game");
                }
            } else { //Logged IN menu
                System.out.println("\tcreate <NAME>\t-create a new game");
                System.out.println("\tjoin <GAME ID> [\"WHITE\"|\"BLACK\"|blank]\t-join a game as white, black, or spectator");
                System.out.println("\tlist\t-list all games");
                System.out.println("\tobserve <GAME ID>\t-spectate a game");
                System.out.println("\tlogout\t-logout");
            }
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

    private static void list(int getGame){ //-1 to display to screen, or give a gameID to update this.game (with no display)
        GameData[] games = ServerFacade.listGames(authToken);
        if(games!=null && games.length>0) {
            for (GameData gameData : games) {
                if (getGame==-1) { //just display the games to the screen
                    System.out.println("\tGame Name: " + gameData.getGameName() + "\tGame ID: " + gameData.getGameID()
                            + "\tWhite Player: " + gameData.getWhiteUsername() + "\tBlack Player: " + gameData.getBlackUsername());
                } else {
                    if(gameData.getGameID()==getGame){
                        game = gameData.getGame();
                        gameID = getGame;
                        break;
                    }
                }
            }
        } else
            System.out.println("No games found.");
    }

    private static void join(String[] args){
        if(args.length!=2 && args.length!=3){
            unrecognizedCommand(args);
            return;
        }
        //verify that the third argument is "WHITE" or "BLACK"
        if(args.length==3 && !args[2].equals("WHITE")&& !args[2].equals("BLACK")){
            unrecognizedCommand(args);
        } else { //correct format
            //join or spectate
            String message;
            if(args.length==2){
                //spectate
                message = ServerFacade.spectateGame(args[1], authToken);
                player = playerType.SPECTATOR;
                gamePlay = true;
            } else { //length==3
                message = ServerFacade.joinGame(args[1], args[2], authToken);
                gamePlay = true;
                if(args[2].equals("WHITE")){
                    player = playerType.WHITE;
                } else {
                    player = playerType.BLACK;
                }
            }
            System.out.println(message);

            if(message.trim().equals("success")){
                updateGame(Integer.parseInt(args[1]));
            }
        }

        //establish web socket connection
        try{
            ServerFacade sf = new ServerFacade();
            sf.openWebSocket();
            sf.joinGame(authToken, gameID, args.length>2);
        }catch(Exception e){
            System.out.println("web socket connection failed");
            System.out.println(e.getMessage());
        }
    }



    private static void updateGame(int gameID){
        list(gameID);
        drawBoard();
    }

    private static void drawBoard(){
        DrawBoard.drawBoard(game.getBoard(), getOri());
    }

    private static void drawBoard(String square){
        DrawBoard.drawBoard(game.getBoard(), getOri(), square);
    }

    private static DrawBoard.ORIENTATION getOri(){
        if(player==playerType.BLACK)
            return DrawBoard.ORIENTATION.BLACK;
        else
            return DrawBoard.ORIENTATION.WHITE;
    }


    private static boolean verifySquare(String square){
        square = square.toLowerCase().trim();
        if(square.length()!=2)
            return false;
        if(square.charAt(0)<'a' || square.charAt(0)>'h')
            return false;
        if(square.charAt(1)<'1' || square.charAt(1)>'8')
            return false;

        //valid square has been called
        return true;
    }
}