import chess.*;
import java.util.Scanner;
public class Main {

    static boolean loggedIn = false;
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

        awaitInput();
    }

    private static void parseInput(String input){
        String command;
        if(input.indexOf(' ')==-1){
            command = input;
        } else {
            command = input.substring(0, input.indexOf(' '));
        }

        switch(command.toUpperCase()){
            case "HELP":{
                help();
                break;
            }
            case "QUIT":{ //later, make sure to log out when this is called!
                System.exit(0);
                break; //i shouldn't need this line but i included it
            }
            case "REGISTER":{
                register(input.split(' '));
                break;
            }
            default:{
                System.out.println("Invalid command \'" + command + "\'. Please try again. Type \'help\' for a list of commands");
                //help();
                break;
            }
        }
    }

    private static boolean verifyCommandFormat(String str){ //makes sure there is only one set of <> brackets, and no spaces
        if(str.charAt(0)!='<' || str.charAt(str.length()-1)!='>') //begins and ends with brackets
            return false;
        if(str.indexOf(' ')>=0)// no spaces
            return false;
        if(str.indexOf('<', 1)>=0 || str.indexOf('>')<str.length()-1)//checks that there are no other brackets
            return false;

        //all tests passed
        return true;
    }

    private static String removeAngleBrackets(String str){ //verifies that
        if(verifyCommandFormat(str)) {//double check
            return str.substring(1, str.length()-1);
        } else {
            return str;
        }
    }

    private void unrecognizedCommand(String str){

    }

    private static void register(String[] args){
        // expecting "<USERNAME>", "<PASSWORD>", "<EMAIL>"
        if(args.length!=4)
            
    }

    private static void help(){
        System.out.println("Commands:");
        System.out.println("\tregister <USERNAME> <PASSWORD> <EMAIL>\t-create a new account");
        System.out.println("\tlogin <USERNAME> <PASSWORD>\t\t\t\t-login into an existing account");
        System.out.println("\thelp\t\t\t\t\t\t\t\t\t-display a list of commands");
        System.out.println("\tquit\t\t\t\t\t\t\t\t\t-close chess program");
    }
}