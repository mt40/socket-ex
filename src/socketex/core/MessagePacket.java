package socketex.core;

/**
 * Created by mt on 11/18/2015.
 */
public class MessagePacket extends Packet {
    public RoomInfo room;

    public MessagePacket(String message, RoomInfo room) {
        super(message);
        this.room = room;
    }
}
