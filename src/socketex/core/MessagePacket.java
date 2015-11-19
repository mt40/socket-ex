package socketex.core;

import java.util.List;

/**
 * Created by mt on 11/18/2015.
 */
public class MessagePacket extends Packet {
    public List<String> recipients;
    public String senderName;

    public MessagePacket(String message, List<String> recipients, String senderName) {
        super(message);
        this.recipients = recipients;
        this.senderName = senderName;
    }
}
