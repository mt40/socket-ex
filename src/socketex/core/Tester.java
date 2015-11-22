package socketex.core;

import java.io.IOException;

/**
 * Created by mt on 11/18/2015.
 */
public class Tester {
    public static void main(final String []args) throws IOException {
        SocketEx socket = new SocketEx("127.0.0.1", 2015);
        console.logf("Listening on port %d...\n", 2015);

        socket.on("connected", (sender, req) -> {
            console.log(sender + " connected.");
            socket.emit("welcome", sender, new Packet("Welcome to the room!"));
            socket.broadcast("announcement", new Packet("New user"));
        });

        socket.on("broadcast_message", (sender, req) -> {
            console.info("/broadcast requested");

            socket.broadcast("broadcast", req);
        });
    }
}
