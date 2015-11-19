package socketex.core;

import java.io.IOException;

/**
 * Created by mt on 11/10/2015.
 */
public interface SocketMonitor {
    public boolean connect(String dest_ip, int dest_port) throws IOException, InterruptedException;
    public void disconnect(String username) throws IOException, InterruptedException;
    public void emit(String event, HostName receiver, Packet content) throws IOException;
    public void broadcast(String event, Packet content) throws IOException;
}
