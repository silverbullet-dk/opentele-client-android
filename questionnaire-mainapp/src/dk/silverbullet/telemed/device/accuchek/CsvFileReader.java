package dk.silverbullet.telemed.device.accuchek;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CsvFileReader {
    private static final String FIRST_HEADER_LINE = "\uFEFFSerienummer;Overføringsdato;Overføringstidspunkt;;;;;;;";
    private static final String START_OF_SECOND_HEADER_LINE_1 = "Dato;Tid;Måleresultat;Måleenhed;Temperaturadvarsel;Uden for målområde;Øvrigt;Før måltid;Efter måltid;Kontrolmåling";
    private static final String START_OF_SECOND_HEADER_LINE_2 = "Dato;Klokkeslæt;Måleresultat;Måleenhed;Temperaturadvarsel;Uden for målområde;Øvrigt;Før måltid;Efter måltid;Kontrolmåling";
    // SimpleDateFormat is not thread-safe, so we need to guard it with a ThreadLocal
    private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd.MM.yyyy HH:mm");
        }
    };

    public static BloodSugarMeasurements readFile(File file) throws IOException {
        List<String> lines = readFileAsLines(file);
        checkNumberOfLines(lines);

        BloodSugarMeasurements result = new BloodSugarMeasurements();
        readMetadata(result, lines);
        readMeasurements(result, lines);
        return result;
    }

    private static void readMetadata(BloodSugarMeasurements measurements, List<String> lines) throws IOException {
        checkMetadataHeadline(lines);

        String[] components = lines.get(1).split(";");
        checkNumberOfMetadataColumns(components);
        measurements.serialNumber = components[0];
        measurements.transferTime = parseDate(components[1], components[2]);
    }

    private static void readMeasurements(BloodSugarMeasurements measurements, List<String> lines) throws IOException {
        checkMeasurementsHeadline(lines);

        for (int i = 3; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) {
                continue;
            }
            measurements.measurements.add(parseLine(line));
        }
    }

    private static BloodSugarMeasurement parseLine(String line) throws IOException {
        String[] components = line.split(";");
        checkNumberOfMeasurementColumns(components);

        BloodSugarMeasurement result = new BloodSugarMeasurement();
        result.timeOfMeasurement = parseDate(components[0], components[1]);
        result.result = parseDouble(components[2]);
        checkUnits(components[3]);
        result.hasTemperatureWarning = parseCheckmark(components[4]);
        result.isOutOfBounds = parseCheckmark(components[5]);
        result.otherInformation = parseCheckmark(components[6]);
        result.isBeforeMeal = parseCheckmark(components[7]);
        result.isAfterMeal = parseCheckmark(components[8]);
        result.isControlMeasurement = parseCheckmark(components[9]);

        return result;
    }

    private static double parseDouble(String d) throws IOException {
        try {
            return Double.parseDouble(d);
        } catch (NumberFormatException e) {
            throw new CsvFormatException("Could not parse double: '" + d + "'", e);
        }
    }

    private static Date parseDate(String date, String timeOfDay) throws IOException {
        try {
            return DATE_FORMAT.get().parse(date + " " + timeOfDay);
        } catch (ParseException exception) {
            throw new CsvFormatException("Could not parse date '" + date + "' and time of day '" + timeOfDay + "'",
                    exception);
        }
    }

    private static boolean parseCheckmark(String s) {
        return s.equals("X");
    }

    private static void checkNumberOfLines(List<String> lines) throws IOException {
        if (lines.size() < 3) {
            throw new CsvFormatException("Too few lines in file: " + lines.size());
        }
    }

    private static void checkMetadataHeadline(List<String> lines) throws IOException {
        String firstHeadline = lines.get(0);
        if (!firstHeadline.equals(FIRST_HEADER_LINE)) {
            throw new CsvFormatException("Wrong first headline: '" + firstHeadline + "'");
        }
    }

    private static void checkMeasurementsHeadline(List<String> lines) throws IOException {
        String secondHeadline = lines.get(2);
        if (!secondHeadline.startsWith(START_OF_SECOND_HEADER_LINE_1) && !secondHeadline.startsWith(START_OF_SECOND_HEADER_LINE_2)) {
            throw new CsvFormatException("Wrong second headline: '" + secondHeadline + "'");
        }
    }

    private static void checkNumberOfMetadataColumns(String[] columns) throws IOException {
        if (columns.length != 3) {
            throw new CsvFormatException("Wrong number of metadata columns: " + columns.length);
        }
    }

    private static void checkNumberOfMeasurementColumns(String[] columns) throws IOException {
        if (columns.length != 10) {
            throw new CsvFormatException("Wrong number of measurement columns: " + columns.length);
        }
    }

    private static void checkUnits(String units) throws IOException {
        if (!units.equals("mmol/l")) {
            throw new CsvFormatException("Wrong units: '" + units + "'");
        }
    }

    private static List<String> readFileAsLines(File file) throws IOException {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charset.forName("utf-8"));
        try {
            BufferedReader bufferedReader = new BufferedReader(reader);
            try {
                List<String> result = new ArrayList<String>();
                for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                    result.add(line);
                }
                return result;
            } finally {
                bufferedReader.close();
            }
        } finally {
            reader.close();
        }
    }
}
