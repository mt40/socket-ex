package socketex.core;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            return s;
        }
    }

    public static String joins(Object[] o) {
        List<String> strings = new ArrayList<String>();
        for (Object x : o) if (x != null) strings.add(x.toString());
        return String.join(",", strings);
    }
}
