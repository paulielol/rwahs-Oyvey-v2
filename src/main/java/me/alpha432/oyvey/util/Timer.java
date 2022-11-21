package me.alpha432.oyvey.util;

public class Timer {
    private long time;
    private long current;
    long startTime = System.currentTimeMillis();
    long delay = 0L;
    boolean paused = false;

    public Timer() {
        this.time = -1L;
        this.current = System.currentTimeMillis();
    }

    public boolean passedS(final double s) {
        return this.passedMs((long) s * 1000L);
    }

    public boolean passedDms(final double dms) {
        return this.passedMs((long) dms * 10L);
    }

    public boolean passedDs(final double ds) {
        return this.passedMs((long) ds * 100L);
    }

    public boolean passedMs(final long ms) {
        return this.passedNS(this.convertToNS(ms));
    }

    public void setMs(final long ms) {
        this.time = System.nanoTime() - this.convertToNS(ms);
    }

    public boolean passedNS(final long ns) {
        return System.nanoTime() - this.time >= ns;
    }

    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }

    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }

    public boolean isPassed() {
        return !this.paused && System.currentTimeMillis() - this.startTime >= this.delay;
    }

    public void resetDelay() {
        this.startTime = System.currentTimeMillis();
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public long getStartTime() {
        return this.startTime;
    }


    public void reset2() {
        this.current = System.currentTimeMillis();
    }

    public long getMs(final long time) {
        return time / 1000000L;
    }

    public long convertToNS(final long time) {
        return time * 1000000L;
    }

    public boolean passed(final long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }

}
