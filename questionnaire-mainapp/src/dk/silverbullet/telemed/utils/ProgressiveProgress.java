package dk.silverbullet.telemed.utils;

public class ProgressiveProgress {
    private final int[] binStart;
    private final int[] increment;
    private final int binSize;
    private int binCount;

    public ProgressiveProgress(int start, int binSize, int... increment) {
        binCount = increment.length;
        this.binSize = binSize;
        this.binStart = new int[binCount];
        this.increment = new int[binCount];
        int currentBinStart = start;
        this.binStart[0] = currentBinStart;
        this.increment[0] = increment[0];
        for (int bin = 1; bin < binCount; bin++) {
            currentBinStart += increment[bin - 1] * (binSize - 1) + increment[bin];
            this.binStart[bin] = currentBinStart;
            this.increment[bin] = increment[bin];
        }
    }

    public int step2value(int step) {
        if (step < 0 || step >= binSize * binCount)
            throw new IllegalArgumentException("Parameter 'step' must be in the range 0.." + binSize * binCount);
        int bin = step / binSize;
        int binIndex = step % binSize;
        return binStart[bin] + increment[bin] * binIndex;
    }

    public int value2step(int value) {
        int bin = binCount - 1;
        for (int i = 1; i < binCount; i++) {
            if (value < binStart[i]) {
                bin = i - 1;
                break;
            }
        }

        int rest = value - binStart[bin];
        int step = bin * binSize + (rest + increment[bin] / 2) / increment[bin];
        if (step < 0 || step >= binCount * binSize)
            throw new IllegalArgumentException("Illegal value (" + value + " for ProgressiveProgress");
        return step;
    }

    public int getStepCount() {
        return binSize * binCount;
    }

    public int getHighestValue() {
        return step2value(getStepCount() - 1);
    }
}
