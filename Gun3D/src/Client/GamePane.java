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
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

/**
 * This class provides an API compatible with GamePane, with JavaFx.
 * Creates and engines a cannon game
 * 
 * <br>
 * Extends: {@link Pane}
 * 
 * @author Daniel Menahem 39676804
 * @author Michael Shvarts 301578878
 * @version 1.0
 */
public class GamePane extends Pane{
	
	/**
	 * The value of static final String {@code TRAINING_BACKGROUND_URL} is {@value}.
	 */
	private static final String TRAINING_BACKGROUND_URL = "/Client/Images/cartoon_desert.jpg";
	
	/**
	 * The value of static final String {@code MATCH_BACKGROUND_URL} is {@value}.
	 */
	private static final String MATCH_BACKGROUND_URL = "/Client/Images/desert.jpg";
	
	/**
	 * The value of static final String {@code SHOT_SOUND_URL} is {@value}.
	 */
	private static final String SHOT_SOUND_URL = "src/Client/Sounds/shot.wav";
	
	/**
	 * The value of static final String {@code HIT_SOUND_URL} is {@value}.
	 */
	private static final String HIT_SOUND_URL = "src/Client/Sounds/hit.wav";
	
	/**
	 * The value of static final String {@code MOVE_SOUND_URL} is {@value}.
	 */
	private static final String MOVE_SOUND_URL = "src/Client/Sounds/cannon_move.wav";
	
	/**
	 * The static final int[] {@code TARGET_SIZES} holds the values 30, 25 ,20
	 */
	private static final int [] TARGET_SIZES = {30, 25 ,20};
	
	/**
	 * The static final int[] {@code TARGET_Z_MAX} holds the values 45, 40, 35
	 */
	private static final int [] TARGET_Z_MAX = {45, 40, 35};
	
	/**
	 * The value of static final double {@code TARGET_Y_DEVIDER} is {@value}.
	 */
	private static final double TARGET_Y_DEVIDER = 2.5;
	
	/**
	 * The value of static final double {@code ANIMATION_MILLIS} is {@value}.
	 */
	private static final double ANIMATION_MILLIS = 5;
	
	/**
	 * The value of static final int {@code HIT_MULTIPLIER} is {@value}.
	 */
	private static final int HIT_MULTIPLIER = 10; 
	
	/**
	 * The {@code backgrounds_url} is an {@link HashMap}
	 *  */
	private static HashMap <Boolean, String> backgrounds_url = new HashMap<>();
	
	/**
	 * The {@code isMatch} is a boolean. holds the game mode
	 * */
	private boolean isMatch;
	
	/**
	 * The {@code isMatch} is a {@link ObjectOutputStream}. holds the output stream to the server
	 * */
	private ObjectOutputStream toServer = null;
	
	/**
	 * The {@code difficulty} is a {@link Difficulty}. holds the game difficulty mode
	 * */
	private Difficulty difficulty;
	
	/**
	 * The {@code score} is an int. holds the game score
	 * */
	private int score;
	
	/**
	 * The {@code hits} is an int. holds the number of hits
	 * */
	private int hits;
	
	/**
	 * The {@code misses} is an int. holds the number of misses
	 * */
	private int misses;
	
	/**
	 * The {@code gameID} is an int. holds the game ID
	 * */
	private int gameID;
	
	/**
	 * The {@code name} is a String. holds the player name
	 * */
	private String name;
	
	/**
	 * The {@code width} is double. holds the panel width
	 * */
	private double width;
	
	/**
	 * The {@code height} is double. holds the panel height
	 * */
	private double height;
	
	/**
	 * The {@code shells} is an {@link ArrayList}. holds the active cannon shells
	 * {@link CannonShell}
	 * */
	private ArrayList<CannonShell> shells;
	
	/**
	 * The {@code mainTarget} is {@link Target}. holds the game main target
	 * */
	private Target mainTarget;
	
	/**
	 * The {@code mainTarget} is {@link Target}. holds the game secondary target
	 * */
	private Target seconderyTarget;
	/**
	 * The {@code cannon} is {@link Cannon}. holds the game cannon
	 * */
	private Cannon cannon;
	
