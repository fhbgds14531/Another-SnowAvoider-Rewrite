package net.mizobogames.snowavoider.render;

import net.mizobogames.snowavoider.SnowAvoider;

public class RenderTimer implements Runnable {

	// 1 billion nanoseconds is one second.
	private float ticksPerSecond;
	private long timeOfLastTick;
	private int elapsedTicks;
	
	private int framesThisSecond = 0;
	private int fps = 0;
	private long timeOfLastSecond;

	public RenderTimer(float tps) {
		this.ticksPerSecond = tps;
		this.timeOfLastTick = System.nanoTime();
		this.timeOfLastSecond = this.timeOfLastTick;
	}

	public void run() {
		this.timeOfLastTick = System.nanoTime();
		while (SnowAvoider.getGame() == null) {
			try {
				Thread.sleep(100l);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		while (!SnowAvoider.getGame().getContinueRendering()) {
			try {
				Thread.sleep(100l);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		while (SnowAvoider.getGame().getContinueRendering()) {
			updateTimer();
		}
	}

	public void updateTimer() {
		long currentTime = System.nanoTime();
		float diff = (currentTime - this.timeOfLastTick);
		diff /= 1_000_000_000;
		if (diff >= (1 / this.ticksPerSecond)) {
			this.elapsedTicks++;
			this.timeOfLastTick = System.nanoTime();
		}
	}

	public synchronized boolean hasElapsedTicks() {
		return (this.elapsedTicks > 0);
	}

	public synchronized int getElapsedTicks() {
		return this.elapsedTicks;
	}

	public void doTick() {
		if(this.elapsedTicks > this.ticksPerSecond){
			int skippedTicks = 0;
			while(this.elapsedTicks > this.ticksPerSecond){
				this.elapsedTicks -= this.ticksPerSecond;
				skippedTicks += this.ticksPerSecond;
			}
			System.err.println("[RENDER TIMER] WARNING: Can't keep up! the number of queued render ticks is higher than the tps of this timer. Skipping " + skippedTicks + " ticks.");
		}
		this.elapsedTicks--;
		RenderEngine.instance.doTick();
		
		this.framesThisSecond++;
		long currentTime = System.nanoTime();
		float diff = (currentTime - this.timeOfLastSecond);
		diff /= 1_000_000_000;
		if(diff >= 1){
			this.timeOfLastSecond = currentTime;
			this.fps = this.framesThisSecond;
			this.framesThisSecond = 0;
		}
	}
	
	public synchronized int getFPS(){
		return this.fps;
	}
}