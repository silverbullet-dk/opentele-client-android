package dk.silverbullet.telemed.questionnaire.element;

import static org.junit.Assert.*;

import org.junit.Test;

public class EditTextElementTest {
	@Test
	public void allowsAnythingAsPlainString() {
		assertTrue(EditTextElement.validates("asjb.,.,/\\sdg_", String.class, null));
	}
	
	@Test
	public void doesNotAllowEmptyString() {
		assertFalse(EditTextElement.validates("", String.class, null));
	}
	
	@Test
	public void allowsValidIntegerAsInteger() {
		assertTrue(EditTextElement.validates("12345", Integer.class, null));
	}
	
	@Test
	public void doesNotAllowIntegerWithMoreThan6Digits() {
		assertTrue(EditTextElement.validates("999999", Integer.class, null));
		assertFalse(EditTextElement.validates("1000000", Integer.class, null));
	}
	
	@Test
	public void doesNotAllowDecimalSeparaterInInteger() {
		assertFalse(EditTextElement.validates("12345.5", Integer.class, null));
	}

	@Test
	public void doesNotAllowNegativeIntegers() {
		assertFalse(EditTextElement.validates("-0", Integer.class, null));
		assertFalse(EditTextElement.validates("-1", Integer.class, null));
		assertFalse(EditTextElement.validates("-20", Integer.class, null));
	}
	
	@Test
	public void allowsValidFloatAsFloat() {
		assertTrue(EditTextElement.validates("1234", Float.class, null));
		assertTrue(EditTextElement.validates("1234.567", Float.class, null));
	}

	@Test
	public void doesNotAllowFloatsWithMoreThan6Digits() {
		assertTrue(EditTextElement.validates("999999", Float.class, null));
		assertFalse(EditTextElement.validates("1000000", Float.class, null));
	}

	@Test
	public void doesNotAllowFloatsEndingInDecimalSeparator() {
		assertFalse(EditTextElement.validates(".", Float.class, null));
		assertFalse(EditTextElement.validates("1234.", Float.class, null));
	}

	@Test
	public void doesNotAllowFloatsStartingWithDecimalSeparator() {
		assertFalse(EditTextElement.validates(".", Float.class, null));
		assertFalse(EditTextElement.validates(".1234", Float.class, null));
	}
	
	@Test
	public void allowsFloatsWithDecimalsWithinRange() {
		assertTrue(EditTextElement.validates("123.456", Float.class, 3));
		assertTrue(EditTextElement.validates("123", Float.class, 0));
	}
	
	@Test
	public void doesNotAllowNegativeFloatValues() {
		assertFalse(EditTextElement.validates("-0.3", Float.class, 3));
		assertFalse(EditTextElement.validates("-1", Float.class, 0));
		assertFalse(EditTextElement.validates("-0", Float.class, 0));
		assertFalse(EditTextElement.validates("-10", Float.class, 0));
		assertFalse(EditTextElement.validates("-10.1234", Float.class, 2));
	}

	@Test
	public void doesNotAllowFloatsWithTooManyDecimals() {
		assertFalse(EditTextElement.validates("123.456", Float.class, 2));
		assertFalse(EditTextElement.validates("123.1", Float.class, 0));
	}
	
	@Test
	public void doesNotAllowSeveralDecimalPointsInFloats() {
		assertFalse(EditTextElement.validates("123.456.78", Float.class, null));
	}
}
