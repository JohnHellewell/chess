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
        if(args.length!=4)
            unrecognizedCommand(args);

        authToken = ServerFacade.registerUser(args[1], args[2], args[3]);
        loginSuccess(args[1]);
    }

    private static void login(String[] args){
        // expecting "login <USERNAME> <PASSWORD>"
        if(args.length!=3)
            unrecognizedCommand(args);

        authToken = ServerFacade.loginUser(args[1], args[2]);
        loginSuccess(args[1]);
    }

    private static void loginSuccess(String u){
        if(!authToken.isEmpty()) {
            loggedIn = true;
            username = u;
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


}