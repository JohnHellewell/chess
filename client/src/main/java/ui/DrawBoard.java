package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Map;

public class DrawBoard {
    public static enum ORIENTATION{WHITE, BLACK, BOTH};

    //fields
    final static String BLANK = "   ";
    final static int[] forwRows = {1, 2, 3, 4, 5, 6, 7, 8};
    final static int[] revRows = {8, 7, 6, 5, 4, 3, 2, 1};

    final static Map<ChessPiece.PieceType, String> icons = Map.of(
            ChessPiece.PieceType.KING, " K ",
            ChessPiece.PieceType.ROOK, " R ",
            ChessPiece.PieceType.QUEEN, " Q ",
            ChessPiece.PieceType.BISHOP, " B ",
            ChessPiece.PieceType.PAWN, " P ",
            ChessPiece.PieceType.KNIGHT, " N "
    );

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
            setTileColor(row, i);
            ChessPosition pos = new ChessPosition(row, i+1);
            drawPiece(board.getPiece(pos));
        }
        resetColors();
    }

    private static void drawPiece(ChessPiece piece){
        String temp = "";
        if(piece!=null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                switch (piece.getPieceType()) {
                    case PAWN -> {
                        temp = EscapeSequences.WHITE_PAWN;
                    }
                    case KING -> {
                        temp = EscapeSequences.WHITE_KING;
                    }
                    case QUEEN -> {
                        temp = EscapeSequences.WHITE_QUEEN;
                    }
                    case KNIGHT -> {
                        temp = EscapeSequences.WHITE_KNIGHT;
                    }
                    case ROOK -> {
                        temp = EscapeSequences.WHITE_ROOK;
                    }
                    case BISHOP -> {
                        temp = EscapeSequences.WHITE_BISHOP;
                    }
                    default -> {
                        temp = BLANK;
                    }
                }
            } else { //black
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
            }
        } else
            temp = BLANK;

        System.out.print(temp);
    }

    private static void drawRowNumber(int r){
        System.out.print(" " + r + " ");
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
            System.out.print(" " + c + " ");
        }
        System.out.print(BLANK);
        resetColors();
        System.out.print("\n");
    }

    private static void setTileColor(int row, int col){ //doesn't matter if the row and col are mixed up
        if((row+col)%2==0){ //white tile
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        } else { //black
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        }
    }

    private static void resetColors(){
        //set bg to black and txt to white
        System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
    }
}


