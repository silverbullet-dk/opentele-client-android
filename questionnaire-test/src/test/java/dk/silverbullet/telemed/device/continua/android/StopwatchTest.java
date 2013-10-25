package dk.silverbullet.telemed.device.continua.android;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import dk.silverbullet.telemed.device.continua.android.Stopwatch;
import dk.silverbullet.telemed.device.continua.android.StopwatchListener;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StopwatchTest {
	@Mock StopwatchListener listener;
	
	@Test
	public void doesNothingIfStoppedBeforeTimeout() throws Exception {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start(200, listener);
		Thread.sleep(100);
		stopwatch.cancel();
		Thread.sleep(100);

		verifyNoMoreInteractions(listener);
	}
	
	@Test
	public void notifiesWhenTimedOut() throws Exception {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start(100, listener);
		Thread.sleep(150);
		stopwatch.cancel();
		Thread.sleep(100);

		verify(listener).timeout();
		verifyNoMoreInteractions(listener);
	}
	
	@Test
	public void notifiesMultipleTimesIfNotCanceled() throws Exception {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start(100, listener);
		Thread.sleep(250);
		stopwatch.cancel();
		Thread.sleep(100);

		verify(listener, times(2)).timeout();
		verifyNoMoreInteractions(listener);
	}
	
	@Test
	public void cancelsCurrentlyRunningStopwatchWhenStartedAgain() throws Exception {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start(100, listener);
		stopwatch.start(100, listener);
		stopwatch.start(100, listener);
		Thread.sleep(150);
		stopwatch.cancel();
		Thread.sleep(100);

		verify(listener).timeout();
		verifyNoMoreInteractions(listener);
	}
	
	@Test
	public void allowsCancelingACanceledWatch() throws Exception {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start(100, listener);
		stopwatch.cancel();
		stopwatch.cancel();
	}
}
