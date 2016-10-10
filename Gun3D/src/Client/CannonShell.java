package Client;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.paint.PhongMaterial;

public class CannonShell extends Sphere{
	private PhongMaterial redMaterial;
	private int theta, phi, length;
	private double height, width;
	
	private static final int RADIUS = 8;
	public static final int INCREASMENT = 2;
	
	public CannonShell(int theta, int phi, int length, double height, double width){	
		this.theta = theta;
		this.phi = phi;
		this.length = length;
		this.height = height;
		this.width = width;
		paintCannonShell();

	}
	
	private void paintCannonShell(){
        redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.GREY);
        redMaterial.setSpecularColor(Color.BLACK);
		//shell = new Sphere();
		setRadius(RADIUS);
		moveShell();
		setDrawMode(DrawMode.FILL);
        setMaterial(redMaterial);
	}
	
	
	public void moveShell() {
		this.length +=INCREASMENT;
		setTranslateX((int) (this.length*Math.cos(Math.toRadians(this.theta))*Math.sin(Math.toRadians(this.phi)) + this.width/2 ));
		setTranslateY((int) (this.height - this.length*Math.sin(Math.toRadians(this.theta))*Math.sin(Math.toRadians(this.phi))));
		setTranslateZ((int) (this.length*Math.cos(Math.toRadians(this.phi))));
		
	}
}
