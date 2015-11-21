package socketex.core;

import java.io.IOException;

/**
 * Created by mt on 11/21/2015.
 */
public class TesterClient {
    public static void main(final String []args) throws IOException, InterruptedException {
        SocketEx socket = new SocketEx("127.0.0.1", 2016);
        socket.connect("127.0.0.1", 2015);

        socket.on("welcome", (sender, req) -> {
            console.log(req);
        });
    }
}
