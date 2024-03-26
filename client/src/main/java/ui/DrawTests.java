package ui;

import chess.ChessBoard;
import chess.ChessGame;
import org.junit.jupiter.api.Test;

public class DrawTests {

    @Test
    public void drawBoardTest(){
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        DrawBoard.drawBoard(board, DrawBoard.ORIENTATION.WHITE);
    }

}
