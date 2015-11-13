/**
 * Created by mt on 11/10/2015.
 */
package socketex.core;

public class HostName {
    String ip;
    int port;

    public HostName(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HostName h = (HostName)obj;
        return ip.equals(h.ip) && port == h.port;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}
