package Client;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

/**
 * This class provides an API compatible with Cannon, with JavaFx.
 * Creates and control the cannon movement.
 * 
 * <br>
 * Extends: {@link ImageView}
 * 
 * @author Daniel Menahem 39676804
 * @author Michael Shvarts 301578878
 * @version 1.0
 */
public class Cannon extends ImageView{

	/**
	 * The value of static final String {@code PICTURE_URL} is {@value}.
	 */
	private static String PICTURE_URL = "/Client/Images/cannon_transp5.png";
	
	/**
	 * The value of static final int {@code ROTATION_DELTA} is {@value}.
	 */
	public static final int ROTATION_DELTA = 2;
	
	/**
	 * The value of static final int {@code MAX_HORIZONTAL_ROTATION} is {@value}.
	 */
	public static final int MAX_HORIZONTAL_ROTATION = 60;
	
	/**
	 * The value of static final int {@code MAX_VERTICAL_ROTATION} is {@value}.
	 */
	public static final int MAX_VERTICAL_ROTATION = 30;
	
	/**
	 * The value of static final int {@code BASE_ANGEL} is {@value}.
	 */
	public static final int BASE_ANGEL = 90;
	
	/**
	 * The {@code horizontalRotation} is an integer. holds the horizontal rotation angel
	 */
	private int horzinotalRotation;
	
	/**
	 * The {@code verticalRotation} is an integer. holds the vertical rotation angel
	 */
	private int verticalRotation;
	
	/**
	 * The {@code  rotateHorizontal} is {@link Rotate}. holds the the horizontal rotation of the cannon
	 */
	private Rotate rotateHorizontal;
	
	/**
	 * The {@code  rotateVertical} is {@link Rotate}. holds the the vertical rotation of the cannon
	 */
	private Rotate rotateVertical;
	
	/**
	 * The {@code height} is a double. holds cannon height
	 */
	private double height;
	
	/**
	 * The {@code width} is a double. holds cannon width
	 */
	private double width;
	
	
	/**
	 * Constructs a cannon
	 * @param width the container width
	 * @param height the container height
	 * */
	public Cannon(double width, double height){
		super(new Image(PICTURE_URL));
		this.horzinotalRotation = 0;
		this.verticalRotation = 0;
		this.height = height;
		this.width = width;
		
		paintCannon();
		setRotationAxis();
	}
	
	
	/**
	 * Places the cannon on the container
	 * */
	private void paintCannon(){
		this.setFitHeight(120);
		this.setFitWidth(50);
		this.setX(width/2 - this.getFitWidth()/2);
		this.setY(height - this.getFitHeight());
	}
	
	
	/**
	 * Sets the cannon rotation axis;
	 * */
	private void setRotationAxis(){
		this.rotateVertical = new Rotate(verticalRotation,width/2,height);
		this.rotateVertical.setAxis(Rotate.X_AXIS);
		this.rotateHorizontal = new Rotate(horzinotalRotation, width/2, height);
		this.rotateHorizontal.setAxis(Rotate.Z_AXIS);
		this.getTransforms().addAll(rotateVertical, rotateHorizontal);
	}
	
	
	/**
	 * Rotates the cannon forward throughout the z axis
	 * @return true if the rotation is allowed (boolean)
	 * */
	public boolean rotateForward(){
		if(verticalRotation < MAX_VERTICAL_ROTATION){
			verticalRotation+=ROTATION_DELTA;
			rotateVertical.setAngle(verticalRotation);
			return true;
		}
		else
			return false;
	}
	
	
	/**
	 * Rotates the cannon backwards throughout the z axis
	 * @return true if the rotation is allowed (boolean)
	 * */
	public boolean rotateBackwards(){
		if(verticalRotation > 0){			
			verticalRotation-=ROTATION_DELTA;
			rotateVertical.setAngle(verticalRotation);
			return true;
		}
		else
			return false;
	}
	
	
	/**
	 * Rotates the cannon left throughout the x axis
	 * @return true if the rotation is allowed (boolean)
	 * */
	public boolean rotateLeft(){
		if(horzinotalRotation > -MAX_HORIZONTAL_ROTATION){
			horzinotalRotation-=ROTATION_DELTA;
			rotateHorizontal.setAngle(horzinotalRotation);
			return true;
		}
		else
			return false;
	}
	
	
	/**
	 * Rotates the cannon right throughout the x axis
	 * @return true if the rotation is allowed (boolean)
	 * */
	public boolean rotateRight(){
		if(horzinotalRotation < MAX_HORIZONTAL_ROTATION){
			horzinotalRotation+=ROTATION_DELTA;
			rotateHorizontal.setAngle(horzinotalRotation);
			return true;
		}
		else
			return false;
	}
	
	
	/**
	 * Returns the cannon rotation theta angle
	 * @return theta angel (int)
	 * */
	public int getTheta(){
		return BASE_ANGEL-horzinotalRotation;
	}
	
	
	/**
	 * Returns the cannon rotation phi angle
	 * @return phi angel (int)
	 * */
	public int getPhi(){
		return BASE_ANGEL-verticalRotation;
	}
	
	
	/**
	 * Returns the cannon height
	 * @return cannon height (double)
	 * */
	public double getHeight() {
		return height;
	}

	
	/**
	 * Returns the cannon height
	 * @return cannon height (double)
	 * */
	public double getWidth() {
		return width;
	}
}
