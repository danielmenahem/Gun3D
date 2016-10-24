package Client;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.paint.PhongMaterial;

/**
 * This class provides an API compatible with Target, with JavaFx.
 * Creates and control a target for the game.
 * 
 * <br>
 * Extends: {@link Sphere}
 * 
 * @author Daniel Menahem 39676804
 * @author Michael Shvarts 301578878
 * @version 1.0
 */
public class Target extends Sphere {
	/**
	 * The {@code leftBoundary} holds target left boundary
	 */
	private double leftBoundary;
	
	/**
	 * The {@code rightBoundary} holds target right boundary
	 */
	private double rightBoundary;
	
	/**
	 * The ({@code actualRadius} holds the target actual radius
	 * */
	private int actualRadius;
	
	/**
	 * The {@code material} is a {@link PhongMaterial}
	 */
	private PhongMaterial material;
	
	/**
	 * The {@code direction} holds the target movement direction
	 * */
	private int direction = 1;

	/**
	 * The {@code colors} is an {@link Array}. holds list of colors
	 * @see Color
	 */
	private static final Color [] colors = {Color.BLACK,Color.YELLOW, Color.RED, Color.BLUEVIOLET, Color.BROWN, Color.DEEPSKYBLUE,
			Color.LIMEGREEN, Color.MEDIUMVIOLETRED, Color.ORANGERED, Color.GREENYELLOW};
	
	
	
	/**
	 * Construct a target
	 * @param radius the target radius
	 * @param x the center x value of the target
	 * @param y the center y value of the target
	 * @param z the center z value of the target
	 * @param leftBoundary the target left boundary
	 * @param rightBoundary the target right boundary
	 * */
	public Target(int radius, double x, double y, double z, double leftBoundary, double rightBoundary){
		this.leftBoundary = leftBoundary;
		this.rightBoundary = rightBoundary;
		this.actualRadius = radius;
		paintTarget(radius, x, y, z);
		if(x + this.getRadius()>rightBoundary){
			direction *= -1;
		}
	}
	

	/**
	 * Paints the target on the container
	 * @param radius the target radius
	 * @param x the center x value of the target
	 * @param y the center y value of the target
	 * @param z the center z value of the target
	 * */
	public void paintTarget(int radius, double x, double y, double z){
        material = new PhongMaterial();
        int random1 = (int)(Math.random()*colors.length);
        int random2 = (int)(1 + Math.random()*(colors.length - 1));
        
        material.setDiffuseColor(colors[random1]);
        material.setSpecularColor(colors[(random1+random2)%colors.length]);
		setTranslateZ(z);
		setRadius(Math.max(radius, getTranslateZ()+1));
		setTranslateX(Math.min(Math.max(x,this.getRadius()+leftBoundary + 1), 
				this.rightBoundary-(this.getRadius()+1)));
		setTranslateY(Math.max(y, this.getRadius()+5));
		setDrawMode(DrawMode.FILL);
        setMaterial(material);
	}
	
	
	/**
	 * Checks if the cannon shell hit the target
	 * @param cs the reviewed {@link CannonShell}
	 * @return true if hit (boolean)
	 * */
	public boolean checkForHit(CannonShell cs){
		return Math.sqrt(Math.pow((this.getTranslateX() - cs.getTranslateX()), 2)
				+ Math.pow((this.getTranslateY() - cs.getTranslateY()), 2) 
				+ Math.pow((this.getTranslateZ() - cs.getTranslateZ()), 2)) 
				<= (this.actualRadius);
	}
	
	
	/**
	 * Moves the target 1 unit on the X axis
	 * */
	public void moveTarget() {
		if(getTranslateX() + getRadius() > rightBoundary || 
				getTranslateX() - getRadius() < leftBoundary)
			direction*=-1;
		setTranslateX(getTranslateX()+direction);
	}
}