	/**
	 * The {@code gameAnimation} is {@link TimeLine}. holds the game engine
	 * */
	private Timeline gameAnimation;
	
	/**
	 * The {@code lblInfo} is {@link Label}. holds the game score information
	 * */
	private Label lblInfo;
	
	/**
	 * The {@code background} is {@link BackgroundImage}. holds the game background
	 * */
	private BackgroundImage background;
	
	/**
	 * The {@code shotPlayer} is {@link MediaPlayer}. holds the sound for shot
	 * */
	private MediaPlayer shotPlayer;
	
	/**
	 * The {@code hitPlayer} is {@link MediaPlayer}. holds the sound for hit
	 * */
	private MediaPlayer hitPlayer;
	
	/**
	 * The {@code movePlayer} is {@link MediaPlayer}. holds the sound for cannon movement
	 * */
	private MediaPlayer movePlayer;
	

	/** 
	 * Constructs new Game panel
	 * @param width the game panel width (double)
	 * @param height the game panel height (double)*/
	public GamePane(double width, double height){
		this.width = width;
		this.height = height;
		setSize();
		backgrounds_url.put(true, MATCH_BACKGROUND_URL);
		backgrounds_url.put(false, TRAINING_BACKGROUND_URL);
	}
	
	
	/**
	 * Start a training game
	 * @param difficulty the requested game difficulty ({@link Difficulty} options: Low, Medium, High)
	 * */
	public void startTraining(Difficulty difficulty){
		this.isMatch = false;
		this.difficulty = difficulty;
		prepareAndStartGame();
	}
	
	
	/**
	 * Start an online match
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
	
	
	/**
	 * Stops the current game. prepares the panel to the next game
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
	
	
	/**
	 * Prepares and start a game
	 * */
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
	
	
	/**
	 * Initiates sound media players
	 * */
	private void initMediaPlayers(){
		this.shotPlayer = new MediaPlayer(new Media(new File(SHOT_SOUND_URL).toURI().toString()));
		this.hitPlayer = new MediaPlayer(new Media(new File(HIT_SOUND_URL).toURI().toString()));
		this.movePlayer = new MediaPlayer(new Media(new File(MOVE_SOUND_URL).toURI().toString()));
	}
		
	
	/**
	 * Performs one unit of the game logic
	 * */
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
	
	
	/**
	 * Removes cannon shell from the game
	 * @param cs the {@link CannonShell} to remove
	 * */
	private void removeShell(CannonShell cs){
		this.getChildren().remove(cs);
		this.shells.remove(cs);
	}

	
	/**
	 * Performs the action needed after a hit
	 * @param cs the hitting {@link CannonShell}
	 * @param target the impaired target
	 * */
	private void hit(CannonShell cs, Target target) {
		playSound(hitPlayer);
		this.hits++;
		removeShell(cs);
		calculateScore();
		int targetSize = TARGET_SIZES[difficulty.ordinal()];
		target.paintTarget(targetSize, getRandom(width, targetSize), 
				getRandom(height/TARGET_Y_DEVIDER, targetSize), getRandom(TARGET_Z_MAX[difficulty.ordinal()], 0));
		if(isMatch){
			try {
				toServer.writeObject(new GameEvent(this.name, this.gameID, EventType.HIT, this.score));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		updateInfoText();
	}

	
	/**
	 * Performs the action needed after a miss
	 * @param cs the missing {@link CannonShell}
	 * */
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

	
	/**
	 * Calculates the current game score
	 * */
	private void calculateScore() {
		this.score = this.hits*(this.difficulty.ordinal()+1)*HIT_MULTIPLIER - this.misses;
	}

	
	/**
	 * Checks if the cannon shell hit a target
	 * @param cs the reviewed {@link CannonShell}
	 * @return the impaired {@link Target}. null if no hit
	 * */
	private Target isHit(CannonShell cs) {
		if(checkForHit(cs, mainTarget))
			return mainTarget;
		
		if(difficulty == Difficulty.Easy){
			if(checkForHit(cs, seconderyTarget))
				return seconderyTarget;
		}
		return null;
	}
	
	
	/**
	 * Checks if the cannon shell hit the target
	 * @param cs the reviewed {@link CannonShell}
	 * @param target the reviewed {@link Target}
	 * @return true if hit (boolean)
	 * */
	private boolean checkForHit(CannonShell cs, Target target){
		return Math.sqrt(Math.pow((target.getTranslateX() - cs.getTranslateX()), 2)
				+ Math.pow((target.getTranslateY() - cs.getTranslateY()), 2) 
				+ Math.pow((target.getTranslateZ() - cs.getTranslateZ()), 2)) <= (TARGET_SIZES[this.difficulty.ordinal()]);
	}

	
	/**
	 * Checks if the cannon shell hit out of bounds
	 * @param shell the reviewed {@link CannonShell}
	 * @return true if out of bounds (boolean)
	 * */
	private boolean isOutofBounds(CannonShell shell) {
		
		return ((shell.getTranslateX() + CannonShell.RADIUS> (this.getWidth()+this.getTranslateX())) || 
				(shell.getTranslateX() - CannonShell.RADIUS< 0) || (shell.getTranslateY() - CannonShell.RADIUS*2 < 0) || 
				(shell.getTranslateZ() > TARGET_Z_MAX[difficulty.ordinal()]*3) );
	}

	
	/**
	 * Sets the game keyboard events
	 * */
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
	
	
	/**Plays a sound file
	 * @param sound the sound {@link MediaPlayer}*/
	private void playSound(MediaPlayer sound){
        new Thread()
        {
            public void run() {
            	sound.seek(Duration.ZERO);
            	sound.play();
            }
        }.start();
	}
	
	
	/**
	 * Requests the focus to the panel
	 * */
	private void getFocus(){
		this.setFocusTraversable(true);
		this.requestFocus();
	}
	

	/**
	 * Sets the game background
	 * */
	private void setBackground() {
		background = new BackgroundImage(new Image(backgrounds_url.get(isMatch),width,height,false,true),
		        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
		         BackgroundSize.DEFAULT);
		this.setBackground(new Background(background));
	}

	
	/**
	 * Adds and paints the game objects
	 * */
	private void addGameControls() {
		shells = new ArrayList<>();
		cannon = new Cannon(width, height);
		this.getChildren().add(cannon);
		int targetSize = TARGET_SIZES[this.difficulty.ordinal()];
		mainTarget = new Target(targetSize, getRandom(width, targetSize), 
				getRandom(height/TARGET_Y_DEVIDER, targetSize), getRandom(TARGET_Z_MAX[difficulty.ordinal()], 0)
				, this.getTranslateX(), width + this.getTranslateX());
		this.getChildren().add(mainTarget);
		
		if(this.difficulty == Difficulty.Easy){
			seconderyTarget =  new Target(targetSize, getRandom(width, targetSize), 
				getRandom(height/TARGET_Y_DEVIDER, targetSize), getRandom(TARGET_Z_MAX[difficulty.ordinal()], 0)
					,this.getTranslateX(), width + this.getTranslateX());
			this.getChildren().add(seconderyTarget);
		}
		
		setLblInfo();
	}
	
	
	/**
	 * Paints of the info {@link Label}
	 * */
	private void setLblInfo(){
		lblInfo = new Label();
		this.getChildren().add(lblInfo);
		lblInfo.setTranslateX(5);
		lblInfo.setTranslateY(5);
		lblInfo.setStyle("-fx-font-weight: bold;-fx-text-fill: blue;");
		lblInfo.setFont(Font.font ("Cooper Black", 14));
		updateInfoText();
	}
	
	
	/**
	 * Updates the text of the info {@link Label}
	 * */
	private void updateInfoText(){
		Platform.runLater(()->{
			lblInfo.setText("Hits: " + this.hits + " Misses: " + this.misses +"\nScore: " + this.score);
		});
	}
	
	
	/**Randomize a number
	 * @param max the number maximum value (double)
	 * @param margin the number minimum value (int)
	 * @return random number (double)*/
	private double getRandom(double max, int margin) {
		return(margin + Math.random()*(max - margin*2)); 
	}

	
	/**
	 * Sets the panel size
	 * */
	private void setSize(){
        this.setWidth(width);
        this.setHeight(height);
	}
}
