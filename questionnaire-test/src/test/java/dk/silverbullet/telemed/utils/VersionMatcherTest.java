package dk.silverbullet.telemed.utils;

import junit.framework.Assert;

import org.junit.Test;

public class VersionMatcherTest {
	
	@Test
	public void canHandleMalformedVersions() {
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("Not a version string", "0.1.2"));
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0.1.2", "not a version string"));
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0.X.X", "0.1.1"));
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0.0.0", "0"));
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0", "1.2.3"));
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0.0.", "1.2.3"));
	}
	
	@Test
	public void canHandleDevelopmentVersions() {
		Assert.assertTrue(VersionMatcher.isClientVersionSupported("${version}", "0.1.2"));
	}
	
	@Test
	public void canTellThatClientVersionCompatible() {
		Assert.assertTrue(VersionMatcher.isClientVersionSupported("0.5.1", "0.4.1"));
		Assert.assertTrue(VersionMatcher.isClientVersionSupported("1.5.1", "0.5.1"));
		Assert.assertTrue(VersionMatcher.isClientVersionSupported("0.6.1", "0.5.1"));
		Assert.assertTrue(VersionMatcher.isClientVersionSupported("0.5.2", "0.5.1"));
		Assert.assertTrue(VersionMatcher.isClientVersionSupported("0.6.0", "0.5.1"));
		Assert.assertTrue(VersionMatcher.isClientVersionSupported("1000.5.2", "10.51.11"));
	}
	
	@Test
	public void canTellIfClientVersionIsTooOld() {
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0.5.0", "0.8.1"));
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0.8.0", "0.8.1"));
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0.8.0", "1.8.1"));
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0.9.5", "1.8.1"));
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0.8.5", "1.8.1"));
	}
	
	@Test
	public void canHandleNulls() {
		Assert.assertFalse(VersionMatcher.isClientVersionSupported("0.5.0", null));
		Assert.assertFalse(VersionMatcher.isClientVersionSupported(null, "0.5.0"));
	}
}
