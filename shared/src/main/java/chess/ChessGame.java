package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor turn;
    ChessBoard board;
    public ChessGame() {
        turn = null;
        board = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(board.getPiece(startPosition)==null){
            return null;
        }
        return removeCheckMoves(board.getMoves(startPosition), board.getPiece(startPosition).getTeamColor());
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //check that its even a valid move
        ArrayList<ChessMove> moves = getTeamMoves(turn, false);

        if(!moves.contains(move))
            throw new InvalidMoveException("Invalid move!");
        else {
            board.makeMove(move);
            turn = getOppositeTeam(turn);
        }
    }



    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    private ChessPosition findKing(TeamColor color){
        for(int i=1; i<=8; i++){
            for(int j=1; j<=8; j++){
                ChessPosition test = new ChessPosition(i, j);
                ChessPiece p = board.getPiece(test);
                if(p!=null && p.getTeamColor()==color && p.getPieceType()== ChessPiece.PieceType.KING)
                    return test;
            }
        }
        return null;
    }

    private TeamColor getOppositeTeam(TeamColor color){
        if(color==TeamColor.WHITE)
            return TeamColor.BLACK;
        else
            return TeamColor.WHITE;
    }
    public boolean isInCheck(TeamColor teamColor) {
        ArrayList<ChessMove> moves = getTeamMoves(getOppositeTeam(teamColor), true);
        ChessPosition kingPos = findKing(teamColor);
        if(kingPos==null){
            return false;
        } else {
            for(ChessMove m : moves){
                if(m.getEndPosition().getRow()==kingPos.getRow() && m.getEndPosition().getColumn()==kingPos.getColumn())
                    return true;
            }
            return false;
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return getTeamMoves(teamColor, false).isEmpty() && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return getTeamMoves(teamColor, false).isEmpty() && !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    private ArrayList<ChessMove> getTeamMoves(TeamColor color, boolean includeChecks){ //need to factor checks FIXME
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        for(int i=1; i<=8; i++){
            for(int j=1; j<=8; j++){
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if(piece!=null){
                    if(piece.getTeamColor()==color){
                        moves.addAll(piece.pieceMoves(board, new ChessPosition(i, j)));
                    }
                }
            }
        }
        if(includeChecks)
            return moves;
        else
            return removeCheckMoves(moves, color);
    }

    private ArrayList<ChessMove> removeCheckMoves(ArrayList<ChessMove> moves, TeamColor color){
        //filter out moves that put king in check
        ArrayList<ChessMove> uncheckedMoves = new ArrayList<ChessMove>();
        for(ChessMove m : moves){
            ChessPiece[][] save = new ChessPiece[8][8];
            for(int i=1; i<=8; i++){
                for(int j=1; j<=8; j++){
                    save[i-1][j-1] = board.getPiece(new ChessPosition(i, j));
                }
            }

            board.makeMove(m);
            if(!isInCheck(color))
                uncheckedMoves.add(m);
            board.setBoard(save);
        }
        return uncheckedMoves;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.deepEquals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
