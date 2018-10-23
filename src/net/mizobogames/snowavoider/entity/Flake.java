package net.mizobogames.snowavoider.entity;

import net.mizobogames.snowavoider.SnowAvoider;

public class Flake extends Entity{
	
	int age = 0;
	boolean onGround = false;
	
	public Flake(double x, double y, double size) {
		super(x, y, size);
	}
	
	@Override
	public void onUpdate(){
		this.motionY += 1 + (this.xSize/40);
		this.age++;
		super.onUpdate();
		
		if(!this.onGround && (this.getBB().getCoords()[2][1] >= SnowAvoider.SCREEN_HEIGHT - this.ySize)){
			this.onGround = true;
		}
		
		if(this.onGround){
			this.changeSize(-0.025, -0.025);
		}
		
		if(this.age > SnowAvoider.MAX_FLAKE_AGE || this.xSize < 1 || this.ySize < 1){
			this.kill();
		}
	}

}
