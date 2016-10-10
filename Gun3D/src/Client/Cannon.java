package Client;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;


public class Cannon extends ImageView{

	private int horzinotalRotation, verticalRotation;
	private Rotate rotateHorizontal, rotateVertical;
	public static final int ROTATION_DELTA = 2;
	public static final int MAX_HORIZONTAL_ROTATION = 50;
	public static final int MAX_VERTICAL_ROTATION = 30;
	public static final int BASE_ANGEL = 90;
	private double height, width;
	
	public Cannon(double width, double height){
		super(new Image("/Client/Images/cannon.jpg"));
		this.horzinotalRotation = 0;
		this.verticalRotation = 0;
		this.height = height;
		this.width = width;
		
		paintCannon();
		setRotationAxises();
	}
	
	private void paintCannon(){
		this.setFitHeight(120);
		this.setFitWidth(65);
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
	
	public void rotateForward(){
		if(verticalRotation < MAX_VERTICAL_ROTATION){
			verticalRotation+=ROTATION_DELTA;
			rotateVertical.setAngle(verticalRotation);
		}
	}
	
	public void rotateBackwords(){
		if(verticalRotation > 0){			
			verticalRotation-=ROTATION_DELTA;
			rotateVertical.setAngle(verticalRotation);
		}
	}
	
	public void rotateLeft(){
		if(horzinotalRotation > -MAX_HORIZONTAL_ROTATION){
			horzinotalRotation-=ROTATION_DELTA;
			rotateHorizontal.setAngle(horzinotalRotation);
		}
	}
	
	public void rotateRight(){
		if(horzinotalRotation < MAX_HORIZONTAL_ROTATION){
			horzinotalRotation+=ROTATION_DELTA;
			rotateHorizontal.setAngle(horzinotalRotation);
		}
	}
	
	public int getTheta(){
		return BASE_ANGEL-horzinotalRotation;
	}
	
	public int getPhi(){
		return BASE_ANGEL-verticalRotation;
	}
	
	
}
