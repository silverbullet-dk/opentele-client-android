package dk.silverbullet.telemed.utils;

public class VersionMatcher {
    private static final int VERSION = 0;
    private static final int MAJOR = 1;
    private static final int MINOR = 2;
    private static final String VERSION_PATTERN = "(\\d)+(\\.)(\\d)+(\\.)(\\d)+";

    public static boolean isClientVersionSupported(String clientVersion, String minumumRequiredVersion) {
        if (clientVersion == null || minumumRequiredVersion == null) {
            return false;
        }

        if (clientVersion.equals("${version}")) {
            return true;
        }

        if (!clientVersion.matches(VERSION_PATTERN) || !minumumRequiredVersion.matches(VERSION_PATTERN)) {
            return false;
        }

        int[] clientVersionInts = parseVersionNumber(clientVersion);
        int[] minumumRequiredVersionInts = parseVersionNumber(minumumRequiredVersion);

        if (minumumRequiredVersionInts[VERSION] > clientVersionInts[VERSION]) {
            return false;
        } else if (minumumRequiredVersionInts[VERSION] < clientVersionInts[VERSION]) {
            return true;
        } else {
            if (minumumRequiredVersionInts[MAJOR] > clientVersionInts[MAJOR]) {
                return false;
            } else if (minumumRequiredVersionInts[MAJOR] < clientVersionInts[MAJOR]) {
                return true;
            } else {
                if (minumumRequiredVersionInts[MINOR] > clientVersionInts[MINOR]) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    private static int[] parseVersionNumber(String versionString) {
        int[] version = new int[3];
        String[] versionTokens = versionString.split("\\.");

        version[VERSION] = Integer.parseInt(versionTokens[VERSION]);
        version[MAJOR] = Integer.parseInt(versionTokens[MAJOR]);
        version[MINOR] = Integer.parseInt(versionTokens[MINOR]);

        return version;
    }
}
