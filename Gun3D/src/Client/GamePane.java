package Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class GamePane extends Pane{
	
	public enum Difficulty {
		Low, Medium, High
	};
		
	private static final String TRAINING_BACKGROUND_URL = "/Client/Images/cartoon_desert.gif";
	private static final String MATCH_BACKGROUND_URL = "/Client/Images/desert.jpg";
	public final int [] TARGET_SIZES = {40, 30 , 20};
	public final int TARGET_Z_MAX = 50;
	public final double TARGET_Y_DEVIDER = 2.2;
	private final int ANIMATION_MILLIS = 20;
	private static HashMap <Boolean, String> backqrounds_url = new HashMap<>();
	
	private boolean isMatch;
	private Socket socket;
	private ObjectOutputStream toServer = null;
	private ObjectInputStream fromServer = null;
	
	private Difficulty difficulty;
	private int score = 0;
	private int hits = 0;
	private int shots = 0;
	private int gameID;
	private double width, height;
	
	private ArrayList<CannonShell> shells;
	private Target mainTarget, seconderyTarget;
	private Cannon cannon;
	private Timeline targetAnimation, shellsAnimation;
	
	private Label lblInfo;
	private BackgroundImage background;
	
	public GamePane(double width, double height){
		this.width = width;
		this.height = height;
		setSize();
		backqrounds_url.put(true, MATCH_BACKGROUND_URL);
		backqrounds_url.put(false, TRAINING_BACKGROUND_URL);
	}
	
	public void startTraining(Difficulty difficulty){
		this.isMatch = false;
		this.difficulty = difficulty;
		setBackground();
        addGameControls();
        setKeyboardEvents();
        getFocus();
	}
	
	private void setKeyboardEvents() {
		this.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.LEFT)
				cannon.rotateLeft();
			else if(e.getCode() == KeyCode.RIGHT)
				cannon.rotateRight();
			else if(e.getCode() == KeyCode.UP)
				cannon.rotateForward();
			else if(e.getCode() == KeyCode.DOWN)
				cannon.rotateBackwords();
			else if(e.getCode() == KeyCode.SPACE){				
				CannonShell shell = new CannonShell(cannon.getTheta(), 
						cannon.getPhi(), (int)cannon.getFitHeight() + 2, height, width);
				this.getChildren().add(shell);
				shells.add(shell);
			}
			getFocus();

		});
	}
	
	private void getFocus(){
		this.setFocusTraversable(true);
		this.requestFocus();
	}
	

	private void setBackground() {
		background = new BackgroundImage(new Image(backqrounds_url.get(isMatch),width,width,false,true),
		        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
		         BackgroundSize.DEFAULT);
		this.setBackground(new Background(background));
	}

	private void addGameControls() {
		shells = new ArrayList<>();
		cannon = new Cannon(width, height);
		this.getChildren().add(cannon);
		int targetSize = TARGET_SIZES[this.difficulty.ordinal()];
		mainTarget = new Target(targetSize, getRandom(width, targetSize), 
				getRandom(height/TARGET_Y_DEVIDER, 0), getRandom(TARGET_Z_MAX, targetSize)
				, this.getTranslateX(), width);
		//this.getChildren().addAll(cannon, mainTarget);
		this.getChildren().add(mainTarget);
		
		if(this.difficulty == Difficulty.Low){
			seconderyTarget =  new Target(targetSize, getRandom(width, targetSize), 
				getRandom(height/TARGET_Y_DEVIDER, 0), getRandom(TARGET_Z_MAX, targetSize)
					,height, width);
			this.getChildren().add(seconderyTarget);
		}
		
		else if(difficulty == Difficulty.High){
			targetAnimation = new Timeline(new KeyFrame(Duration.millis(ANIMATION_MILLIS), e1 -> mainTarget.moveTarget()));
			targetAnimation.setCycleCount(Timeline.INDEFINITE);
			targetAnimation.play();
		}
		
		setShellsAnimation();
	}
	
	private void setShellsAnimation(){
		shellsAnimation = new Timeline(new KeyFrame(Duration.millis(ANIMATION_MILLIS), e->{
			for(CannonShell c : shells){
				c.moveShell();
			}
		}));
		shellsAnimation.setCycleCount(Timeline.INDEFINITE);
		shellsAnimation.play();
	}

	private double getRandom(double max, int margin) {
		return(margin + Math.random()*(max - margin*2)); 
	}

	private void setSize(){
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        this.setWidth(width);
        this.setHeight(height);
	}
	
}
