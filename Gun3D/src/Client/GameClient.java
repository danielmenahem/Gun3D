package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import GameObjects.Difficulty;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * A GUI stage to display DB queries
 * 
 * @author Daniel Menahem 39676804
 * @author Michael Shvarts 301578878
 * @version 1.0
 *
 *          JavaDoc made under the assumption that the HTML generated doc will
 *          include private fields. Under different circumstances, the private
 *          attributes would be documented in the public getters, and the rest
 *          would link there. Private attributes documentation was made so
 *          everything will be documented.
 */
public class GameClient extends Application {
	/**
	 * Background image path for the training or game screen.
	 * {@code TRAINING_OR_GAME_BACKGROUND} is {@value}
	 */
	private static final String TRAINING_OR_GAME_BACKGROUND = "/Client/Images/first_screen.jpg";
	/**
	 * Background image path for the game settings screen.
	 * {@code GAME_SETTINGS_BACKGROUND} is {@value}
	 */
	private static final String GAME_SETTINGS_BACKGROUND = "/Client/Images/second_screen.jpg";
	/**
	 * According to assignment description, user doesn't choose training
	 * difficulty. {@code TRAINING_DIFFICULTY} is {@value}.
	 */
	private static final Difficulty TRAINING_DIFFICULTY = Difficulty.Easy;
	/**
	 * Denotes the size of the main buttons. {@code SIZE_OF_BUTTONS} is {@value}
	 */
	private static final int SIZE_OF_BUTTONS = 150;
	/**
	 * Denotes the size of padding. {@code SIZE_OF_PADDING} is {@value}
	 */
	private static final int SIZE_OF_PADDING = 30;
	/**
	 * Denotes the default length of a game. {@code GAME_LENGTH} is
	 * {@value} seconds
	 */
	private static final int GAME_LENGTH = 120;
	/**
	 * Holds the server address. {@code HOST} is {@value}
	 */
	private static final String HOST = "localhost";
	/**
	 * Connects to the server.
	 */
	private Socket socket;
	/**
	 * Sends game events to the server.
	 */
	private ObjectOutputStream toServer;
	/**
	 * Receives game ID from server.
	 */
	private ObjectInputStream fromServer;
	/**
	 * Displays server connection errors.
	 */
	private Label lblConnectionError;

	/**
	 * Width of screen to send to the game pane.
	 */
	private double gameWidth;
	/**
	 * Height of screen to sent to the game pane.
	 */
	private double gameHeight;

	/**
	 * Displays the game
	 */
	private GamePane pnlGame;
	/**
	 * Primary stage for the game client
	 */
	private Stage primaryStage;
	/**
	 * Performs one second ticks to display a seconds timer in the game
	 */
	private Timeline timer;
	/**
	 * Holds timer ticks
	 */
	private int secondsCounter;
	/**
	 * Game ID received from the server
	 */
	private int gameID;

