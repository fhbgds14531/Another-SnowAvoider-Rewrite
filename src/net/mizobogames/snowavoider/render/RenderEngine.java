package net.mizobogames.snowavoider.render;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import net.mizobogames.snowavoider.SnowAvoider;
import net.mizobogames.snowavoider.entity.Entity;
import net.mizobogames.snowavoider.entity.Player;

public class RenderEngine implements Runnable {

	RenderTimer timer;
	static RenderEngine instance;
	boolean setupComplete = false;
	boolean fullscreen = false;

	public RenderEngine(RenderTimer renderTimer) {
		instance = this;
		this.timer = renderTimer;
		Thread timerThread = new Thread(timer);
		timerThread.setDaemon(true);
		timerThread.setName("Render Engine Timer");
		timerThread.start();
	}

	@Override
	public void run() {
		while (SnowAvoider.getGame() == null) {
			try {
				Thread.sleep(100l);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			DisplayMode mode = new DisplayMode(SnowAvoider.SCREEN_WIDTH, SnowAvoider.SCREEN_HEIGHT);
			Display.setDisplayMode(mode);
			Display.setTitle(SnowAvoider.BASE_WINDOW_TITLE);
			Display.create();
			this.getReadyFor2DDrawing();

			while (Keyboard.next()) {
			} // Flush queued keypresses
		} catch (Exception e) {
			System.err.println("Error setting up display.");
			e.printStackTrace();
			System.exit(-1);
			return;
		}
		setupComplete = true;
		System.out.println("[RENDER] Rendering Engine started successfully. Waiting to sync with other threads.");
		while (!SnowAvoider.getGame().getContinueRendering()) {
			try {
				Thread.sleep(100l);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("[RENDER] Game setup complete, beginning render loop.");
		while (SnowAvoider.getGame().getContinueRendering() && !Display.isCloseRequested()) {
			if (timer.hasElapsedTicks()) {
				for (int i = timer.getElapsedTicks(); i > 0; i--) {
					timer.doTick();
				}
			}
		}
		SnowAvoider.getGame().shutdown(0);
	}

	void doTick() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		List<Entity> entities = new ArrayList<Entity>();
		synchronized(SnowAvoider.lock){
			entities.addAll(SnowAvoider.getGame().getEntitiesForRender());
		}
		for(Entity e : entities){
			if(e instanceof Player){
				RenderEntity.drawPlayer((Player) e);
			}else{
				RenderEntity.drawEntity(e);
			}
		}
		Display.setTitle(SnowAvoider.BASE_WINDOW_TITLE + "   Level: " + SnowAvoider.getGame().getLevel() + "   UPS: " + SnowAvoider.getGameTimer().getUPS() + "   FPS: " + timer.getFPS());
		Display.update();
	}

	public boolean getSetupComplete() {
		return this.setupComplete;
	}

	private void getReadyFor2DDrawing() {
		glMatrixMode(GL_PROJECTION);
		glEnable(GL_TEXTURE);
		glEnable(GL_TEXTURE_2D);
		glLoadIdentity();
		GL11.glViewport(0, 0, SnowAvoider.SCREEN_WIDTH, SnowAvoider.SCREEN_HEIGHT);
		glOrtho(0, SnowAvoider.SCREEN_WIDTH, SnowAvoider.SCREEN_HEIGHT, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		GL11.glClearColor(0.2F, 0.2F, 0.2F, 1);
	}
}
