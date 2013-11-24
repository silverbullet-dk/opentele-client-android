package dk.silverbullet.telemed.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class DataLogger {
    private static final String LOG_FILE_PREFIX = "CTG-data-";

    private static final String TAG = Util.getTag(DataLogger.class);

    static DataLogger instance;
    static final Object semaphore = new Object();

    OutputStreamWriter out;

    private File file;

    private long start;

    private DataLogger() {
        try {
            Date now = new Date();
            start = 0;
            file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), LOG_FILE_PREFIX
                    + Json.ISO8601_DATE_TIME_FORMAT_SHORT.format(now) + ".CSV");
            cleanupFiles(file.getParentFile(), LOG_FILE_PREFIX, 4);
            Log.d(TAG, "DateLogger output file: " + file.getAbsolutePath());
            out = new OutputStreamWriter(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            out = null;
            e.printStackTrace();
        }
    }

    private static void cleanupFiles(File dir, final String prefix, int retainCount) {
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().startsWith(prefix);
            }
        });

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return (int) Math.signum(lhs.lastModified() - rhs.lastModified());
            }
        });

        for (int i = 0; files.length - i > retainCount; i++) {
            Log.d(TAG, "Deleting file \"" + files[i].getAbsolutePath() + "\"");
            if (!files[i].delete()) {
                Log.w(TAG, "Could not delete file \"" + files[i].getAbsolutePath() + "\"");
            }
        }
    }

    public static void close() {
        synchronized (semaphore) {
            if (instance == null || instance.out == null)
                return;

            try {
                instance.out.close();
            } catch (IOException e) {
                Log.w(TAG, "Closing gave an exception.", e);
            } finally {
                instance.out = null;
                instance = null;
            }
        }
    }

    public static void logInput(Date time, String input) {
        byte[] bytes = new byte[input.length()];
        for (int i = 0; i < input.length(); i++) {
            bytes[i] = (byte) (input.charAt(i));
        }
        getInstance().logData(time, "AN24", bytes);
    }

    private void logData(Date time, String source, byte[] bytes) {
        if (out == null)
            return;
        try {
            if (start == 0) {
                start = time.getTime();
            }
            out.write(Long.toString(time.getTime() - start));
            out.write(", \"");
            out.write(source);
            out.write("\", \"");
            out.write(Util.toHexString(bytes));
            out.write("\"\n");
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error while writing to datalogger output stream!", e);
            out = null;
        }
    }

    public static void logOutput(Date time, byte[] bytes) {
        getInstance().logData(time, "PC", bytes);
    }

    public static DataLogger getInstance() {
        synchronized (semaphore) {
            if (instance == null) {
                instance = new DataLogger();
            }
        }
        return instance;
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
