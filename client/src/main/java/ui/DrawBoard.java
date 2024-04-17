package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Map;

public class DrawBoard {
    public static enum ORIENTATION{WHITE, BLACK, BOTH};

    //fields
    final static String BLANK = EscapeSequences.EMPTY;
    final static int[] forwRows = {1, 2, 3, 4, 5, 6, 7, 8};
    final static int[] revRows = {8, 7, 6, 5, 4, 3, 2, 1};

    static boolean highlight = false;
    static String pos;


    final static Map<ChessPiece.PieceType, String> icons = Map.of(
            ChessPiece.PieceType.KING, " K ",
            ChessPiece.PieceType.ROOK, " R ",
            ChessPiece.PieceType.QUEEN, " Q ",
            ChessPiece.PieceType.BISHOP, " B ",
            ChessPiece.PieceType.PAWN, " P ",
            ChessPiece.PieceType.KNIGHT, " N "
    );

    public static void drawBoard(ChessBoard board, ORIENTATION ori, String square){
        highlight = true;
        pos = square;
        drawBoard(board, ori);
    }

    public static void drawBoard(ChessBoard board, ORIENTATION ori){
        if(ori==ORIENTATION.BOTH){
            drawBoard(board, ORIENTATION.WHITE);
            System.out.print("\n");
            drawBoard(board, ORIENTATION.BLACK);
            return;
        }

        drawRanks(ori);
        int[] rows = new int[8];
        if(ori==ORIENTATION.BLACK){
            rows = forwRows;
        } else {
            rows = revRows;
        }
        for(int i=0; i<8; i++){
            drawRowNumber(rows[i]);
            drawRow(board, rows[i], ori);
            drawRowNumber(rows[i]);
            System.out.println("");
        }
        drawRanks(ori);
    }

    private static void drawRow(ChessBoard board, int row, ORIENTATION ori){


        for(int i=0; i<8; i++){
            setTileColor(row, i, board);
            ChessPosition pos;
            if(ori==ORIENTATION.WHITE) {
                pos = new ChessPosition(row, i + 1);
            } else { //black
                pos = new ChessPosition(row, 8-i);
            }
            drawPiece(board.getPiece(pos));
        }
        resetColors();
    }

    private static void drawPiece(ChessPiece piece){
        String temp = "";
        if(piece!=null) {
            switch (piece.getPieceType()) {
                case PAWN -> {
                    temp = EscapeSequences.BLACK_PAWN;
                }
                case KING -> {
                    temp = EscapeSequences.BLACK_KING;
                }
                case QUEEN -> {
                    temp = EscapeSequences.BLACK_QUEEN;
                }
                case KNIGHT -> {
                    temp = EscapeSequences.BLACK_KNIGHT;
                }
                case ROOK -> {
                    temp = EscapeSequences.BLACK_ROOK;
                }
                case BISHOP -> {
                    temp = EscapeSequences.BLACK_BISHOP;
                }
                default -> {
                    temp = BLANK;
                }
            }
            if(piece.getTeamColor()== ChessGame.TeamColor.WHITE){
                System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
            } else {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
            }

        } else
            temp = EscapeSequences.EMPTY;

        System.out.print(temp);

        //reset text color
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private static void drawRowNumber(int r){
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        System.out.print("\u2003" + r + " ");
        System.out.print(EscapeSequences.RESET_BG_COLOR);
    }

    private static void drawRanks(ORIENTATION ori){
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        System.out.print(BLANK);

        char[] ranks = new char[8];
        if(ori==ORIENTATION.WHITE) {
            for (int i = 0; i < 8; i++) {
                ranks[i] = (char) (i + 'a');
            }
        }
        else {
            for (int i = 0; i < 8; i++) {
                ranks[i] = (char) ('h' - i);
            }
        }

        for(char c : ranks){
            System.out.print(" " + c + "\u2003");

        }
        System.out.print(BLANK);
        resetColors();
        System.out.print("\n");
    }

    private static void setTileColor(int row, int col, ChessBoard board){
        if(highlightTile(row, col, board)){
            System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
        } else {
            if ((row + col) % 2 == 0) { //white tile
                System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
            } else { //black
                System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
            }
        }
    }

    private static boolean highlightTile(int row, int col, ChessBoard board){
        if(!highlight)
            return false;
        //highlight tiles is turned on
        ChessPosition temp = new ChessPosition( (int)(pos.charAt(1)-'1')+1 , (int)(pos.charAt(0)-'a')+1);//position of the piece specified in the string 'pos'
        if(board.getPiece(temp)!=null) {
            ArrayList<ChessMove> validMoves  = board.getMoves(temp);

            for(ChessMove move : validMoves){
                if(move.getEndPosition().getRow()==row && move.getEndPosition().getColumn()-1==col)
                    return true;
            }
            return false;
        } else
            return false;
    }



    private static void resetColors(){
        //set bg to black and txt to white
        System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
    }
}


