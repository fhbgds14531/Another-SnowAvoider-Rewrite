package net.mizobogames.snowavoider.entity;

import java.util.UUID;

import net.mizobogames.snowavoider.Physics;

public class Entity {

	double xPos;
	double yPos;
	
	double xSize;
	double ySize;
	
	double motionX;
	double motionY;
	
	double r = 1;
	double g = 1;
	double b = 1;
	
	BoundingBox bb;
	private UUID id;
	
	boolean alive = true;
	
	public Entity(double x, double y, double size){
		this(x, y, size, size);
	}
	
	public Entity(double x, double y, double sizeX, double sizeY){
		this.xPos = x;
		this.yPos = y;
		this.xSize = sizeX;
		this.ySize = sizeY;
		
		this.bb = new BoundingBox(x, y, sizeX, sizeY);
		this.id = UUID.randomUUID();
	}
	
	public void onUpdate(){
		this.xPos += this.motionX;
		this.yPos += this.motionY;
		
		this.bb.moveBy(motionX, motionY);
		
		this.motionX *= Physics.FRICTION_MULT;
		this.motionY *= Physics.FRICTION_MULT;
		if(this.motionX < 0.5 && this.motionX > -0.5) this.motionX = 0;
		if(this.motionY < 0.5 && this.motionY > -0.5) this.motionY = 0;
	}
	
	public BoundingBox getBB(){
		return this.bb;
	}
	
	public double[] getRGB(){
		return new double[] {this.r, this.g, this.b};
	}
	
	public void setRGB(double r, double g, double b){
		if(r < 0) r = 0;
		if(r > 1) r = 1;
		this.r = r;
		
		if(g < 0) g = 0;
		if(g > 1) g = 1;
		this.g = g;
		
		if(b < 0) b = 0;
		if(b > 1) b = 1;
		this.b = b;
	}
	
	public void accelerate(double x, double y){
		this.motionX += x;
		this.motionY += y;
	}
	
	public void changeSize(double deltaX, double deltaY){
		this.xSize += deltaX;
		this.ySize += deltaY;
		this.getBB().scaleBy(deltaX, deltaY);
	}
	
	public void moveTo(double x, double y){
		this.xPos = x;
		this.yPos = y;
		this.getBB().moveTo(x, y);
	}
	
	public void moveBy(double x, double y){
		this.xPos += x;
		this.yPos += y;
		this.getBB().moveBy(x, y);
	}
	
	public double[] getSize(){
		return new double[] {this.xSize, this.ySize};
	}
	
	public void kill(){
		this.alive = false;
	}
	
	public boolean isAlive(){
		return this.alive;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Entity){
			Entity e = (Entity) o;
			return e.id.equals(this.id);
		}
		return false;
	}
}
