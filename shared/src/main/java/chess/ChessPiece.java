package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor color;
    PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //Note to TAs: this switch format is super cool, it automatically adjusted it in IntelliJ
        return switch (board.getPiece(myPosition).getPieceType()) {
            case BISHOP -> getBishopMoves(board, myPosition);
            case ROOK -> getRookMoves(board, myPosition);
            case QUEEN -> getQueenMoves(board, myPosition);
            default -> null;
        };

    }

    private int isValidMove(ChessPosition pos, ChessBoard board){ // -1 is out of bounds OR landing on same team, 0 is clear, 1 is capture
        if(pos.getRow() > 8 || pos.getRow() < 1 || pos.getColumn() >8 || pos.getColumn() < 1)
            return -1;
        ChessPiece land = board.getPiece(pos);
        if(land == null)
            return 0;
        if(land.getTeamColor()==color)
            return -1;
        else
            return 1;
    }

    private ArrayList<ChessMove> getQueenMoves(ChessBoard board, ChessPosition pos){
        ArrayList<ChessMove> moves = getBishopMoves(board, pos);
        moves.addAll(getRookMoves(board, pos));
        return moves;
    }

    private ArrayList<ChessMove> getBishopMoves(ChessBoard board, ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        for(int i=0; i<4; i++){
            int x_inc = i%2*2-1; //-1, 1, -1, 1
            int y_inc = i/2*2-1; //-1, -1, 1, 1
            for(int j=1; j<=8; j++){
                ChessPosition test = new ChessPosition(pos.getRow()+x_inc*j, pos.getColumn()+y_inc*j);
                int valid = isValidMove(test, board);
                if(valid == -1)
                    break;
                else {
                    moves.add(new ChessMove(pos, test, null)); //add move

                    if(valid == 1)
                        break;
                }
            }
        }
        return moves;
    }

    private ArrayList<ChessMove> getRookMoves(ChessBoard board, ChessPosition pos){
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        for(int i=0; i<4; i++){
            int x_inc = i/2 * (i%2*2-1); //0, 0, -1, 1
            int y_inc = (3-i)/2 * (i%2*2-1); //1, -1, 0, 0
            for(int j=1; j<=8; j++){
                ChessPosition test = new ChessPosition(pos.getRow()+x_inc*j, pos.getColumn()+y_inc*j);
                int valid = isValidMove(test, board);
                if(valid == -1)
                    break;
                else {
                    moves.add(new ChessMove(pos, test, null)); //add move

                    if(valid == 1)
                        break;
                }
            }
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
