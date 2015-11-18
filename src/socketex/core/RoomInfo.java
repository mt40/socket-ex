package socketex.core;

import java.util.List;

/**
 * Created by mt on 11/18/2015.
 */
public class RoomInfo {
    public String channel;
    public List<HostName> members; // id of member in this room

    public RoomInfo(String channel, List<HostName> members) {
        this.channel = channel;
        this.members = members;
    }
}
