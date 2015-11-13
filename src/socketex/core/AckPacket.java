package socketex.core;

/**
 * Created by mt on 11/9/2015.
 */
class AckPacket extends Packet {
    int sequence;

    public AckPacket() {}

    public AckPacket(HostName sender, HostName receiver, int sequence) {
        super(sender, receiver, "connected", null);
        this.sequence = sequence;
    }

    public AckPacket(HostName sender, HostName receiver, String event, int sequence) {
        super(sender, receiver, event, null);
        this.sequence = sequence;
    }
}
