package ctg;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import dk.silverbullet.telemed.device.monica.packet.CBlockMessage;

// import dk.silverbullet.telemed.device.monica.packet.CBlockMessage;

public class ReadCtgDataLog {

    public final static String TIME = "time";
    public final static String SOURCE = "source";
    public final static String DATA = "data";

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        File input = new File(args[0]);
        System.out.println("Reading from: " + input.getAbsolutePath());

        CsvPreference preferences = CsvPreference.STANDARD_PREFERENCE;
        CsvMapReader cmr = new CsvMapReader(new FileReader(input), preferences);
        Map<String, String> read = cmr.read(new String[] { TIME, SOURCE, DATA });
        System.out.println("\"time\",\"M\",\"MHR\",\"FHR\",\"TOCO\",\"QFHR\"");
        boolean mark = false;
        while (read != null) {
            Date time = new Date(Long.parseLong(read.get(TIME)));
            byte[] bytes = hex2bytes(read.get(DATA));
            String data = bytes2string(bytes);

            if (data.length() != bytes.length) {
                System.out.println("Length error: String:" + data.length() + " bytes:" + bytes.length);
                return;
            } else {
                for (int i = 0; i < bytes.length; i++) {
                    if (bytes[i] != (byte) data.charAt(i)) {
                        System.out.println("*****************************          byte: " + bytes[i] + " char: "
                                + data.charAt(i));
                        return;
                    }
                }
            }

            if (data.equals("MM")) {
                mark = true;
            } else if (data.startsWith("C")) {
                CBlockMessage cb = new CBlockMessage(time, data);
                float[] mhr = cb.getMHR();
                float[] fhr = cb.getFHR1();
                float[] toco = cb.getTOCO();
                int[] qfhr = cb.getQFHR1();

                for (int i = 0; i < mhr.length; i++) {
                    System.out.print((time.getTime() + 500) / 1000 + ",");
                    if (mark) {
                        System.out.print("10,");
                    } else {
                        System.out.print("0,");
                    }
                    System.out.print(mhr[i] + ",");
                    System.out.print(fhr[i] + ",");
                    System.out.print(toco[i] + ",");
                    System.out.print(qfhr[i] + "");
                    System.out.println();
                    mark = false;
                }
            } else if (data.startsWith("N02ANS")) {

                // System.out.print((time.getTime() + 500) / 1000 + ",");
                // int start = "N02ANS".length();
                // int fh = Integer.parseInt(data.substring(start, start + 4), 16);
                // int n = Integer.parseInt(data.substring(start + 4, start + 8), 16);
                // System.out.println(fh + "," + n);
            } else {
                // StringBuffer sb = new StringBuffer();
                // for (int i = 0; i < data.length(); i++) {
                // char ch = data.charAt(i);
                // if (ch > ' ' && ch < 127)
                // sb.append(ch);
                // else
                // break;
                // }
                // System.out.println(sb + " [" + data.length() + "]");
            }

            read = cmr.read(new String[] { TIME, SOURCE, DATA });
        }

        cmr.close();
    }

    private static String bytes2string(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        for (byte b : bytes) {
            sb.append((char) (b & 0xff));
        }
        return sb.toString();
    }

    private static byte[] hex2bytes(String string) {
        String hex = string.toUpperCase().replaceAll("[. \\-]", "");
        if (hex.length() % 2 != 0)
            throw new IllegalArgumentException("Invalid hex string!");
        int byteCount = hex.length() / 2;
        byte[] bytes = new byte[byteCount];
        String hexDigits = "0123456789ABCDEF";
        for (int i = 0; i < byteCount; i++) {
            int high = hexDigits.indexOf(hex.charAt(i * 2));
            int low = hexDigits.indexOf(hex.charAt(i * 2 + 1));
            if (low < 0 || high < 0)
                throw new IllegalArgumentException("Illegal hex digit!");
            bytes[i] = (byte) (low | (high << 4));
        }
        return bytes;
    }
}
