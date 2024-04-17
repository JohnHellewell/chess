package webSocketMessages.serverMessages;

import model.GameData;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String notification;

    GameData game;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public GameData getGame() {
        return game;
    }

    public void setGame(GameData game) {
        this.game = game;
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getNotification() {
        return notification;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerMessage))
            return false;
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
