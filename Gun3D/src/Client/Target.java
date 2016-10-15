package Client;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.paint.PhongMaterial;

public class Target extends Sphere {
	
	private double leftBoundry, rightBoundry;
	private PhongMaterial material;
	private int direction = 1;

	private final Color [] colors = {Color.BLACK,Color.YELLOW, Color.RED, Color.BLUEVIOLET, Color.BROWN, Color.DEEPSKYBLUE,
			Color.LIMEGREEN, Color.MEDIUMVIOLETRED, Color.ORANGERED, Color.GREENYELLOW};
	
	public Target(int radius, double x, double y, double z, double leftBoundry, double rightBoundry){
		
		paintTarget(radius, x, y, z);
		if(x+radius>rightBoundry){
			direction *= -1;
		}
		this.leftBoundry = leftBoundry;
		this.rightBoundry = rightBoundry;
	}
	

	public void paintTarget(int radius, double x, double y, double z){
        material = new PhongMaterial();
        int random1 = (int)(Math.random()*colors.length);
        int random2 = (int)(1 + Math.random()*(colors.length-1));
        
        material.setDiffuseColor(colors[random1]);
        material.setSpecularColor(colors[(random1+random2)%colors.length]);
		setTranslateX(x);
		setTranslateY(y);
		setTranslateZ(z);
		setRadius(radius + this.getTranslateZ()/2);
		setDrawMode(DrawMode.FILL);
        setMaterial(material);
	}
	

	public void moveTarget() {
		if(getTranslateX() + getRadius() > rightBoundry || 
				getTranslateX() - getRadius() < leftBoundry)
			direction*=-1;
		setTranslateX(getTranslateX()+direction);
	}
}
