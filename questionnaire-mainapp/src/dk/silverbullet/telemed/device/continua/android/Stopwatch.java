package dk.silverbullet.telemed.device.continua.android;

/**
 * Simple timer.
 */
public class Stopwatch {
    private Thread thread;

    /**
     * Starts the timer, notifying the listener at given intervals.
     * 
     * @param milliseconds
     *            The interval, in milliseconds, between notifications.
     * @param listener
     */
    public synchronized void start(final long milliseconds, final StopwatchListener listener) {
        cancel();

        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(milliseconds);
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        listener.timeout();
                    }
                } catch (InterruptedException e) {
                    // Timeout was canceled
                }
            }
        });

        thread.start();
    }

    /**
     * Stops the timer.
     */
    public synchronized void cancel() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
}
