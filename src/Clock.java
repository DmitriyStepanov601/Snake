/**
 * Class describing the clock
 * @author Dmitriy Stepanov
 */
public class Clock {
	private float millisPerCycle;
	private long lastUpdate;
	private int elapsedCycles;
	private float excessCycles;
	private boolean isPaused;

	/**
	 * Constructor - creating a new clock
	 * @param cyclesPerSecond - cycles per second
	 * @see Clock#Clock(float)
	 */
	public Clock(float cyclesPerSecond) {
		setCyclesPerSecond(cyclesPerSecond);
		reset();
	}

	public void setCyclesPerSecond(float cyclesPerSecond) {
		this.millisPerCycle = (1.0f / cyclesPerSecond) * 1000;
	}

	/**
	 * Resetting the clock
	 */
	public void reset() {
		this.elapsedCycles = 0;
		this.excessCycles = 0.0f;
		this.lastUpdate = getCurrentTime();
		this.isPaused = false;
	}

	/**
	 * Updating the clock
	 */
	public void update() {
		long currUpdate = getCurrentTime();
		float delta = (float)(currUpdate - lastUpdate) + excessCycles;
		if(!isPaused) {
			this.elapsedCycles += (int)Math.floor(delta / millisPerCycle);
			this.excessCycles = delta % millisPerCycle;
		}
		this.lastUpdate = currUpdate;
	}

	// getter  and setter for boolean field
	public void setPaused(boolean paused) {
		this.isPaused = paused;
	}
	public boolean isPaused() {
		return isPaused;
	}

	public boolean hasElapsedCycle() {
		if(elapsedCycles > 0) {
			this.elapsedCycles--;
			return true;
		}
		return false;
	}

	public boolean peekElapsedCycle() {
		return (elapsedCycles > 0);
	}
	private static final long getCurrentTime() {
		return (System.nanoTime() / 1000000L);
	}
}