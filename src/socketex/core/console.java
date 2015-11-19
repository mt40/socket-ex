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

    public static void info(Object obj) {
        log("[Info] " + obj.toString());
    }

    public static void infof(String format, Object...args) {
        logf("[Info] " + format, args);
    }

    public static void error(Object obj) {
        log("[Error] " + obj.toString());
    }

    public static void errorf(String format, Object...args) {
        logf("[Error] " + format, args);
    }

    public static void system(Object obj) {
        log("[System] " + obj.toString());
    }

    public static void systemf(String format, Object...args) {
        logf("[System] " + format, args);
    }
}
