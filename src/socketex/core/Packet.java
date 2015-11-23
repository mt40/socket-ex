package socketex.core;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by mt on 11/9/2015.
 */
public class Packet {
    String event;
    String type = "Packet";
    public String message;
    public PacketStatus status = PacketStatus.OK;
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

    public Packet(String message, PacketStatus status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String toString() {
        // return a json string
        return new Gson().toJson(this);
    }

    // return Packet from Json string
    public static Packet fromString(String json) {
        Packet p = new Gson().fromJson(json, Packet.class);
        if(p.type.equals("AckPacket"))
            return fromString(json, AckPacket.class);
        else if(p.type.equals("MessagePacket"))
            return fromString(json, MessagePacket.class);
        return p;
    }

    public static Packet fromString(String json, Type type) {
        return new Gson().fromJson(json, type);
    }

    public static boolean tryParseJson(String json, Type type) {
        try {
            Packet p = new Gson().fromJson(json, type);
            if(p.getClass() == type)
                return true;
            return false;
        }
        catch (JsonParseException | ClassCastException e) {
            return false;
        }
    }
}
