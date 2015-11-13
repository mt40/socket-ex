package socketex.core;

/**
 * Created by mt on 11/13/2015.
 */
public class EventSubscriber {
    HostName name;
    String event;
    SocketExEventHandler handler;

    public EventSubscriber(HostName name, String event, SocketExEventHandler handler) {
        this.name = name;
        this.event = event;
        this.handler = handler;
    }
}
