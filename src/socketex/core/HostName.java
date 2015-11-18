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

    public static HostName valueOf(String s) {
        if(StringEx.isNullOrWhiteSpace(s))
            throw new IllegalArgumentException("String is not HostName");
        String []parts = s.split(":");
        if(parts.length != 2)
            throw new IllegalArgumentException("String is not HostName");
        return new HostName(parts[0], Integer.valueOf(parts[1]));
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
