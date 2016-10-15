package Client;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.paint.PhongMaterial;

public class CannonShell extends Sphere{
	private PhongMaterial material;
	private int theta, phi, length;
	private double height, width;
	
	public static final int RADIUS = 8;
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
        material = new PhongMaterial();
        material.setDiffuseColor(Color.GREY);
        material.setSpecularColor(Color.BLACK);
		setRadius(RADIUS);
		moveShell();
		setDrawMode(DrawMode.FILL);
        setMaterial(material);
	}
	
	
	public void moveShell() {
		this.length +=INCREASMENT;
		double z = this.getTranslateZ();
		setTranslateX((int) (this.length*Math.cos(Math.toRadians(this.theta))*Math.sin(Math.toRadians(this.phi)) + this.width/2 ));
		setTranslateY((int) (this.height - this.length*Math.sin(Math.toRadians(this.theta))*Math.sin(Math.toRadians(this.phi))));
		setTranslateZ((int) (this.length*Math.cos(Math.toRadians(this.phi)))/10);
		
		if((z>0 && this.getTranslateZ()>0))
			this.setRadius(Math.max(RADIUS, this.getTranslateZ() + 0.1));
		
	}
}
