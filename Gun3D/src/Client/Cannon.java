package Client;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

/**
 * This class provides an API compatible with Cannon with JavaFx and
 * control the cannon movement.
 * 
 * <br>
 * Extends: {@link ImageView}
 * 
 * @author Daniel Menahem 39676804
 * @author Michael Shvarts 301578878
 * @version 1.0
 */
public class Cannon extends ImageView{

	
	private static String PICTURE_URL = "/Client/Images/cannon_transp5.png";
	private int horzinotalRotation, verticalRotation;
	private Rotate rotateHorizontal, rotateVertical;
	public static final int ROTATION_DELTA = 2;
	public static final int MAX_HORIZONTAL_ROTATION = 60;
	public static final int MAX_VERTICAL_ROTATION = 30;
	public static final int BASE_ANGEL = 90;
	private double height, width;
	
	public Cannon(double width, double height){
		super(new Image(PICTURE_URL));
		this.horzinotalRotation = 0;
		this.verticalRotation = 0;
		this.height = height;
		this.width = width;
		
		paintCannon();
		setRotationAxises();
	}
	
	private void paintCannon(){
		this.setFitHeight(120);
		this.setFitWidth(120);
		this.setX(width/2 - this.getFitWidth()/2);
		this.setY(height - this.getFitHeight());
	}
	
	private void setRotationAxises(){
		this.rotateVertical = new Rotate(verticalRotation,width/2,height);
		this.rotateVertical.setAxis(Rotate.X_AXIS);
		this.rotateHorizontal = new Rotate(horzinotalRotation, width/2, height);
		this.rotateHorizontal.setAxis(Rotate.Z_AXIS);
		this.getTransforms().addAll(rotateVertical, rotateHorizontal);

	}
	
	public boolean rotateForward(){
		if(verticalRotation < MAX_VERTICAL_ROTATION){
			verticalRotation+=ROTATION_DELTA;
			rotateVertical.setAngle(verticalRotation);
			return true;
		}
		else
			return false;
	}
	
	public boolean rotateBackwards(){
		if(verticalRotation > 0){			
			verticalRotation-=ROTATION_DELTA;
			rotateVertical.setAngle(verticalRotation);
			return true;
		}
		else
			return false;
	}
	
	public boolean rotateLeft(){
		if(horzinotalRotation > -MAX_HORIZONTAL_ROTATION){
			horzinotalRotation-=ROTATION_DELTA;
			rotateHorizontal.setAngle(horzinotalRotation);
			return true;
		}
		else
			return false;
	}
	

	public boolean rotateRight(){
		if(horzinotalRotation < MAX_HORIZONTAL_ROTATION){
			horzinotalRotation+=ROTATION_DELTA;
			rotateHorizontal.setAngle(horzinotalRotation);
			return true;
		}
		else
			return false;
	}
	
	public int getTheta(){
		return BASE_ANGEL-horzinotalRotation;
	}
	
	public int getPhi(){
		return BASE_ANGEL-verticalRotation;
	}
	
	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}
	
	
}
