package socketex.core;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mt on 11/18/2015.
 */
public class StringEx {
    public static boolean isNullOrWhiteSpace(String s) {
        if (s == null || s.length() == 0)
            return true;
        for (char c : s.toCharArray())
            if (c != ' ')
                return false;
        return true;
    }

    public static String getMD5(String s) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            return (new HexBinaryAdapter()).marshal(md5.digest(s.getBytes()));
        }
        catch (NoSuchAlgorithmException e) {
            return s;
        }
    }
}
