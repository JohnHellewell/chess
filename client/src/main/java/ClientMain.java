import java.util.Scanner;
public class ClientMain {

    private static boolean loggedIn = false;

    private static String authToken = "";
    public static void main(String[] args) {
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Client: " + piece);
        System.out.println("♕ 240 Chess Client ♕");
        help();
        awaitInput();
    }

    private static void awaitInput(){
        if(loggedIn){
            System.out.print("[LOGGED_IN] >>> ");
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
            case "REGISTER": {
                register(input.split(" "));
                break;
            }
            case "LOGIN": {
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
        if(!authToken.isEmpty())
            loggedIn = true;
    }

    private static void login(String[] args){
        // expecting "login <USERNAME> <PASSWORD>"
        if(args.length!=3)
            unrecognizedCommand(args);

        //FIXME add code to login user
    }

    private static void help(){
        System.out.println("Commands:");
        if(!loggedIn) { //Logged OUT menu
            System.out.println("\tregister <USERNAME> <PASSWORD> <EMAIL>\t-create a new account");
            System.out.println("\tlogin <USERNAME> <PASSWORD>\t\t\t\t-login into an existing account");
        } else { //Logged IN menu
            System.out.println("\tregister <USERNAME> <PASSWORD> <EMAIL>\t-create a new account");
            System.out.println("\tlogin <USERNAME> <PASSWORD>\t\t\t\t-login into an existing account");
        }
        System.out.println("\thelp\t\t\t\t\t\t\t\t\t-display a list of commands");
        System.out.println("\tquit\t\t\t\t\t\t\t\t\t-close chess program");
    }
}