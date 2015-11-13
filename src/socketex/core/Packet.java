package socketex.core;

import com.google.gson.Gson;
import java.lang.reflect.Type;

/**
 * Created by mt on 11/9/2015.
 */
public class Packet {
    String event;
    String message;
    PacketStatus status = PacketStatus.OK;
    HostName sender, receiver;

    public Packet() {}

    public Packet(String message) {
        this.message = message;
    }

    public Packet(String event, String message) {
        this.event = event;
        this.message = message;
    }

    public Packet(HostName sender, HostName receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public Packet(HostName sender, HostName receiver, String event, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.event = event;
        this.message = message;
    }

    public Packet(PacketStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        // return a json string
        return new Gson().toJson(this);
    }

    // return Packet from Json string
    public static Packet fromString(String json) {
        return new Gson().fromJson(json, Packet.class);
    }

    public static Packet fromString(String json, Type type) {
        return new Gson().fromJson(json, type);
    }
}
