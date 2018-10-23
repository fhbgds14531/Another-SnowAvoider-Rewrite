package net.mizobogames.snowavoider;

public class Timer implements Runnable {

	// 1 billion nanoseconds is one second.
	private float ticksPerSecond;
	private long timeOfLastTick;
	private int elapsedTicks;
	
	private int pauseFor = 0;
	
	private int updatesThisSecond = 0;
	private int ups = 0;
	private long timeOfLastSecond;


	boolean setupComplete = false;

	public Timer(float tps) {
		this.ticksPerSecond = tps;
		this.timeOfLastTick = System.nanoTime();
	}

	public void run() {
		while (SnowAvoider.getGame() == null) {
			try {
				Thread.sleep(100l);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.timeOfLastTick = System.nanoTime();
		this.elapsedTicks = 1;
		setupComplete = true;
		System.out.println("[UPDATER] Waiting for other threads.");
		while (!SnowAvoider.getGame().running) {
			try {
				Thread.sleep(100l);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("[UPDATER] Beginning update loop.");
		this.timeOfLastTick = System.nanoTime();
		this.elapsedTicks = 1;
		while (SnowAvoider.getGame().running) {
			updateTimer();
		}
	}

	public void updateTimer() {
		long currentTime = System.nanoTime();
		float diff = (currentTime - this.timeOfLastTick);
		diff /= 1_000_000_000;
		if (diff >= (1 / this.ticksPerSecond)) {
			if(this.pauseFor <= 0){
				this.elapsedTicks++;
			}else{
				this.pauseFor--;
			}
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
			System.err.println("[UPDATE TIMER] WARNING: Can't keep up! the number of queued ticks is higher than the tps of this timer. Skipping " + skippedTicks + " ticks.");
		}
		this.elapsedTicks--;
		SnowAvoider.getGame().doTick();
		
		this.updatesThisSecond++;
		long currentTime = System.nanoTime();
		float diff = (currentTime - this.timeOfLastSecond);
		diff /= 1_000_000_000;
		if(diff >= .5){
			this.timeOfLastSecond = currentTime;
			this.ups = this.updatesThisSecond*2;
			this.updatesThisSecond = 0;
		}
	}
	
	public void pauseFor(int numberOfTicks){
		this.pauseFor = numberOfTicks;
	}
	
	public int getUPS(){
		return this.ups;
	}
}