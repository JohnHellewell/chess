package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board;
    ChessMove lastMove;
    boolean whiteShort, whiteLong, blackShort, blackLong;


    public ChessBoard() {
        board = new ChessPiece[8][8];
        whiteShort = true;
        whiteLong = true;
        blackShort = true;
        blackLong = true;
        lastMove = null;
    }

    public boolean getWhiteShort() {
        return whiteShort;
    }

    public boolean getWhiteLong() {
        return whiteLong;
    }

    public boolean getBlackShort() {
        return blackShort;
    }

    public boolean getBlackLong() {
        return blackLong;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece; //assuming index of 1-8
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1]; //assuming index 1-8
    }

    public ArrayList<ChessMove> getMoves(ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        if(getPiece(pos)==null)
            return null;
        else {
            moves.addAll(getPiece(pos).pieceMoves(this, pos));
            moves.addAll(getCastleMoves(pos));
            return moves;
        }

    }

    private ArrayList<ChessMove> getCastleMoves(ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = getPiece(pos).getTeamColor();
        if(color== ChessGame.TeamColor.WHITE){
            if(canWhiteLong()){
                moves.add(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 3), null));
            }
            if(canWhiteShort()){
                moves.add(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null));
            }
        } else { //black

        }

        return moves;
    }

    private boolean canWhiteLong(){ //not finished, needs to check for checks
        ChessPiece rook = board[0][0];
        ChessPiece king = board[0][4];
        boolean temp = whiteLong && rook!=null&&rook.getTeamColor()== ChessGame.TeamColor.WHITE&&rook.getPieceType()== ChessPiece.PieceType.ROOK
                && board[0][1]==null&&board[0][2]==null&&board[0][3]==null
                && king!=null && king.getTeamColor()== ChessGame.TeamColor.WHITE&&king.getPieceType()== ChessPiece.PieceType.KING;

        ArrayList<ChessMove> blackMoves = new ArrayList<ChessMove>();
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(board[i][j]!=null && board[i][j].getTeamColor()== ChessGame.TeamColor.BLACK){
                    blackMoves.addAll(board[i][j].pieceMoves(this, new ChessPosition(i+1, j+1)));
                }
            }
        }
        ChessPosition[] noCheckZone = {new ChessPosition(1, 3), new ChessPosition(1, 4), new ChessPosition(1, 5)};
        for(ChessPosition z : noCheckZone){
            for(ChessMove m : blackMoves){
                if(m.getEndPosition().equals(z)){
                    return false;
                }
            }
        }
        return temp;
    }
    private boolean canWhiteShort(){ //not finished, needs to check for checks
        ChessPiece rook = board[0][7];
        ChessPiece king = board[0][4];
        boolean temp = whiteShort && rook!=null&&rook.getTeamColor()== ChessGame.TeamColor.WHITE&&rook.getPieceType()== ChessPiece.PieceType.ROOK
                && board[0][5]==null&&board[0][6]==null
                && king!=null && king.getTeamColor()== ChessGame.TeamColor.WHITE&&king.getPieceType()== ChessPiece.PieceType.KING;

        ArrayList<ChessMove> blackMoves = new ArrayList<ChessMove>();
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(board[i][j]!=null && board[i][j].getTeamColor()== ChessGame.TeamColor.BLACK){
                    blackMoves.addAll(board[i][j].pieceMoves(this, new ChessPosition(i+1, j+1)));
                }
            }
        }
        ChessPosition[] noCheckZone = {new ChessPosition(1, 5), new ChessPosition(1, 6), new ChessPosition(1, 7)};
        for(ChessPosition z : noCheckZone){
            for(ChessMove m : blackMoves){
                if(m.getEndPosition().equals(z)){
                    return false;
                }
            }
        }
        return temp;
    }

    public void makeMove(ChessMove move){
        ChessPiece temp = getPiece(move.getStartPosition());
        //check for promo piece
        if(move.getPromotionPiece()!=null){
            temp = new ChessPiece(temp.getTeamColor(), move.getPromotionPiece());
        }
        //make without castling first
        board[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()-1] = temp;
        board[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-1] = null;

        //set castling booleans
        if(temp.getPieceType()== ChessPiece.PieceType.KING){
            if(temp.getTeamColor()== ChessGame.TeamColor.WHITE){
               whiteLong = false;
               whiteShort = false;
            } else {
                blackLong = false;
                blackShort = false;
            }
        }
        if(move.getStartPosition().equals(new ChessPosition(1, 1))) //white rook, long
            whiteLong = false;
        if(move.getStartPosition().equals(new ChessPosition(1, 8))) //white rook, long
            whiteShort = false;
        if(move.getStartPosition().equals(new ChessPosition(8, 1))) //white rook, long
            blackLong = false;
        if(move.getStartPosition().equals(new ChessPosition(8, 8))) //white rook, long
            blackShort = false;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        //using index of 0-7, and backwards chess notation
        for(int i=0; i<=7; i++){
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        board[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        whiteShort = true;
        whiteLong = true;
        blackShort = true;
        blackLong = true;

        lastMove = null;
    }

    

    public void setBoard(ChessPiece[][] board) {
        this.board = board;
    }

    public ChessPiece[][] getBoard(){
        return board;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String temp = "";
        for(int i=7; i>=0; i--){
            temp += '|';
            for(int j=0; j<8; j++){
                String p = " ";
                if(board[i][j]!=null)
                    p = board[i][j].toString();
                temp += p + '|';
            }
            temp+="\n";
        }
        return temp;
    }
}
