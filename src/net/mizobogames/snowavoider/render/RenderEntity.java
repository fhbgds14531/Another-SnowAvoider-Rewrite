package net.mizobogames.snowavoider.render;

import org.lwjgl.opengl.GL11;

import net.mizobogames.snowavoider.entity.Entity;
import net.mizobogames.snowavoider.entity.Player;

public class RenderEntity {
	
	public static void drawEntity(Entity p){
		if(p == null){
			System.err.println("[RENDER_ENITIY] Requested to draw null entity. Ignoring request...");
			return;
		}
		double[][] coords = p.getBB().getCoords();
		double[] rgb = p.getRGB();
		GL11.glColor3d(rgb[0], rgb[1], rgb[2]);
		
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2d(coords[0][0], coords[0][1]);
			GL11.glVertex2d(coords[1][0], coords[1][1]);
			GL11.glVertex2d(coords[2][0], coords[2][1]);
			GL11.glVertex2d(coords[3][0], coords[3][1]);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public static void drawPlayer(Player p){
		if(p == null){
			System.err.println("[RENDER_PLAYER] Requested to draw null entity. Ignoring request...");
			return;
		}
		double[][] coords = p.getBB().getCoords();
		
		GL11.glColor3d(0, 0, 0);
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2d(coords[0][0], coords[0][1]);
			GL11.glVertex2d(coords[1][0], coords[1][1]);
			GL11.glVertex2d(coords[2][0], coords[2][1]);
			GL11.glVertex2d(coords[3][0], coords[3][1]);
		GL11.glEnd();
		GL11.glPopMatrix();
		
		double[] rgb = p.getRGB();
		GL11.glColor3d(rgb[0], rgb[1], rgb[2]);
		
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2d(coords[0][0]+1, coords[0][1]+1);
			GL11.glVertex2d(coords[1][0]-1, coords[1][1]+1);
			GL11.glVertex2d(coords[2][0]-1, coords[2][1]-1);
			GL11.glVertex2d(coords[3][0]+1, coords[3][1]-1);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
}
