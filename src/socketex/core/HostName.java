/**
 * Created by mt on 11/10/2015.
 */
package socketex.core;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class HostName {
    String ip;
    int port;

    public HostName(String ip, int port) {
        if(ip.equals("127.0.0.1") || ip.equals("localhost")) {
            try {
                ip = Inet4Address.getLocalHost().getHostAddress(); // try to get the real IP
                console.info("Your ip: " + ip);
            }
            catch (UnknownHostException e) {
            }
        }
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
    public int hashCode() {
        return ip.hashCode() ^ port;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}
