package net.mizobogames.snowavoider;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import net.mizobogames.snowavoider.entity.BoundingBox;
import net.mizobogames.snowavoider.entity.Entity;
import net.mizobogames.snowavoider.entity.Flake;
import net.mizobogames.snowavoider.entity.Player;
import net.mizobogames.snowavoider.render.RenderEngine;
import net.mizobogames.snowavoider.render.RenderTimer;

public class SnowAvoider {

	public static final Object lock = new Object();
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final BoundingBox screenBoundary = new BoundingBox(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, SCREEN_WIDTH,
			SCREEN_HEIGHT);
	public static final String BASE_WINDOW_TITLE = "Snow Avoider 2";
	private static final int GAME_TIMER_UPDATE_FREQUENCY = 60;
	private static final int RENDER_TIMER_UPDATE_FREQUENCY = 120;
	public static final int MAX_FLAKE_AGE = GAME_TIMER_UPDATE_FREQUENCY * 4;

	private static SnowAvoider instance;
	boolean continueRendering = false;
	boolean running = false;
	boolean paused = false;
	static Timer gameTimer;
	static RenderTimer renderTimer;
	static RenderEngine renderer;
	SecureRandom rand;
	private long levelNumber = 1L;
	private int pausedFor = 0;
	private boolean handlingDeath = false;

	Player player;

	List<Entity> entities;
	List<Entity> renderEntities;

	public SnowAvoider() throws Exception {
		instance = this;

		rand = new SecureRandom();
		rand.setSeed(levelNumber);

		entities = new ArrayList<Entity>();
		renderEntities = entities;

		player = new Player(SCREEN_WIDTH / 2, SCREEN_HEIGHT - 10);
		this.getEntities().add(player);

		System.out.println("[MAIN] Setup complete, waiting for other threads.");
		while (!gameTimer.setupComplete || !renderer.getSetupComplete()) {
			try {
				Thread.sleep(100l);
			} catch (Exception e) {
				System.err.println("Error during setup");
				e.printStackTrace();
				System.exit(-1);
			}
		}

		running = true;
		continueRendering = true;
		System.out.println("[MAIN] Entering main loop.");
		while (running) {
			if (gameTimer.hasElapsedTicks()) {
				for (int i = gameTimer.getElapsedTicks(); i > 0; i--) {
					gameTimer.doTick();
				}
			}
		}
	}

	public void doTick() {
		if (!handlingDeath) {
			List<Entity> dead = new ArrayList<Entity>();

			if (rand.nextFloat() < 0.35F) {
				double xPos;
				if (rand.nextFloat() < 0.175) {
					xPos = rand.nextInt(60) + player.getBB().xPos() - 15;
				} else {
					xPos = rand.nextInt(SCREEN_WIDTH);
				}
				Flake f = new Flake(xPos, 10, rand.nextInt(5) + 5);
				this.getEntities().add(f);
			}
			boolean advanceLevel = false;
			for (Entity e : this.getEntities()) {
				e.onUpdate();
				if (e instanceof Player) {
					Player p = (Player) e;
					if (p.getBB().getCoords()[0][1] <= 0) {
						advanceLevel = true;
					}
				}
				if (!advanceLevel) {
					Physics.confineToBounds(e, screenBoundary);
				}
				if (!e.isAlive()) {
					dead.add(e);
				}
			}
			for (Entity e : dead) {
				this.getEntities().remove(e);
			}
			dead = null;

			synchronized (lock) {
				this.renderEntities = this.getEntities();
			}

			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
						SnowAvoider.getGame().shutdown(0);
					}
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				SnowAvoider.getGame().getPlayer().accelerate(0, -1.75);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				SnowAvoider.getGame().getPlayer().accelerate(-1.75, 0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				SnowAvoider.getGame().getPlayer().accelerate(0, 1.75);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				SnowAvoider.getGame().getPlayer().accelerate(1.75, 0);
			}

			if (advanceLevel) {
				this.levelNumber++;
				this.player.moveTo(SCREEN_WIDTH / 2, SCREEN_HEIGHT - (this.player.getSize()[1] / 2));
				this.rand.setSeed(levelNumber);
				this.entities = new ArrayList<Entity>();
				this.entities.add(player);
			}

			boolean collided = false;
			Entity killer = null;
			for (Entity e : SnowAvoider.getGame().getEntities()) {
				if (!e.equals(this.player)) {
					if (this.player.getBB().isInside(e.getBB())) {
						collided = true;
						killer = e;
					}
				}
			}
			if (collided) {
				this.player.setRGB(1, 0.3, 0.3);
				killer.setRGB(1, 0.3, 0.3);
				gameTimer.pauseFor(GAME_TIMER_UPDATE_FREQUENCY/2);
				this.pausedFor = GAME_TIMER_UPDATE_FREQUENCY/2;
				this.handlingDeath = true;
			}
		}else{
			if(this.pausedFor > 0){
				this.pausedFor--;
			}else{
				this.pausedFor = 0;
				this.entities = new ArrayList<Entity>();
				this.entities.add(this.player);
				this.player.moveTo(SCREEN_WIDTH/2, SCREEN_HEIGHT - (this.player.getSize()[1]/2));
				this.player.setRGB(1, 1, 1);
				this.handlingDeath = false;
			}
		}
	}

	public void shutdown(int code) {
		this.running = false;
		this.continueRendering = false;
		System.exit(code);
	}

	public Player getPlayer() {
		return player;
	}

	public synchronized List<Entity> getEntities() {
		return this.entities;
	}

	public synchronized List<Entity> getEntitiesForRender() {
		synchronized (lock) {
			return this.renderEntities;
		}
	}

	public static SnowAvoider getGame() {
		return instance;
	}

	public boolean getPaused() {
		return this.paused;
	}

	public long getLevel() {
		return this.levelNumber;
	}

	public static Timer getGameTimer() {
		return gameTimer;
	}

	public boolean getRunning() {
		return this.running;
	}

	public boolean getContinueRendering() {
		return this.continueRendering;
	}

	public static long getSystemTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	public static void main(String[] args) throws Exception {
		File JGLLib = null;
		switch(LWJGLUtil.getPlatform())
		{
		    case LWJGLUtil.PLATFORM_WINDOWS:
		    {
		        JGLLib = new File("./SnowAvoider2_lib/native/windows/");
		    }
		    break;

		    case LWJGLUtil.PLATFORM_LINUX:
		    {
		        JGLLib = new File("./SnowAvoider2_lib/native/linux/");
		    }
		    break;

		    case LWJGLUtil.PLATFORM_MACOSX:
		    {
		        JGLLib = new File("./SnowAvoider2_lib/native/macosx/");
		    }
		    break;
		}
		System.setProperty("org.lwjgl.librarypath", JGLLib.getAbsolutePath());
		
		gameTimer = new Timer(GAME_TIMER_UPDATE_FREQUENCY);
		renderTimer = new RenderTimer(RENDER_TIMER_UPDATE_FREQUENCY);
		renderer = new RenderEngine(renderTimer);

		Thread gameTimerThread = new Thread(gameTimer);
		gameTimerThread.setDaemon(true);
		gameTimerThread.setName("Game Timer Thread");
		gameTimerThread.start();

		Thread renderEngineThread = new Thread(renderer);
		renderEngineThread.setDaemon(true);
		renderEngineThread.setName("Rendering Engine");
		renderEngineThread.start();
		
		new SnowAvoider();
	}
}
