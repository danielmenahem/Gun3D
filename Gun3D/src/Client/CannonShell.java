package Client;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.paint.PhongMaterial;

/**
 * This class provides an API compatible with CannonShell, with JavaFx.
 * Creates and control the cannon shell.
 * 
 * <br>
 * Extends: {@link Sphere}
 * 
 * @author Daniel Menahem 39676804
 * @author Michael Shvarts 301578878
 * @version 1.0
 */
public class CannonShell extends Sphere{
	/**
	 * The value of static final {@code RADIUS} is {@value}.
	 */
	public static final int RADIUS = 8;
	
	/**
	 * The value of static final {@code INCREASMENT} is {@value}.
	 */
	public static final int INCREASMENT = 2;
	
	/**
	 * The {@code material} is a {@link PhongMaterial}
	 */
	private PhongMaterial material;
	
	/**
	 * The {@code theta} holds the cannon shell theta angel
	 * */
	private int theta;
	
	/**
	 * The {@code phi} holds the cannon shell phi angel
	 * */
	private int phi;
	
	/**
	 * The {@code length} holds the cannon shell length from bottom center
	 * */
	private int length;
	
	/**
	 * The {@code height} holds the container height
	 * */
	private double height;

	/**
	 * The {@code height} holds the container width
	 * */
	private double width;
	
	
	/**
	 * Constructs and paint cannon shell
	 * @param theta the cannon shell fire theta angel
	 * @param phi the cannon shell fire phi angel
	 * @param length the cannon shell initial length from bottom center 
	 * @param height the container height
	 * @param width the container width
	 * */
	public CannonShell(int theta, int phi, int length, double height, double width){	
		this.theta = theta;
		this.phi = phi;
		this.length = length;
		this.height = height;
		this.width = width;
		paintCannonShell();
	}
	
	
	/**
	 * Paints the cannon shell on the container
	 * */
	private void paintCannonShell(){
        material = new PhongMaterial();
        material.setDiffuseColor(Color.GREY);
        material.setSpecularColor(Color.BLACK);
		setRadius(RADIUS);
		moveShell();
		setDrawMode(DrawMode.FILL);
        setMaterial(material);
	}
	
	
	/**
	 * Checks if the cannon shell reached out of bounds
	 * @param maxZ the max z depth allowed
	 * @return true if out of bounds (boolean)
	 * */
	public boolean isOutofBounds(double maxZ) {
		
		return ((this.getTranslateX() + CannonShell.RADIUS > (this.width)) || 
				(this.getTranslateX() - CannonShell.RADIUS < 0) || 
				(this.getTranslateY() - CannonShell.RADIUS*2 < 0) || 
				(this.getTranslateZ() > maxZ));
	}
	
	
	/**
	 * Moves the shell one unit based on theta and phi angles
	 * */
	public void moveShell() {
		this.length +=INCREASMENT;
		double z = this.getTranslateZ();
		setTranslateX((int) (this.length*Math.cos(Math.toRadians(this.theta))*Math.sin(Math.toRadians(this.phi)) + this.width/2 ));
		setTranslateY((int) (this.height - this.length*Math.sin(Math.toRadians(this.theta))*Math.sin(Math.toRadians(this.phi))));
		setTranslateZ((int) (this.length*Math.cos(Math.toRadians(this.phi)))/10);
		
		if((z>0 && this.getTranslateZ()>0))
			this.setRadius(Math.max(RADIUS, this.getTranslateZ() + 1/this.getTranslateZ()));
	}
}

