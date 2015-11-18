package socketex.core;

/**
 * Created by mt on 11/9/2015.
 */
public class console {
    public static void log(Object obj) {
        System.out.println(obj);
    }

    public static void logf(String format, Object...args) {
        System.out.printf(format, args);
    }
}
