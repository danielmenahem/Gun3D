package Client;
import javafx.scene.text.Font;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import GameObjects.GameEvent;
import Utilities.Difficulty;
import Utilities.EventType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;


public class GamePane extends Pane{
	

	private static final String TRAINING_BACKGROUND_URL = "/Client/Images/cartoon_desert.jpg";
	private static final String MATCH_BACKGROUND_URL = "/Client/Images/desert.jpg";
	private static final String SHOT_SOUND_URL = "src/Client/Sounds/shot.wav";
	private static final String HIT_SOUND_URL = "src/Client/Sounds/hit.wav";
	private static final String MOVE_SOUND_URL = "src/Client/Sounds/cannon_move.wav";
	private static final int [] TARGET_SIZES = {40, 30 , 20};
	private static final int TARGET_Z_MAX = 35;
	private static final double TARGET_Y_DEVIDER = 2.2;
	private static final double ANIMATION_MILLIS = 5;
	private static final int HIT_MULTIPLIER = 10; 
	
	private static HashMap <Boolean, String> backgrounds_url = new HashMap<>();
	
	private boolean isMatch;
	private ObjectOutputStream toServer = null;
	
	private Difficulty difficulty;
	private int score, hits, misses;
	private int gameID;
	private String name;
	private double width, height;
	
	private ArrayList<CannonShell> shells;
	private Target mainTarget, seconderyTarget;
	private Cannon cannon;
	private Timeline gameAnimation;
	
	private Label lblInfo;
	private BackgroundImage background;
	private MediaPlayer shotPlayer, hitPlayer, movePlayer;
	
	
	//private CannonShell currentShell;


	/**Create new Game panel
	 * @param width the game panel width (double)
	 * @param height the game panel height (double)*/
	public GamePane(double width, double height){
		this.width = width;
		this.height = height;
		setSize();
		backgrounds_url.put(true, MATCH_BACKGROUND_URL);
		backgrounds_url.put(false, TRAINING_BACKGROUND_URL);
	}
	
	/**Start training game
	 * @param difficulty the requested game difficulty ({@link Difficulty} options: Low, Medium, High)
	 * */
	public void startTraining(Difficulty difficulty){
		this.isMatch = false;
		this.difficulty = difficulty;
		prepareAndStartGame();
	}
	
	/**Start an official match
	 * @param difficulty game difficulty ({@link Difficulty} options: Low, Medium, High)
	 * @param toServer {@link ObjectOutputStream}, the output stream, already initialized and connected to the server
	 * @param name the player name
	 * @param gameID the game ID given from server
	 * */
	public void startMatch(Difficulty difficulty, ObjectOutputStream toServer, String name, int gameId){
		this.isMatch = true;
		this.difficulty = difficulty;
		this.toServer = toServer;
		this.name = name;
		this.gameID = gameId;
		prepareAndStartGame();
	}
	
	/**Stops current game. prepares the panel to the next game
	 * @return the game final score*/
	public int stopGame(){
		if(isMatch){
			try {
				toServer.writeObject(new GameEvent(this.name, this.gameID, EventType.END_GAME, this.score));
			} catch (IOException e) {
			}
		}
		this.getChildren().removeAll(this.getChildren());
		calculateScore();
		return this.score;
	}
	
	private void prepareAndStartGame(){
		this.score = 0;
		this.hits = 0;
		this.misses = 0;
		initMediaPlayers();
		setBackground();
        addGameControls();
        setKeyboardEvents();
        getFocus();
        
        gameAnimation = new Timeline(new KeyFrame(Duration.millis(ANIMATION_MILLIS), e ->{ 
        	game();
        	getFocus();
        }));
        
		gameAnimation.setCycleCount(Timeline.INDEFINITE);
		gameAnimation.play();
	}
	
	private void initMediaPlayers(){
		this.shotPlayer = new MediaPlayer(new Media(new File(SHOT_SOUND_URL).toURI().toString()));
		this.hitPlayer = new MediaPlayer(new Media(new File(HIT_SOUND_URL).toURI().toString()));
		this.movePlayer = new MediaPlayer(new Media(new File(MOVE_SOUND_URL).toURI().toString()));
	}
		
	private void game() {
		if(difficulty == Difficulty.Hard){
			this.mainTarget.moveTarget();
		}
		for(int i=0; i<this.shells.size();i++){
			if (this.shells.get(i) instanceof CannonShell){
				CannonShell cs = (CannonShell)this.shells.get(i);
				cs.moveShell  ();
				if(isOutofBounds(cs)){
					miss(cs);
				}
				else{
					Target target = isHit(cs);
					if(target != null){
						hit(cs, target);
					}
				}
			}
		}
	}
	
	private void removeShell(CannonShell cs){
		this.getChildren().remove(cs);
		this.shells.remove(cs);
	}

