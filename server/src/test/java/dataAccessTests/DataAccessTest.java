package dataAccessTests;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import dataAccess.DataAccess;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

class DataAccessTest {

    @Test
    public void clearServiceTest(){
        String token = DataAccess.addUser("xyz", "abc", "hello@yahoo.com");
        Assertions.assertTrue(DataAccess.isAuthValid(token));
        DataAccess.clearAll();
        Assertions.assertFalse(DataAccess.isAuthValid(token));

    }

    @Test
    public void registerTestA(){ //check that it can register a new user
        DataAccess.clearAll();
        String token = DataAccess.addUser("nathan","123", "john@gmail.com");
        Assertions.assertNotNull(token);;
    }

    @Test
    public void registerTestB(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("nathan","123", "john@gmail.com");

        //register duplicate user
        token = DataAccess.addUser("nathan","123", "john@gmail.com");

        Assertions.assertNull(token);
    }

    @Test
    public void loginTestA(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("nathan","123", "john@gmail.com");
        DataAccess.logout(token);
        Assertions.assertNotEquals(token, DataAccess.login("nathan"));
    }


    @Test
    public void isAuthValidTestA(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");

        Assertions.assertTrue(DataAccess.isAuthValid(token));
    }

    @Test
    public void isAuthValidTestB(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");

        Assertions.assertFalse(DataAccess.isAuthValid("fake token"));
    }

    @Test
    public void createGameTestA(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        Assertions.assertEquals(4, ("" + (DataAccess.createGame("duck"))).length());
    }

    @Test
    public void createGameTestB(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        DataAccess.createGame("duck");
        Assertions.assertEquals(-1, DataAccess.createGame("duck"));
    }

    @Test
    public void getGamesTestA(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        int id = DataAccess.createGame("duck");
        Assertions.assertEquals(id, DataAccess.getGames().get(0).getGameID());
    }

    @Test
    public void getGamesTestB(){ //check that it returns nothing
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        Assertions.assertEquals(0, DataAccess.getGames().size());
    }

    @Test
    public void getGamesTestC(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        int id = DataAccess.createGame("duck");
        DataAccess.createGame("goose");
        Assertions.assertEquals(2, DataAccess.getGames().size());
    }

    @Test
    public void getGamesTestD(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        int id = DataAccess.createGame("duck");
        int id2 = DataAccess.createGame("goose");
        Assertions.assertEquals(id2, DataAccess.getGames().get(1).getGameID());
    }

    @Test
    public void logoutTestA(){
        DataAccess.clearAll();
        String token = DataAccess.addUser("John","123", "john@gmail.com");
        DataAccess.logout(token);
        Assertions.assertFalse(DataAccess.isAuthValid(token));
    }

    @Test
    public void logoutTestB(){
        DataAccess.clearAll();
        String tokenA = DataAccess.addUser("John","123", "john@gmail.com");
        String tokenB = DataAccess.addUser("Nathan", "123", "nathan@gmail.com");
        DataAccess.logout(tokenB);
        Assertions.assertTrue(DataAccess.isAuthValid(tokenA));
        Assertions.assertFalse(DataAccess.isAuthValid(tokenB));
    }

    @Test
    public void findUserTestA(){
        DataAccess.clearAll();
        String tokenA = DataAccess.addUser("John","123", "john@gmail.com");
        Assertions.assertEquals(DataAccess.findUser(tokenA), "John");
    }

    @Test
    public void findUserTestB(){
        DataAccess.clearAll();
        String tokenA = DataAccess.addUser("John","123", "john@gmail.com");
        Assertions.assertNotEquals(DataAccess.findUser("fake token"), "John");
    }

    @Test
    public void updateGameTestA(){
        DataAccess.clearAll();
        String tokenA = DataAccess.addUser("John","123", "john@gmail.com");
        int gameID = DataAccess.createGame("duck");
        GameData gd = DataAccess.getGame(gameID);
        ChessGame game = gd.getGame();
        game.resetBoard();
        try{
        game.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        }
        catch(Exception e){}
        gd.setGame(game);
        DataAccess.updateGame(gameID, gd);

        Assertions.assertEquals(DataAccess.getGame(gameID).getGame().getTurn(), ChessGame.TeamColor.BLACK);
    }

    @Test
    public void updateGameTestB(){
        DataAccess.clearAll();
        String tokenA = DataAccess.addUser("John","123", "john@gmail.com");
        int gameID = DataAccess.createGame("duck");
        GameData gd = DataAccess.getGame(gameID);
        ChessGame game = gd.getGame();
        game.resetBoard();
        try{
            game.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        }
        catch(Exception e){}
        gd.setGame(game);
        DataAccess.updateGame(gameID, gd);

        Assertions.assertEquals(DataAccess.getGame(gameID).getGame().getBoard().getPiece(new ChessPosition(3, 1)).getPieceType(),
                ChessPiece.PieceType.PAWN);
    }

}