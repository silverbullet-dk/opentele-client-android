package dk.silverbullet.telemed.device.accuchek;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class CsvFileReaderTest {
	File properCsvFile = fileFor("ProperDiary.csv");
    File properCsvFileWithNewColumnHeader = fileFor("ProperDiaryWithNewColumnHeader.csv");
	File csvFileWithTooFewLines = fileFor("DiaryWithTooFewLines.csv");
	File csvFileWithWrongFirstHeadline = fileFor("DiaryWithWrongFirstHeadline.csv");
	File csvFileWithWrongSecondHeadline = fileFor("DiaryWithWrongSecondHeadline.csv");
	File csvFileWithWrongUnits = fileFor("DiaryWithWrongUnits.csv");
	File csvFileWithTooManyColumns = fileFor("DiaryWithTooManyColumns.csv");
	File csvFileWithTooManyMetadataColumns = fileFor("DiaryWithTooManyMetadataColumns.csv");
	File csvFileWithTooFewColumns = fileFor("DiaryWithTooFewColumns.csv");
	File csvFileWithTooFewMetadataColumns = fileFor("DiaryWithTooFewMetadataColumns.csv");

	@Test
	public void readsMetadata() throws Exception {
		BloodSugarMeasurements measurements = CsvFileReader.readFile(properCsvFile);
		
		assertEquals("U100160433", measurements.serialNumber);
		assertEquals(date("22-02-2013 13:26"), measurements.transferTime);
	}
	
	@Test
	public void readsTheCorrectAmountOfMeasurements() throws Exception {
		List<BloodSugarMeasurement> measurements = CsvFileReader.readFile(properCsvFile).measurements;
		
		assertEquals(9, measurements.size());
	}
	
	@Test
	public void readsTimestampsFromLinesInFile() throws Exception {
		List<BloodSugarMeasurement> measurements = CsvFileReader.readFile(properCsvFile).measurements;
		
		assertEquals(date("21-01-2013 09:48"), measurements.get(0).timeOfMeasurement);
		assertEquals(date("21-01-2013 09:40"), measurements.get(1).timeOfMeasurement);
		// ...
		assertEquals(date("17-08-2012 17:13"), measurements.get(8).timeOfMeasurement);
	}
	
	@Test
	public void readsBloodSugarLevelsFromLinesInFile() throws Exception {
		List<BloodSugarMeasurement> measurements = CsvFileReader.readFile(properCsvFile).measurements;
		
		assertEquals(5.2, measurements.get(0).result, 0.0001);
		assertEquals(9.7, measurements.get(1).result, 0.0001);
		// ...
		assertEquals(6.5, measurements.get(8).result, 0.0001);
	}
	
	@Test
	public void readsTemperatureWarnings() throws Exception {
		List<BloodSugarMeasurement> measurements = CsvFileReader.readFile(properCsvFile).measurements;
		
		assertFalse(measurements.get(0).hasTemperatureWarning);
		assertTrue(measurements.get(1).hasTemperatureWarning);
		assertFalse(measurements.get(2).hasTemperatureWarning);
	}
	
	@Test
	public void knowsWhenMeasurementIsOutOfBounds() throws Exception {
		List<BloodSugarMeasurement> measurements = CsvFileReader.readFile(properCsvFile).measurements;
		
		assertFalse(measurements.get(3).isOutOfBounds);
		assertTrue(measurements.get(4).isOutOfBounds);
		assertFalse(measurements.get(5).isOutOfBounds);
	}
	
	@Test
	public void knowsWhenMeasurementHasOtherInformation() throws Exception {
		List<BloodSugarMeasurement> measurements = CsvFileReader.readFile(properCsvFile).measurements;
		
		assertFalse(measurements.get(4).otherInformation);
		assertTrue(measurements.get(5).otherInformation);
		assertFalse(measurements.get(6).otherInformation);
	}
	
	@Test
	public void knowsWhenMeasurementIsBeforeMeal() throws Exception {
		List<BloodSugarMeasurement> measurements = CsvFileReader.readFile(properCsvFile).measurements;
		
		assertFalse(measurements.get(1).isBeforeMeal);
		assertTrue(measurements.get(2).isBeforeMeal);
		assertFalse(measurements.get(3).isBeforeMeal);
	}
	
	@Test
	public void knowsWhenMeasurementIsAfterMeal() throws Exception {
		List<BloodSugarMeasurement> measurements = CsvFileReader.readFile(properCsvFile).measurements;
		
		assertFalse(measurements.get(6).isAfterMeal);
		assertTrue(measurements.get(7).isAfterMeal);
		assertFalse(measurements.get(8).isAfterMeal);
	}
	
	@Test
	public void knowsWhenMeasurementIsAControlMeasurement() throws Exception {
		List<BloodSugarMeasurement> measurements = CsvFileReader.readFile(properCsvFile).measurements;
		
		assertFalse(measurements.get(0).isControlMeasurement);
		assertTrue(measurements.get(1).isControlMeasurement);
		assertFalse(measurements.get(2).isControlMeasurement);
	}

    @Test
    public void readsDiaryWithNewColumnHeader() throws Exception {
        List<BloodSugarMeasurement> measurements = CsvFileReader.readFile(properCsvFileWithNewColumnHeader).measurements;

        assertEquals(2, measurements.size());
    }
	
	@Test(expected=IOException.class)
	public void failsWhenFirstHeadlineIsWrong() throws Exception {
		CsvFileReader.readFile(csvFileWithWrongFirstHeadline);
	}
	
	@Test(expected=IOException.class)
	public void failsWhenFileHasTooFewLines() throws Exception {
		CsvFileReader.readFile(csvFileWithTooFewLines);
	}
	
	@Test(expected=IOException.class)
	public void failsWhenSecondHeadlineIsWrong() throws Exception {
		CsvFileReader.readFile(csvFileWithWrongSecondHeadline);
	}
	
	@Test(expected=IOException.class)
	public void failsWhenUnitsAreWrong() throws Exception {
		CsvFileReader.readFile(csvFileWithWrongUnits);
	}
	
	@Test(expected=IOException.class)
	public void failsWhenRowHasTooFewColumns() throws Exception {
		CsvFileReader.readFile(csvFileWithTooFewColumns);
	}
	
	@Test(expected=IOException.class)
	public void failsWhenMetadataRowHasTooFewColumns() throws Exception {
		CsvFileReader.readFile(csvFileWithTooFewMetadataColumns);
	}
	
	@Test(expected=IOException.class)
	public void failsWhenRowHasTooManyColumns() throws Exception {
		CsvFileReader.readFile(csvFileWithTooManyColumns);
	}
	
	@Test(expected=IOException.class)
	public void failsWhenMetadataRowHasTooManyColumns() throws Exception {
		CsvFileReader.readFile(csvFileWithTooManyMetadataColumns);
	}
	
	private static File fileFor(String resource) {
		try {
			return new File(CsvFileReaderTest.class.getResource(resource).toURI());
		} catch (URISyntaxException e) {
			// Should never, ever be able to happen
			throw new IllegalStateException("class.getResource(...) gives an invalid URI? I don't think so.", e);
		}
	}
	
	private Date date(String dateAsString) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return dateFormat.parse(dateAsString);
	}
}
