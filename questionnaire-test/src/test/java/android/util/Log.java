package android.util;

/**
 * We don't want failing unit tests just because some code calls Log.d. We just want peace.
 */
public class Log {
    public static int d(String tag, String message) {
        System.out.println("D: " + tag + ": " + message);
        System.out.flush();
        return 0;
    }

    public static int i(String tag, String message) {
        System.out.println("I: " + tag + ": " + message);
        System.out.flush();
        return 0;
    }

    public static int w(String tag, String message) {
        System.out.println("W: " + tag + ": " + message);
        System.out.flush();
        return 0;
    }

    public static int e(String tag, String message) {
        System.out.println("E: " + tag + ": " + message);
        System.out.flush();
        return 0;
    }

    public static int e(String tag, String message, Throwable throwable) {
        System.out.println("E: " + tag + ": " + message);
        throwable.printStackTrace();
        System.out.flush();
        return 0;
    }

    public static int w(String tag, String message, Throwable throwable) {
        System.out.println("W: " + tag + ": " + message);
        throwable.printStackTrace();
        System.out.flush();
        return 0;
    }
}
