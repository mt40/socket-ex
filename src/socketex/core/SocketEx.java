package socketex.core;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by mt on 11/8/2015.
 */
public class SocketEx implements PacketReceiveHandler, SocketMonitor {

    Host localhost;
    private List<EventSubscriber> eventSubscribers = new CopyOnWriteArrayList<>();

    public static void main(final String[] args) {
        console.info("Welcome to SocketEx.");
    }

    public SocketEx(String ip, int port) {
        this(ip, port, false);
    }

    public SocketEx(String ip, int port, boolean isServer) {
        localhost = new Host(ip, port);
        localhost.packetHandler = this; // I will handle incoming packet
        localhost.isServer = isServer;

        console.info("Your addr: " + localhost.name);
    }

    /**
     * Bring packets to the right receivers with the right event
     * @param res
     * @throws IOException
     */
    private void distributeResponse(String res) throws IOException {
        Packet p = Packet.fromString(res);
        for (EventSubscriber lst : this.eventSubscribers) {
            /* make sure we packet is deliverd to the right people */
            if(p.receiver != null && !lst.name.equals(p.receiver))
                continue;

            if (lst.event.equals(p.event)) {
                if (p.event.equals("connected")) {

                    AckPacket ackPacket = (AckPacket) Packet.fromString(res);
                    if(ackPacket.sequence == 0)
                        continue; // we are in the middle of the handshake so don't emit any event
                }

                lst.handler.run(p.sender, p);
            }
        }
    }

    public void on(String event, SocketExEventHandler handler) throws IOException {
        eventSubscribers.add(new EventSubscriber(this.localhost.name, event, handler));
    }

    @Override
    public void packetReceived(String rawJson, Packet packet) throws IOException {
        distributeResponse(rawJson);
    }

    @Override
    public boolean connect(String dest_ip, int dest_port) throws IOException, InterruptedException {
        return localhost.connect(dest_ip, dest_port);
    }

    @Override
    public void disconnect(String username) throws IOException, InterruptedException {
        localhost.disconnect(username);
    }

    @Override
    public void emit(String event, HostName receiver, Packet packet) throws IOException {
        assert(packet != null);
        localhost.emit(event, receiver, packet);
    }

    @Override
    public void broadcast(String event, Packet packet) throws IOException {
        assert(packet != null);
        localhost.broadcast(event, packet);
    }
}
