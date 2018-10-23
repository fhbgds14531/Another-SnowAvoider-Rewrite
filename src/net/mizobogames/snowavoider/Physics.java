package net.mizobogames.snowavoider;

import net.mizobogames.snowavoider.entity.BoundingBox;
import net.mizobogames.snowavoider.entity.Entity;
import net.mizobogames.snowavoider.entity.Player;

public class Physics {

	public static final double FRICTION_MULT = 0.7;

	public static boolean confineToBounds(Player toConfine, BoundingBox bounds) {
		double[][] boundaryVertices = bounds.getCoords();
		double[][] entityVertices = toConfine.getBB().getCoords();

		if (!toConfine.getBB().isFullyInside(bounds)) {
			if (boundaryVertices[0][0] - entityVertices[0][0] > 0) {
				double diff = boundaryVertices[0][0] - entityVertices[0][0];
				toConfine.accelerate(diff, 0);
			}
			if (boundaryVertices[0][1] - entityVertices[0][1] > 0) {
				double diff = boundaryVertices[0][1] - entityVertices[0][1];
				toConfine.accelerate(0, diff);
			}
			if (entityVertices[1][0] - boundaryVertices[1][0] > 0) {
				double diff = boundaryVertices[1][0] - entityVertices[1][0];
				toConfine.accelerate(diff, 0);
			}
			if (entityVertices[3][1] - boundaryVertices[3][1]> 0) {
				double diff = boundaryVertices[3][1] - entityVertices[3][1];
				toConfine.accelerate(0, diff);
			}
		}
		return false;
	}
	
	public static boolean confineToBounds(Entity toConfine, BoundingBox bounds) {
		double[][] boundaryVertices = bounds.getCoords();
		double[][] entityVertices = toConfine.getBB().getCoords();

		if (!toConfine.getBB().isFullyInside(bounds)) {
			if (boundaryVertices[0][0] - entityVertices[0][0] > 0) {
				double diff = boundaryVertices[0][0] - entityVertices[0][0];
				toConfine.accelerate(diff, 0);
			}
			if (boundaryVertices[0][1] - entityVertices[0][1] > 0) {
				double diff = boundaryVertices[0][1] - entityVertices[0][1];
				toConfine.accelerate(0, diff);
			}
			if (entityVertices[1][0] - boundaryVertices[1][0] > 0) {
				double diff = boundaryVertices[1][0] - entityVertices[1][0];
				toConfine.accelerate(diff, 0);
			}
			if (entityVertices[3][1] - boundaryVertices[3][1]> 0) {
				double diff = boundaryVertices[3][1] - entityVertices[3][1];
				toConfine.accelerate(0, diff);
			}
		}
		return false;
	}

}
