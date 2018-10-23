package net.mizobogames.snowavoider.entity;

import java.io.Serializable;

public class BoundingBox implements Serializable{

	private static final long serialVersionUID = 385748080520054421L;

	private double xPos, yPos, xSize, ySize;
	private double lastCalcXPos, lastCalcYPos, lastCalcXSize, lastCalcYSize;
	private double[][] coords;

	public BoundingBox(double xPos, double yPos, double xSize, double ySize){
		this.xPos = xPos;
		this.yPos = yPos;
		this.xSize = xSize;
		this.ySize = ySize;
		
		this.calcCoords();
	}

	private void calcCoords(){
		boolean unchanged = true;
		unchanged = xPos == lastCalcXPos;
		unchanged = yPos == lastCalcYPos;
		unchanged = xSize == lastCalcXSize;
		unchanged = ySize == lastCalcYSize;
		
		if(unchanged && this.coords != null) return;
		
		double[] v0 = new double[] {xPos - (xSize/2), yPos - (ySize/2)};
		double[] v1 = new double[] {xPos + (xSize/2), yPos - (ySize/2)};
		double[] v2 = new double[] {xPos + (xSize/2), yPos + (ySize/2)};
		double[] v3 = new double[] {xPos - (xSize/2), yPos + (ySize/2)};
		
		this.coords = new double[][] {v0, v1, v2, v3};
	}
	
	public BoundingBox(BoundingBox bb){
		this(bb.xPos, bb.yPos, bb.xSize, bb.ySize);
	}
	
	public double[][] getCoords(){
		this.calcCoords();
		return coords;
	}

	public void moveTo(double x, double y){
		this.xPos = x;
		this.yPos = y;
	}

	public void moveBy(double xAmount, double yAmount){
		this.xPos += xAmount;
		this.yPos += yAmount;
	}

	public void scaleTo(double x, double y){
		this.xSize = x;
		this.ySize = y;
	}

	public void moveAndScaleTo(double xPos, double yPos, double xSize, double ySize){
		this.moveTo(xPos, yPos);
		this.scaleTo(xSize, ySize);
	}

	public boolean isInside(BoundingBox bb){
		this.calcCoords();
		if(bb.coords[0][0] > this.coords[1][0] || bb.coords[1][0] < this.coords[0][0])
			return false;
		if(bb.coords[0][1] > this.coords[2][1] || bb.coords[2][1] < this.coords[0][1])
			return false;
		return true;
	}
	
	public boolean isFullyInside(BoundingBox bb){
		this.calcCoords();
		if(this.coords[0][0] > bb.coords[0][0] && this.coords[1][0] < bb.coords[1][0]){
			if(this.coords[0][1] > bb.coords[0][1] && this.coords[1][1] < bb.coords[1][1]){
				return true;
			}
		}
		return false;
	}

	public double xPos(){
		return this.xPos;
	}

	public double yPos(){
		return this.yPos;
	}

	public double xSize(){
		return this.xSize;
	}

	public double ySize(){
		return this.ySize;
	}

	@Override
	public boolean equals(Object o){
		if(!(o instanceof BoundingBox))
			return false;
		BoundingBox bb = (BoundingBox) o;

		if(this.xPos == bb.xPos && this.xSize == bb.xSize && this.yPos == bb.yPos && this.ySize == bb.ySize)
			return true;

		return false;
	}

	@Override
	public String toString(){
		return "Entity Bounding Box: [Position: (" + this.xPos + ", " + this.yPos + "), Size: (" + this.xSize + ", "
				+ this.ySize + ")]";
	}

	public void scaleBy(double xAmount, double yAmount) {
		this.xSize += xAmount;
		this.ySize += yAmount;
	}
}
