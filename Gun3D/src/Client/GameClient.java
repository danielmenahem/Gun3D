package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import Utilities.Difficulty;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class GameClient extends Application {
	private final Difficulty TRAINING_DIFFICULTY = Difficulty.Easy;
	private final int SIZE_OF_BUTTONS = 300;
	private final int SIZE_OF_PADDING = 30;
	private final int GAME_LENGTH = 120;

	private int secondsCounter;
	private Socket socket;
	private String host = "localhost";
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;

	private double gameWidth;
	private double gameHeight;

	private GamePane pnlGame;
	private Stage primaryStage;

	private int gameID;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

		buildGUI();

	}

	private void buildGUI() {
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		gameWidth = primaryScreenBounds.getWidth();
		gameHeight = primaryScreenBounds.getHeight();

		showTrainingOrGameStage();
	}

	private void showGameStage(Difficulty difficulty, String nameText) {

		String playerID = nameText.equals("") ? "Annonymous" : nameText;

		try {
			connectToServer();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private void addTimerToGame(GamePane gp, boolean realGame) {
		secondsCounter = 0;
		Label lblTimer = new Label("Timer:");
		Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
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
		lblTimer.setLayoutY(gp.getHeight() - 45);
		gp.getChildren().add(lblTimer);
	}

	private void endGame() {
		pnlGame.stopGame();
		try {
			System.out.println("gpt here");
			toServer.close();
			fromServer.close();
			socket.close();
			showGameEndedStage();
			System.out.println("got here 2");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showGameEndedStage() {
		Label lblInfo = new Label("Player: " + pnlGame.getName() + "\nGame ID: " + pnlGame.getGameID() + "\nTime: "
				+ secondsCounter + "\nHits: " + pnlGame.getHits() + "\nMisses: " + pnlGame.getMisses() + "\nScore: "
				+ pnlGame.getScore());
		Button btnOK = new Button("OK");
		btnOK.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showTrainingOrGameStage();
			}
		});

		VBox vb = new VBox(30);
		vb.getChildren().addAll(lblInfo, btnOK);
		Scene scene = new Scene(vb);

		primaryStage.setScene(scene);
		System.out.println("got here 3");
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				showTrainingOrGameStage();
			}
		});
	}

	private void showGameSettingsStage() {
		GridPane gpGameSettings = new GridPane();
		Label lblName = new Label("Insert your name:");
		TextField tfName = new TextField();

		Button btnEasy = new Button("Easy");
		btnEasy.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-min-height: " + SIZE_OF_BUTTONS + "px; " + "-fx-max-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-max-height: " + SIZE_OF_BUTTONS + "px;");
		btnEasy.setOnAction(e -> {
			showGameStage(Difficulty.Easy, tfName.getText());
		});

		Button btnMedium = new Button("Medium");
		btnMedium.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-min-height: " + SIZE_OF_BUTTONS + "px; " + "-fx-max-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-max-height: " + SIZE_OF_BUTTONS + "px;");
		btnMedium.setOnAction(e -> {
			showGameStage(Difficulty.Medium, tfName.getText());
		});

		Button btnHard = new Button("Hard");
		btnHard.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-min-height: " + SIZE_OF_BUTTONS + "px; " + "-fx-max-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-max-height: " + SIZE_OF_BUTTONS + "px;");
		btnHard.setOnAction(e -> {
			showGameStage(Difficulty.Hard, tfName.getText());
		});

		gpGameSettings.add(lblName, 0, 0);
		gpGameSettings.add(tfName, 1, 0);
		gpGameSettings.add(btnEasy, 0, 1);
		gpGameSettings.add(btnMedium, 1, 1);
		gpGameSettings.add(btnHard, 2, 1);

		gpGameSettings.setHgap(SIZE_OF_PADDING);
		gpGameSettings.setVgap(SIZE_OF_PADDING);
		gpGameSettings.setPadding(new Insets(SIZE_OF_PADDING));
		gpGameSettings.setBackground(null);

		Scene scene = new Scene(gpGameSettings, SIZE_OF_BUTTONS * 3 + SIZE_OF_PADDING * 4,
				SIZE_OF_BUTTONS + SIZE_OF_PADDING * 5, true);
		scene.setFill(null);

		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				showTrainingOrGameStage();
			}
		});
	}

	private void showTrainingStage() {
		GamePane gp = new GamePane(gameWidth, gameHeight);
		Scene scene = new Scene(gp, gameWidth, gameHeight, true);
		scene.setFill(null);
		addTimerToGame(gp, false);

		gp.startTraining(TRAINING_DIFFICULTY);

		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				showTrainingOrGameStage();
			}
		});
	}

	private void showTrainingOrGameStage() {
		GridPane gpTraingOrGame = new GridPane();
		Button btnTraining = new Button("Training Mode");

		btnTraining.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-min-height: " + SIZE_OF_BUTTONS + "px; " + "-fx-max-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-max-height: " + SIZE_OF_BUTTONS + "px;"
				+ "-fx-background-color: #E6E6FA, rgba(0,0,0,0.05),linear-gradient(#dcca8a, #c7a740), linear-gradient(#f9f2d6 0%, #f4e5bc 20%, #e6c75d 80%, #e2c045 100%),linear-gradient(#f6ebbe, #e6c34d);");
		btnTraining.setOnAction(e -> {
			showTrainingStage();
		});

		Button btnGame = new Button("Game Mode");
		btnGame.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-min-height: " + SIZE_OF_BUTTONS + "px; " + "-fx-max-width: " + SIZE_OF_BUTTONS + "px; "
				+ "-fx-max-height: " + SIZE_OF_BUTTONS + "px;");
		btnGame.setOnAction(e -> {
			showGameSettingsStage();
		});

		gpTraingOrGame.add(btnTraining, 0, 0);
		gpTraingOrGame.add(btnGame, 1, 0);
		gpTraingOrGame.setHgap(SIZE_OF_PADDING);
		gpTraingOrGame.setPadding(new Insets(SIZE_OF_PADDING));
		gpTraingOrGame.setBackground(null);

		Scene scene = new Scene(gpTraingOrGame, SIZE_OF_BUTTONS * 2 + SIZE_OF_PADDING * 3,
				SIZE_OF_BUTTONS + SIZE_OF_PADDING * 2, true);
		scene.setFill(null);

		primaryStage.show();
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	private void connectToServer() throws UnknownHostException, IOException, ClassNotFoundException {
		socket = new Socket(host, 8000);
		toServer = new ObjectOutputStream(socket.getOutputStream());
		fromServer = new ObjectInputStream(socket.getInputStream());
		gameID = fromServer.readInt();
	}

	public static void main(String[] args) {
		launch(args);
	}

	// TODO: CSS independent sheet + better design
	// TODO: JavaDoc, Daniel's improvements and added methods
	// TODO: no server exceptions
	// TODO: Close client better

}
