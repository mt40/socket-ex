package socketex.core;

import java.io.IOException;

@FunctionalInterface
interface PacketReceiveHandler {
    void packetReceived(String rawJson, Packet packet) throws IOException;
}