	private void hit(CannonShell cs, Target target) {
		playSound(hitPlayer);
		this.hits++;
		removeShell(cs);
		calculateScore();
		int targetSize = TARGET_SIZES[difficulty.ordinal()];
		target.paintTarget(targetSize, getRandom(width, targetSize), 
				getRandom(height/TARGET_Y_DEVIDER, targetSize), getRandom(TARGET_Z_MAX, 0));
		if(isMatch){
			try {
				toServer.writeObject(new GameEvent(this.name, this.gameID, EventType.HIT, this.score));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		updateInfoText();
	}

	private void miss(CannonShell cs) {
		this.misses++;
		removeShell(cs);
		calculateScore();
		if(isMatch){
			try {
				toServer.writeObject(new GameEvent(this.name, this.gameID, EventType.MISS, this.score));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		updateInfoText();
	}

	private void calculateScore() {
		this.score = this.hits*(this.difficulty.ordinal()+1)*HIT_MULTIPLIER - this.misses;
	}

	private Target isHit(CannonShell cs) {
		if(checkForHit(cs, mainTarget))
			return mainTarget;
		
		if(difficulty == Difficulty.Easy){
			if(checkForHit(cs, seconderyTarget))
				return seconderyTarget;
		}
		return null;
	}
	
	private boolean checkForHit(CannonShell cs, Target target){
		return Math.sqrt(Math.pow((target.getTranslateX() - cs.getTranslateX()), 2)
				+ Math.pow((target.getTranslateY() - cs.getTranslateY()), 2) 
				+ Math.pow((target.getTranslateZ() - cs.getTranslateZ()), 2)) <= (TARGET_SIZES[this.difficulty.ordinal()]);
	}

	private boolean isOutofBounds(CannonShell shell) {
		
		return ((shell.getTranslateX() > (this.getWidth()+this.getTranslateX())) || 
				(shell.getTranslateX() < 0) || (shell.getTranslateY() < 0) || 
				(shell.getTranslateZ() > TARGET_Z_MAX*5) );
	}

	private void setKeyboardEvents() {
		this.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.LEFT){
				if(cannon.rotateLeft())
					playSound(movePlayer);
			}
			else if(e.getCode() == KeyCode.RIGHT){
				if(cannon.rotateRight())
					playSound(movePlayer);
			}
			else if(e.getCode() == KeyCode.UP){
				if(cannon.rotateForward())
					playSound(movePlayer);
			}
			else if(e.getCode() == KeyCode.DOWN){
				if(cannon.rotateBackwards())
					playSound(movePlayer);
			}
			else if(e.getCode() == KeyCode.SPACE){
				playSound(shotPlayer);
				CannonShell shell = new CannonShell(cannon.getTheta(), 
						cannon.getPhi(), (int)cannon.getFitHeight() + 2, height, width);
				this.getChildren().add(shell);
				shells.add(shell);				
			}
		});
	}
	
	private void playSound(MediaPlayer sound){
        new Thread()
        {
            public void run() {
            	sound.seek(Duration.ZERO);
            	sound.play();
            }
        }.start();
	}
	
	private void getFocus(){
		this.setFocusTraversable(true);
		this.requestFocus();
	}
	

	private void setBackground() {
		background = new BackgroundImage(new Image(backgrounds_url.get(isMatch),width,height,false,true),
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
				getRandom(height/TARGET_Y_DEVIDER, targetSize), getRandom(TARGET_Z_MAX, 0)
				, this.getTranslateX(), width + this.getTranslateX());
		this.getChildren().add(mainTarget);
		
		if(this.difficulty == Difficulty.Easy){
			seconderyTarget =  new Target(targetSize, getRandom(width, targetSize), 
				getRandom(height/TARGET_Y_DEVIDER, targetSize), getRandom(TARGET_Z_MAX, 0)
					,this.getTranslateX(), width + this.getTranslateX());
			this.getChildren().add(seconderyTarget);
		}
		
		setLblInfo();
	}
	
	private void setLblInfo(){
		lblInfo = new Label();
		this.getChildren().add(lblInfo);
		lblInfo.setTranslateX(5);
		lblInfo.setTranslateY(5);
		lblInfo.setStyle("-fx-font-weight: bold;-fx-text-fill: blue;");
		lblInfo.setFont(Font.font ("Cooper Black", 14));
		updateInfoText();
	}
	
	private void updateInfoText(){
		Platform.runLater(()->{
			lblInfo.setText("Hits: " + this.hits + " Misses: " + this.misses +"\nScore: " + this.score);
		});
	}
	

	private double getRandom(double max, int margin) {
		return(margin + Math.random()*(max - margin*2)); 
	}

	private void setSize(){
        this.setWidth(width);
        this.setHeight(height);
	}
	
}
