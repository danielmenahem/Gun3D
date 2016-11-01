package Client;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import GameObjects.Difficulty;
import GameObjects.EventType;
import GameObjects.GameEvent;
import GameObjects.RotationDirection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.util.Duration;


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
	 * The value of static final {@code TRAINING_BACKGROUND_URL} is {@value}.
	 */
	private static final String TRAINING_BACKGROUND_URL = "/Client/Images/cartoon_desert.jpg";
	
	/**
	 * The value of static final {@code MATCH_BACKGROUND_URL} is {@value}.
	 */
	private static final String MATCH_BACKGROUND_URL = "/Client/Images/desert.jpg";
	
	/**
	 * {@code  EXPLOSION_URL} holds the explosions images URL's
	 */
	private static final String EXPLOSIONS_URL [] = {"/Client/Images/explosion.png", 
			"/Client/Images/explosion2.png", "/Client/Images/explosion3.png"};
	
	/**
	 * The value of static final {@code SHOT_SOUND_URL} is {@value}.
	 */
	private static final String SHOT_SOUND_URL = "src/Client/Sounds/shot.wav";
	
	/**
	 * The value of static final {@code HIT_SOUND_URL} is {@value}.
	 */
	private static final String HIT_SOUND_URL = "src/Client/Sounds/hit.wav";
	
	/**
	 * The value of static final {@code MOVE_SOUND_URL} is {@value}.
	 */
	private static final String MOVE_SOUND_URL = "src/Client/Sounds/cannon_move.wav";
	
	/**
	 * The value of static final {@code BACKGROUND_SOUND_URL} is {@value}.
	 */
	private static final String BACKGROUND_SOUND_URL = "src/Client/Sounds/wind.wav";
	
	/**
	 * The static final {@code TARGET_SIZES} holds the values 30, 25 ,20
	 */
	private static final int [] TARGET_SIZES = {30, 25 ,20};
	
	/**
	 * The static final {@code TARGET_Z_MAX} holds the values 50, 42, 35
	 */
	private static final int [] TARGET_Z_MAX = {50, 42, 35};
	
	/**
	 * The value of static final {@code TARGET_Y_DEVIDER} is {@value}.
	 */
	private static final double TARGET_Y_DEVIDER = 2.5;
	
	/**
	 * The value of static final {@code GAME_ANIMATION_MILLIS} is {@value}.
	 */
	private static final double GAME_ANIMATION_MILLIS = 5;
	
	/**
	 * The value of static final {@code EXPLOSION_ANIMATION_MILLIS} is {@value}.
	 */
	private static final double EXPLOSION_ANIMATION_MILLIS = 100;
	
	/**
	 * The value of static final {@code SOUND_EFFECTS_VOLUME} is {@value}.
	 */
	private static final double SOUND_EFFECTS_VOLUME = 0.4;	
	/**
	 * The value of static final {@code NUMBER_OF_EXPLOSION_CYCLES} is {@value}.
	 */
	private static final int NUMBER_OF_EXPLOSION_CYCLES = 9;
	
	
	/**
	 * The value of static final {@code HIT_MULTIPLIER} is {@value}.
	 */
	private static final int HIT_MULTIPLIER = 10; 
	
	/**
	 * The {@code backgroundsUrl} is an {@link HashMap}. holds the backgrounds URL's
	 *  */
	private static HashMap <Boolean, String> backgroundsUrl = new HashMap<>();
	
	/**
	 * The {@code rotationDirection} is an {@link HashMap}. holds the directions map
	 *  */
	private static HashMap<KeyCode, RotationDirection> rotationDirections = new HashMap<>();
	
	/**
	 * The {@code isMatch} holds the game mode
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
	 * The {@code score} holds the game score
	 * */
	private int score;

	/**
	 * The {@code hits} holds the number of hits
	 * */
	private int hits;
	
	/**
	 * The {@code misses} holds the number of misses
	 * */
	private int misses;
	
	/**
	 * The {@code gameID} holds the game ID
	 * */
	private int gameID;
	
	/**
	 * The {@code name} holds the player name
	 * */
	private String name;
	
	/**
	 * The {@code width} holds the panel width
	 * */
	private double width;
	
	/**
	 * The {@code height} holds the panel height
	 * */
	private double height;
	
	/**
	 * The {@code shells} is an {@link ArrayList}. holds the active cannon shells
	 * {@link CannonShell}
	 * */
	private ArrayList<CannonShell> shells;
	
	/**
	 * The {@code mainTarget} is a {@link Target}. holds the game main target
	 * */
	private Target mainTarget;
	
	/**
	 * The {@code mainTarget} is a {@link Target}. holds the game secondary target
	 * */
	private Target seconderyTarget;
	/**
	 * The {@code cannon} is a {@link Cannon}. holds the game cannon
	 * */
	private Cannon cannon;
	
	/**
	 * The {@code gameAnimation} is a {@link TimeLine}. holds the game engine
	 * */
	private Timeline gameAnimation;
	
	/**
	 * The {@code lblInfo} is a {@link Label}. holds the game score information
	 * */
	private Label lblInfo;
	
	/**
	 * The {@code background} is a {@link BackgroundImage}. holds the game background
	 * */
	private BackgroundImage background;
	
	/**
	 * The {@code shotPlayer} is a {@link MediaPlayer}. holds the sound for shot
	 * */
	private MediaPlayer shotPlayer;
	
	/**
	 * The {@code hitPlayer} is a {@link MediaPlayer}. holds the sound for hit
	 * */
	private MediaPlayer hitPlayer;
	
	/**
	 * The {@code movePlayer} is a {@link MediaPlayer}. holds the sound for cannon movement
	 * */
	private MediaPlayer movePlayer;
	
	/**
	 * The {@code movePlayer} is a {@link MediaPlayer}. holds the sound for cannon movement
	 * */
	private MediaPlayer backgroundPlayer;
	
	
	/** 
	 * Constructs new Game panel
	 * @param width the game panel width (double). Sets {@link GamePane#width}.
	 * @param height the game panel height (double). Sets {@link GamePane#height}.
	 * */
	public GamePane(double width, double height){
		setSize(width, height);
		initHashMapsValues();
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
		this.gameAnimation.stop();
		this.backgroundPlayer.stop();
		this.getChildren().removeAll(this.getChildren());
		calculateScore();
		return getScore();
	}
	
	
	/**
	 * Prepares and start a game
	 * */
	private void prepareAndStartGame(){
		resetGameStats();
		initMediaPlayers();
		setBackground();
        addGameControls();
        setKeyboardEvents();
        getFocus();
        
        gameAnimation = new Timeline(new KeyFrame(Duration.millis(GAME_ANIMATION_MILLIS), e ->{ 
        	game();
        	getFocus();
        }));
        
		gameAnimation.setCycleCount(Timeline.INDEFINITE);
		gameAnimation.play();
		playSound(backgroundPlayer, Timeline.INDEFINITE);
	}
	
	
	private void resetGameStats(){
		this.score = 0;
		this.hits = 0;
		this.misses = 0;
	}
	
	
	/**
	 * Initiates sound media players
	 * */
	private void initMediaPlayers(){
		this.shotPlayer = new MediaPlayer(new Media(new File(SHOT_SOUND_URL).toURI().toString()));
		this.shotPlayer.setVolume(SOUND_EFFECTS_VOLUME);
		this.hitPlayer = new MediaPlayer(new Media(new File(HIT_SOUND_URL).toURI().toString()));
		this.hitPlayer.setVolume(SOUND_EFFECTS_VOLUME);
		this.movePlayer = new MediaPlayer(new Media(new File(MOVE_SOUND_URL).toURI().toString()));
		this.movePlayer.setVolume(SOUND_EFFECTS_VOLUME);
		this.backgroundPlayer = new MediaPlayer(new Media(new File(BACKGROUND_SOUND_URL).toURI().toString()));
	}
		
	
	/**
	 * Performs one unit of the game logic
	 * */
	private void game() {
		if(difficulty == Difficulty.Hard){
			this.mainTarget.moveTarget();
		}
		
		for(int i=0; i<this.shells.size(); i++){
			CannonShell cs = this.shells.get(i);
			cs.moveShell();
			if(cs.isOutofBounds(TARGET_Z_MAX[difficulty.ordinal()]*3)){
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
	
	
	/**Shoots a cannon shell*/
	private void shoot(){
		CannonShell shell = cannon.shoot();
		this.getChildren().add(shell);
		shells.add(shell);
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
	 * @param target the impaired {@link Target}
	 * */
	private void hit(CannonShell cs, Target target) {
		playSound(hitPlayer,1);
		prepareAndShowExplosion(target);
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
			}
		}
		updateInfoText();
	}
	
	
	/**
	 * Prepares and start explosion animation
	 * @param target the impaired target*/
	private void prepareAndShowExplosion(Target target){
		ImageView imgExplusion = setExplosionImage();
		Timeline explosionAnimation = prepareExplosionAnimation(imgExplusion);
    	imgExplusion.setVisible(true);
    	imgExplusion.setX(target.getTranslateX() - target.getRadius() + target.getTranslateZ()/2);
    	imgExplusion.setY(target.getTranslateY() - target.getRadius() + target.getTranslateZ()/2);
    	imgExplusion.setFitWidth(Math.max(1,target.getRadius()*2 - target.getTranslateZ()));
    	imgExplusion.setFitHeight(Math.max(1,target.getRadius()*2 - target.getTranslateZ()));

		explosionAnimation.play();
		explosionAnimation.setOnFinished(e ->  {
			this.getChildren().remove(imgExplusion);
		});
	}
	

	/**
	 * Performs the actions which needed after a miss
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
			}
		}
		updateInfoText();
	}

	
	/**
	 * Calculates and updates the current game score
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
		if(mainTarget.checkForHit(cs))
			return mainTarget;
		
		if(difficulty == Difficulty.Easy){
			if(seconderyTarget.checkForHit(cs))
				return seconderyTarget;
		}
		return null;
	}

	
	/**
	 * Sets the game keyboard events
	 * */
	private void setKeyboardEvents() {
		this.setOnKeyPressed(e -> {
			if(cannon.rotate(rotationDirections.get(e.getCode())))
				playSound(movePlayer,1);

			else if(e.getCode() == KeyCode.SPACE){
				playSound(shotPlayer,1);
				shoot();
			}
		});
	}
	
	
	/**
	 * Initiates the values of {@link GamePane#backgroundsUrl} and {@link GamePane#rotationDirections}
	 * */
	private void initHashMapsValues(){
		backgroundsUrl.put(true, MATCH_BACKGROUND_URL);
		backgroundsUrl.put(false, TRAINING_BACKGROUND_URL);
		
		rotationDirections.put(KeyCode.LEFT, RotationDirection.Left);
		rotationDirections.put(KeyCode.RIGHT, RotationDirection.Right);
		rotationDirections.put(KeyCode.UP, RotationDirection.Forward);
		rotationDirections.put(KeyCode.DOWN, RotationDirection.Backward);
	}
	
	
	/**
	 * Plays a sound file
	 * @param sound the sound {@link MediaPlayer}
	 * @param cycles the number of cycles
	 * */
	private void playSound(MediaPlayer sound, int cycles){
        new Thread()
        {
            public void run() {
            	sound.seek(Duration.ZERO);
            	sound.play();
            	sound.setCycleCount(cycles);
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
	 * Configures explosion image
	 * */
	private ImageView setExplosionImage(){
		ImageView imgExplusion = new ImageView(new Image(EXPLOSIONS_URL[(int)getRandom(EXPLOSIONS_URL.length, 0)]));
		imgExplusion.setVisible(false);
		this.getChildren().add(imgExplusion);
		return imgExplusion;
	}
	
	
	/**
	 * Configures an explosion animation
	 * */
	private Timeline prepareExplosionAnimation(ImageView img){
		Timeline explosionAnimation = new Timeline(new KeyFrame(Duration.millis(EXPLOSION_ANIMATION_MILLIS), e -> {
		    img.setVisible(!img.isVisible());
		}));
		
		explosionAnimation.setCycleCount(NUMBER_OF_EXPLOSION_CYCLES);
		return explosionAnimation;
	}
	

	/**
	 * Sets the game background
	 * */
	private void setBackground() {
		background = new BackgroundImage(new Image(backgroundsUrl.get(isMatch),width,height,false,true),
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
	 * Sets the panel size.
	 * Sets {@link GamePane#width} and {@link GamePane#height}
	 * */
	private void setSize(double width, double height){
		this.width = width;
        this.setWidth(width);
		this.height = height;
        this.setHeight(height);
	}
	
	
	/**
	 * @return {@link GamePane#difficulty}
	 */
	public Difficulty getDifficulty() {
		return difficulty;
	}

	
	/**
	 * @return {@link GamePane#score}
	 */
	public int getScore() {
		return score;
	}

	
	/**
	 * @return {@link GamePane#hits}
	 */
	public int getHits() {
		return hits;
	}

	
	/**
	 * @return {@link GamePane#misses}
	 */
	public int getMisses() {
		return misses;
	}

	
	/**
	 * @return {@link GamePane#gameID}
	 */
	public int getGameID() {
		return gameID;
	}

	
	/**
	 * @return {@link GamePane#name}
	 */
	public String getName() {
		return name;
	}
	

	/**
	 * @return {@link GamePane#isMatch}
	 */
	public boolean isMatch() {
		return isMatch;
	}
}
