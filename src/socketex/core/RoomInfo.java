package socketex.core;

import java.util.List;

/**
 * Created by mt on 11/18/2015.
 */
public class RoomInfo {
    public String channel;
    public List<String> members; // username of member in this room

    public RoomInfo(String channel, List<String> members) {
        this.channel = channel;
        this.members = members;
    }
}
