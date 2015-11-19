package socketex.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by mt on 11/10/2015.
 */
public class Host extends Thread {
    HostName name;
    Thread listenThread;
    PacketReceiveHandler packetHandler = null; // someone who will handle the incoming packet
    Timer cleanupTimer = new Timer();

    List<HostName> knownHost = new ArrayList(); // thread-safe list
    Queue<HostName> unreachableHost = new LinkedList<>();

    final int cleanupDelay = 10000; // 10 sec
    boolean stopListening = false;
    boolean stopWaiting = true;

    public Host(String ip, int port) {
        this.name = new HostName(ip, port);

        start(); // start thread
        console.log("Waiting for thread...");
        while(!stopWaiting) {console.logf("");} // wait for the listening thread to actually run.

        /* Run cleanup periodically */
        this.cleanupTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() { cleanup(); }
        }, cleanupDelay, cleanupDelay);
    }

    //<editor-fold desc="Code for server only">
    private void cleanup() {
        int count = 0;
        while(this.unreachableHost.size() > 0) {
            HostName h = this.unreachableHost.poll();
            this.knownHost.remove(h); // don't care if exist or not
            count++;
        }
        console.logf("[ Cleaned up %d hosts ]\n", count);
    }
    //</editor-fold>

    //<editor-fold desc="Code for client only">
    public boolean connect(String dest_ip, int dest_port) throws IOException, InterruptedException {
        /**
         * Calling this function means the caller wants to be
         * the client. So we stop listening to incoming event from server
         * until the handshake is completed
         */
        console.log("connecting");
        this.stopListening();
        console.log("begin!");

        /* Begin the handshake */
        HostName dest = new HostName(dest_ip, dest_port);
        console.log("ack 0");
        Packet result = ack(dest, 0);
        if (result.status == PacketStatus.OK) {
            this.addKnownHost(dest);

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
    //</editor-fold>

    //<editor-fold desc="Common code for both server and client">
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
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(dest.ip, dest.port);
            socket.setSoTimeout(3000); // timeout for read operation
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            out.println(packet);
            String req = in.readLine(); // timeout for this
            return Packet.fromString(req);
        }
        catch (SocketTimeoutException e) {
            console.log("Emit timeout on read, no return packet");
            return null;
        }
        catch (IOException e) {
            console.log(e.getMessage());
            this.unreachableHost.add(dest);
            throw e;
        }
        finally {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
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
        serverSocket.setSoTimeout(3000); // timeout after 3 seconds

        while (!this.stopListening) {
            Socket clientSocket = null;
            //System.out.println ("Waiting for connection.....");

            try {
                clientSocket = serverSocket.accept();
            }
            catch (SocketTimeoutException e) {
                // socket timeout because there is no incomming connection -> retry
                continue;
            }
            catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            // connection succeed, open a new thread to read data
            new MyThread(clientSocket, this);

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

    public void addKnownHost(HostName hostName) {
        this.knownHost.add(hostName);
        removeDuplicateHost();
    }

    private void removeDuplicateHost() {
        int old = knownHost.size();
        Set<HostName> set = new HashSet<>();
        for (HostName host : knownHost) {
            set.add(host);
        }
        this.knownHost.clear();
        this.knownHost.addAll(new ArrayList<>(set));
        if(old != knownHost.size())
            console.logf("Remove %d duplicate hosts\n", old - knownHost.size());
    }
    //</editor-fold>

    @Override
    public void run() {
        startListening();
        this.stopWaiting = true; // wait for the thread to actually run
    }

    @Override
    public synchronized void start() {
        this.stopWaiting = false;
        super.start();
    }
}
