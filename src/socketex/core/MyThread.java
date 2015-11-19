package socketex.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyThread extends Thread {
    Socket clientSocket;
    Host parent;

    public MyThread(Socket clientSocket, Host parent) {
        this.clientSocket = clientSocket;
        this.parent = parent;
        start();
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String input = in.readLine();
            //console.log("input: " + input);

            //console.log("response OK");
            /* Response OK to confirm that the packet is received */
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(new Packet()); // empty packet with OK status

            /* 3-way handshake packet detected */
            if (Packet.fromString(input).event.equals("connected")) {
                AckPacket ackPacket = (AckPacket) Packet.fromString(input, AckPacket.class);

                if (ackPacket.sequence == 1) // this means the 3-way handshake is completed
                    parent.addKnownHost(new HostName(ackPacket.sender.ip, ackPacket.sender.port));
            }
            /* Disconnection packet detected */
            else if (Packet.fromString(input).event.equals("disconnected")) {
                AckPacket ackPacket = (AckPacket) Packet.fromString(input, AckPacket.class);
                parent.knownHost.remove(ackPacket.sender);
            }

            /* Call event subscribers */
            if (parent.packetHandler != null)
                parent.packetHandler.packetReceived(input, Packet.fromString(input));

            in.close();
            clientSocket.close();
        }
        catch (IOException e) {
            console.log(e.getMessage());
        }
    }
}
