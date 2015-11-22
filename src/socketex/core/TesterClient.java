package socketex.core;

import java.io.IOException;

/**
 * Created by mt on 11/21/2015.
 */
public class TesterClient {
    public static void main(final String []args) throws IOException, InterruptedException {
        HostName sv = new HostName("127.0.0.1", 2015);
        SocketEx socket = new SocketEx("127.0.0.1", 2016);
        socket.connect(sv.ip, sv.port);

        socket.on("welcome", (sender, req) -> {
            console.log(req);

        });

        socket.on("announcement", (sender, req) -> {
            console.log(req);
            socket.emit("broadcast_message", sv, new Packet("pls!"));
        });

        socket.on("broadcast", (sender, req) -> {
            console.log("works");
        });
    }
}
