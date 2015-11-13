package socketex.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by mt on 11/10/2015.
 */
public class Host extends Thread {
    HostName name;
    List<HostName> knownHost = new ArrayList<>();
    Queue<HostName> unreachableHost = new LinkedList<>();
    boolean stopListening = false, stopWaiting = true;
    Thread listenThread;
    PacketReceiveHandler packetHandler = null; // someone who will handle the incoming packet
    Timer cleanupTimer = new Timer();

    public Host(String ip, int port) {
        this.name = new HostName(ip, port);

        start(); // start thread
        console.log("Waiting for thread...");
        while(!stopWaiting) {console.logf("");} // wait for the listening thread to actually run.

        /* Run cleanup periodically */
        int delay = 10 * 1000; // 10 sec
        this.cleanupTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() { cleanup(); }
        }, delay, delay);
    }

    public boolean connect(String dest_ip, int dest_port) throws IOException, InterruptedException {
        /**
         * Calling this function means the caller wants to be
         * the client. So we stop listening to incoming event from server
         * until the handshake is completed
         */
        console.log("connecting");
        this.stopListening();

        /* Begin the handshake */
        HostName dest = new HostName(dest_ip, dest_port);
        console.log("ack 0");
        Packet result = ack(dest, 0);
        if (result.status == PacketStatus.OK) {
            this.knownHost.add(dest);

            /* our part in the 3-way handshake completes, start listening again */
            this.startListening();
            console.log("ack 1");
            /* receive Ack from Server, send back the 2nd Ack */
            ack(dest, 1);
        }
        console.log("connected");
        return true;
    }

    private Packet ack(HostName dest, int sequence) throws IOException {
        AckPacket p = new AckPacket(this.name, dest, sequence);
        return sendMessage(dest, p);
    }

    private Packet ack(HostName dest, AckType type, int sequence) throws IOException {
        AckPacket p = new AckPacket(this.name, dest, sequence);
        if(type == AckType.Disconnect)
            p = new AckPacket(this.name, dest, "disconnected", sequence);
        return sendMessage(dest, p);
    }

    // Notify all knownHosts that I will disappear
    public void disconnect() throws IOException, InterruptedException {
        console.log("disconnect...");
        for(HostName h : knownHost) {
            ack(h, AckType.Disconnect, 0);
        }
        this.cleanupTimer.cancel();
        stopListening();
    }

    public void emit(String event, HostName receiver, Packet content) throws IOException {
        /* Add more info into the packet */
        content.sender = this.name;
        content.receiver = receiver;
        for (HostName dest : this.knownHost) {
            if(receiver != null && !dest.equals(receiver)) continue; // not send to u
            content.event = event;
            sendMessage(dest, content);
        }
    }

    public void broadcast(String event, Packet content) {
        content.sender = this.name;
        int count = 0;
        for (HostName dest : this.knownHost) {
            content.event = event;
            try {
                sendMessage(dest, content);
            }
            catch (IOException e) {
                count--;
                console.log("Broadcast: cannot send but will ignore");// ignore error and continue sending the message
            }
            count++;
        }
        console.logf("Sent to %d hosts\n", count);
    }

    private Packet sendMessage(HostName dest, Packet packet) throws IOException {
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(dest.ip, dest.port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));

            out.println(packet);
            String req = in.readLine();
            return Packet.fromString(req);
        }
        catch (IOException e) {
            console.log(e.getMessage());
            this.unreachableHost.add(dest);
            throw e;
        }
        finally {
            if (out != null) out.close();
            if (in != null) in.close();
            if (echoSocket != null) echoSocket.close();
        }
    }

    // Listen on incoming connection
    private void listen() throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(this.name.port);
        }
        catch (IOException e) {
            System.err.println("Could not listen on port: " + this.name.port);
            System.exit(1);
        }

        while (!this.stopListening) {
            Socket clientSocket = null;
            //System.out.println ("Waiting for connection.....");

            try {
                clientSocket = serverSocket.accept();
            }
            catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

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
                    this.knownHost.add(new HostName(ackPacket.sender.ip, ackPacket.sender.port));
            }
            /* Disconnection packet detected */
            else if (Packet.fromString(input).event.equals("disconnected")) {
                AckPacket ackPacket = (AckPacket) Packet.fromString(input, AckPacket.class);
                this.knownHost.remove(ackPacket.sender);
            }

            /* Call event subscribers */
            if(this.packetHandler != null)
                this.packetHandler.packetReceived(input, Packet.fromString(input));

            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private void stopListening() throws InterruptedException {
        this.stopListening = true;
        this.listenThread.join(); // wait for this thread to finish
    }

    private void startListening() {
        this.stopListening = false;
        this.listenThread = new Thread(() -> {
            try {
                this.listen();
            }
            catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
        this.listenThread.start();
    }

    private void cleanup() {
        int count = 0;
        while(this.unreachableHost.size() > 0) {
            HostName h = this.unreachableHost.poll();
            this.knownHost.remove(h); // don't care if exist or not
            count++;
        }
        console.logf("Cleaned up %d hosts \n", count);
    }

    @Override
    public void run() {
        startListening();
        this.stopWaiting = true;
        console.log("Thread is running");
    }

    @Override
    public synchronized void start() {
        stopWaiting = false;
        super.start();
    }
}
