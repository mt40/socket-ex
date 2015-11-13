package socketex.core;

import java.io.IOException;

/**
 * Created by mt on 11/13/2015.
 */
@FunctionalInterface
public interface SocketExEventHandler {
    void run(HostName sender, Packet message) throws IOException;
}
