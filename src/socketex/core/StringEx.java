package socketex.core;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
        return s;
//        MessageDigest md5 = null;
//        try {
//            md5 = MessageDigest.getInstance("MD5");
//            return (new HexBinaryAdapter()).marshal(md5.digest(s.getBytes()));
//        }
//        catch (NoSuchAlgorithmException e) {
//            return s;
//        }
    }

    public static String joins(Object[] o) {
        List<String> strings = new ArrayList<String>();
        for (Object x : o) if (x != null) strings.add(x.toString());
        return String.join(",", strings);
    }
}