	/**
	 * Assigns the primary stage to the class property and gets the screen sizes
	 * 
	 * @param primaryStage
	 *            primary stage of the application
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		gameWidth = primaryScreenBounds.getWidth();
		gameHeight = primaryScreenBounds.getHeight();

		setTrainingOrGameScene();
	}

	/**
	 * Sets the scene that allows choice between training and game modes.
	 */
	private void setTrainingOrGameScene() {
		GridPane gpTraingOrGame = new GridPane();
		Button btnTraining = new Button("Training Mode");
		btnTraining.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-min-height: " + SIZE_OF_BUTTONS + "px; " + "-fx-max-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-max-height: " + SIZE_OF_BUTTONS + "px;" + "-fx-background-color: #E0FFFF");

		btnTraining.setOnAction(e -> {
			setTrainingScene();
		});
		Button btnGame = new Button("Game Mode");
		btnGame.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-min-height: " + SIZE_OF_BUTTONS + "px; " + "-fx-max-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-max-height: " + SIZE_OF_BUTTONS + "px;" + "-fx-background-color: #4682B4");

		btnGame.setOnAction(e -> {
			setGameSettingsScene();
		});
		gpTraingOrGame.add(btnTraining, 0, 0);
		gpTraingOrGame.add(btnGame, 1, 0);
		gpTraingOrGame.setHgap(SIZE_OF_PADDING);
		gpTraingOrGame.setPadding(new Insets(SIZE_OF_PADDING));
		gpTraingOrGame.setBackground(null);

		Scene scene = new Scene(gpTraingOrGame, SIZE_OF_BUTTONS * 2 + SIZE_OF_PADDING * 3,
				SIZE_OF_BUTTONS + SIZE_OF_PADDING * 2, true);

		gpTraingOrGame.setBackground(new Background(new BackgroundImage(
				new Image(TRAINING_OR_GAME_BACKGROUND, gpTraingOrGame.getWidth(), gpTraingOrGame.getHeight(), false,
						true),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT)));

		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("Gun3D");
		primaryStage.setAlwaysOnTop(true);
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	/**
	 * Sets the scene that shows the game in training mode (unlimited timer and
	 * no records).
	 */
	private void setTrainingScene() {
		GamePane gp = new GamePane(gameWidth, gameHeight);
		addTimerToGame(gp, false);
		Scene scene = new Scene(gp, gameWidth, gameHeight, true);
		scene.setFill(null);
		gp.startTraining(TRAINING_DIFFICULTY);

		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				timer.stop();
				setTrainingOrGameScene();
				event.consume();
			}
		});
	}

	/**
	 * Sets the scene that allows player's name insertion and choice of game
	 * difficulty.
	 */
	private void setGameSettingsScene() {
		GridPane gpGameSettings = new GridPane();
		Label lblName = new Label("Name: ");
		lblName.setStyle("-fx-text-fill: white");
		lblConnectionError = new Label();
		lblConnectionError.setStyle("-fx-text-fill: red");
		TextField tfName = new TextField();
		Button btnEasy = new Button("Easy");
		btnEasy.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-min-height: " + SIZE_OF_BUTTONS + "px; " + "-fx-max-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-max-height: " + SIZE_OF_BUTTONS + "px;" + "-fx-background-color: #008000");
		btnEasy.setOnAction(e -> {
			setGameScene(Difficulty.Easy, tfName.getText());
		});

		Button btnMedium = new Button("Medium");
		btnMedium.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-min-height: " + SIZE_OF_BUTTONS + "px; " + "-fx-max-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-max-height: " + SIZE_OF_BUTTONS + "px;" + "-fx-background-color: #FF8C00");
		btnMedium.setOnAction(e -> {
			setGameScene(Difficulty.Medium, tfName.getText());
		});

		Button btnHard = new Button("Hard");
		btnHard.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-min-height: " + SIZE_OF_BUTTONS + "px; " + "-fx-max-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-max-height: " + SIZE_OF_BUTTONS + "px;" + "-fx-background-color: #800000");
		btnHard.setOnAction(e -> {
			setGameScene(Difficulty.Hard, tfName.getText());
		});

		gpGameSettings.add(lblName, 0, 0);
		gpGameSettings.add(tfName, 1, 0);
		gpGameSettings.add(lblConnectionError, 2, 0);
		gpGameSettings.add(btnEasy, 0, 1);
		gpGameSettings.add(btnMedium, 1, 1);
		gpGameSettings.add(btnHard, 2, 1);

		gpGameSettings.setHgap(SIZE_OF_PADDING);
		gpGameSettings.setVgap(SIZE_OF_PADDING);
		gpGameSettings.setPadding(new Insets(SIZE_OF_PADDING));
		gpGameSettings.setBackground(null);

		Scene scene = new Scene(gpGameSettings, SIZE_OF_BUTTONS * 3 + SIZE_OF_PADDING * 4,
				SIZE_OF_BUTTONS + SIZE_OF_PADDING * 5, true);

		gpGameSettings
				.setBackground(new Background(new BackgroundImage(
						new Image(GAME_SETTINGS_BACKGROUND, gpGameSettings.getWidth(), gpGameSettings.getHeight(),
								false, true),
						BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
						BackgroundSize.DEFAULT)));

		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				setTrainingOrGameScene();
				event.consume();
			}
		});

	}

	/**
	 * Sets the scene to display the game pane in game mode (2 minutes limit,
	 * records events).
	 * 
	 * @param difficulty
	 *            the game's {@link Difficulty}
	 * @param nameText
	 *            text inserted by the user as the player's ID
	 */
	private void setGameScene(Difficulty difficulty, String nameText) {
		String playerID = nameText.equals("") ? "Annonymous" : nameText;

		try {
			connectToServer();
		} catch (ClassNotFoundException | IOException e) {
			lblConnectionError.setText("Server Error");
			return;
		}

		pnlGame = new GamePane(gameWidth, gameHeight);
		addTimerToGame(pnlGame, true);
		pnlGame.startMatch(difficulty, toServer, playerID, gameID);

		Scene scene = new Scene(pnlGame, gameWidth, gameHeight, true);
		scene.setFill(null);

		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				endGame();
				event.consume();
			}
		});
	}

	/**
	 * Ends game mode.
	 */
	private void endGame() {
		pnlGame.stopGame();
		timer.stop();
		closeConnection();
		setGameEndedScene();
	}

	/**
	 * Disconnects from server.
	 */
	private void closeConnection() {
		try {
			toServer.close();
			fromServer.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets the scene to display game statistics.
	 */
	private void setGameEndedScene() {
		Label lblInfo = new Label("Player: " + pnlGame.getName() + "\nGame ID: " + pnlGame.getGameID() + "\nTime: "
				+ secondsCounter + "\nHits: " + pnlGame.getHits() + "\nMisses: " + pnlGame.getMisses() + "\nScore: "
				+ pnlGame.getScore());
		Button btnOK = new Button("OK");
		btnOK.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setTrainingOrGameScene();
			}
		});

		VBox vb = new VBox(30);
		vb.getChildren().addAll(lblInfo, btnOK);
		Scene scene = new Scene(vb);

		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				setTrainingOrGameScene();
				event.consume();
			}
		});
	}

	/**
	 * Adds timer to the game pane.
	 * 
	 * @param gamePanel
	 *            the game panel
	 * @param realGame
	 *            true for game mode, false for training mode.
	 */
	private void addTimerToGame(GamePane gamePanel, boolean realGame) {
		secondsCounter = 0;
		Label lblTimer = new Label("Timer: 0");
		lblTimer.setStyle("-fx-text-fill: blue");
		timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				secondsCounter++;
				lblTimer.setText("Time: " + Integer.toString(secondsCounter));

				if ((secondsCounter == GAME_LENGTH) && realGame) {
					endGame();
				}
			}

		}));

		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
		lblTimer.setLayoutX(0);
		lblTimer.setLayoutY(gamePanel.getHeight() - 45);
		gamePanel.getChildren().add(lblTimer);
	}

	/**
	 * Connects to server.
	 */
	private void connectToServer() throws UnknownHostException, IOException, ClassNotFoundException {
		socket = new Socket(HOST, 8000);
		toServer = new ObjectOutputStream(socket.getOutputStream());
		fromServer = new ObjectInputStream(socket.getInputStream());
		gameID = fromServer.readInt();
	}

	/**
	 * Main method of {@code GameClient} to launch the application.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	// TODO: JavaDoc, improve. Daniel: no need to mention type of properties
	// (already mentioned)
	// TODO: DANIEL? Music doesn't stop at scene change
	// TODO: DANIEL? 3D improvements (balls should be small AND look further
	// away, cannon ball should fall and not disappear/rise - add gravity? add
	// shadow to targets? to cannon ball?)
	// sometimes explosion animation stays, some balls are un-hittable, targets'
	// location on Z axis is not clear
	// TODO: add a player from server views

}
