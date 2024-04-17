package webSocketMessages.userCommands;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    public UserGameCommand(String authToken, int gameID, CommandType command, String player) {
        this.gameID = gameID;
        this.authToken = authToken;
        this.commandType = command;
        this.player = player;
    }

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }



    public int getGameID() {
        return gameID;
    }

    protected CommandType commandType;

    private final String authToken;

    private String player;

    private int gameID;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }
}
