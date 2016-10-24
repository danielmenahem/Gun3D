package Client;

import GameObjects.RotationDirection;
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
	 * The value of static final {@code PICTURE_URL} is {@value}.
	 */
	private static final String PICTURE_URL = "/Client/Images/cannon_transp5.png";
	
	/**
	 * The value of static final {@code ROTATION_DELTA} is {@value}.
	 */
	public static final int ROTATION_DELTA = 2;
	
	/**
	 * The value of static final {@code MAX_HORIZONTAL_ROTATION} is {@value}.
	 */
	public static final int MAX_HORIZONTAL_ROTATION = 60;
	
	/**
	 * The value of static final {@code MAX_VERTICAL_ROTATION} is {@value}.
	 */
	public static final int MAX_VERTICAL_ROTATION = 30;
	
	/**
	 * The value of static final {@code BASE_ANGEL} is {@value}.
	 */
	public static final int BASE_ANGEL = 90;
	
	/**
	 * The value of static final {@code CANNON_WIDTH} is {@value}.
	 */
	public static final int CANNON_WIDTH = 50;
	
	/**
	 * The value of static final {@code CANNON_HEIGHT} is {@value}.
	 */
	public static final int CANNON_HEIGHT = 120;

	/**
	 * The {@code horizontalRotation} holds the horizontal rotation angel
	 */
	private int horzinotalRotation;
	
	/**
	 * The {@code verticalRotation} holds the vertical rotation angel
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
	 * The {@code height} holds container height
	 */
	private double height;
	
	/**
	 * The {@code width} holds container width
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
		this.setFitHeight(CANNON_HEIGHT);
		this.setFitWidth(CANNON_WIDTH);
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
	 * Rotates the cannon
	 * @param direction the rotation direction {@link RotationDirection}
	 * @return true if the rotation is allowed (boolean)
	 * */
	public boolean rotate(RotationDirection direction){
		if(direction == RotationDirection.Left)
			return rotateLeft();
		else if(direction == RotationDirection.Right)
			return rotateRight();
		else if(direction == RotationDirection.Forward)
			return rotateForward();
		else if(direction == RotationDirection.Backward)
			return rotateBackwards();
		
		return false;
	}
	
	
	/**
	 * Rotates the cannon forward throughout the z axis
	 * @return true if the rotation is allowed (boolean)
	 * */
	private boolean rotateForward(){
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
	private boolean rotateBackwards(){
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
	private boolean rotateLeft(){
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
	private boolean rotateRight(){
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
	
	
	/**Shoots a cannon shell based on the cannon rotation angles*/
	public CannonShell shoot(){
		CannonShell shell = new CannonShell(this.getTheta(), 
				this.getPhi(), (int)this.getFitHeight() + 2, this.height, this.width);
		return shell;	
	}
}
