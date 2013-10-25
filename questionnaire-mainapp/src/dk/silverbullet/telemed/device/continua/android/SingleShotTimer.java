package dk.silverbullet.telemed.device.continua.android;

/**
 * Simple timer.
 */
public class SingleShotTimer {
    private Thread thread;

    /**
     * Starts the timer, notifying the listener after a given time.
     * 
     * @param milliseconds
     *            Time to wait before calling timeout on listener
     * @param listener
     *            Receiver of the timeout notification
     */
    public SingleShotTimer(final long milliseconds, final StopwatchListener listener) {
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(milliseconds);
                    if (!Thread.currentThread().isInterrupted()) {
                        listener.timeout();
                    }
                } catch (InterruptedException e) {
                    // Timeout was canceled
                }
            }
        });

        thread.start();
    }
}
