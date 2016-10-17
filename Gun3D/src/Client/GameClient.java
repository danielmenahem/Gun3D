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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class GameClient extends Application {
	private final Difficulty trainingDifficulty = Difficulty.Easy;
	private final int sizeOfButtons = 300;
	private final int sizeOfPadding = 30;

	private Stage primaryStage;
	private int secondsCounter;
	private Socket socket;
	private String host = "localhost";
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;

	private double gameWidth;
	private double gameHeight;

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

		primaryStage.setScene(trainingOrGameScene());
		// primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.show();
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	private Scene startGame(Difficulty difficulty, String nameText) {

		String playerID = nameText.equals("") ? "Annonymous" : nameText;
		try {
			connectToServer();
		} catch (ClassNotFoundException | IOException e) {
			// TODO what if no server
			e.printStackTrace();
		}
		GamePane gp = new GamePane(gameWidth, gameHeight);
		addTimerToGame(gp);
		gp.startMatch(difficulty, toServer, playerID, gameID);

		Scene scene = new Scene(gp, gameWidth, gameHeight, true);
		scene.setFill(null);
		return scene;

	}

	private void addTimerToGame(GamePane gp) {
		secondsCounter = 0;
		Label lblTimer = new Label("Timer:");
		Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				secondsCounter++;
				lblTimer.setText("Time: " + Integer.toString(secondsCounter));
			}
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
		lblTimer.setLayoutX(0);
		lblTimer.setLayoutY(gp.getHeight() - 45);
		gp.getChildren().add(lblTimer);
	}

	private Scene gameSettingsScene() {
		GridPane gpGameSettings = new GridPane();
		Label lblName = new Label("Insert your name:");
		TextField tfName = new TextField();

		Button btnEasy = new Button("Easy");
		btnEasy.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnEasy.setOnAction(e -> {
			createNewWindow(startGame(Difficulty.Easy, tfName.getText()));
		});

		Button btnMedium = new Button("Medium");
		btnMedium.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnMedium.setOnAction(e -> {
			createNewWindow(startGame(Difficulty.Medium, tfName.getText()));
		});

		Button btnHard = new Button("Hard");
		btnHard.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnHard.setOnAction(e -> {
			createNewWindow(startGame(Difficulty.Hard, tfName.getText()));
		});

		gpGameSettings.add(lblName, 0, 0);
		gpGameSettings.add(tfName, 1, 0);
		gpGameSettings.add(btnEasy, 0, 1);
		gpGameSettings.add(btnMedium, 1, 1);
		gpGameSettings.add(btnHard, 2, 1);

		gpGameSettings.setHgap(sizeOfPadding);
		gpGameSettings.setVgap(sizeOfPadding);
		gpGameSettings.setPadding(new Insets(sizeOfPadding));
		gpGameSettings.setBackground(null);

		Scene scene = new Scene(gpGameSettings, sizeOfButtons * 3 + sizeOfPadding * 4,
				sizeOfButtons + sizeOfPadding * 5, true);
		scene.setFill(null);
		return scene;
	}

	private Scene trainingScene() {
		GamePane gp = new GamePane(gameWidth, gameHeight);
		Scene scene = new Scene(gp, gameWidth, gameHeight, true);
		scene.setFill(null);
		addTimerToGame(gp);

		gp.startTraining(trainingDifficulty);

		return scene;
	}

	private Scene trainingOrGameScene() {
		GridPane gpTraingOrGame = new GridPane();
		Button btnTraining = new Button("Training Mode");

		btnTraining.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;"
				+ "-fx-background-color: #E6E6FA, rgba(0,0,0,0.05),linear-gradient(#dcca8a, #c7a740), linear-gradient(#f9f2d6 0%, #f4e5bc 20%, #e6c75d 80%, #e2c045 100%),linear-gradient(#f6ebbe, #e6c34d);");
		btnTraining.setOnAction(e -> {
			createNewWindow(trainingScene());
		});

		Button btnGame = new Button("Game Mode");
		btnGame.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnGame.setOnAction(e -> {
			createNewWindow(gameSettingsScene());
		});

		gpTraingOrGame.add(btnTraining, 0, 0);
		gpTraingOrGame.add(btnGame, 1, 0);
		gpTraingOrGame.setHgap(sizeOfPadding);
		gpTraingOrGame.setPadding(new Insets(sizeOfPadding));
		gpTraingOrGame.setBackground(null);

		Scene scene = new Scene(gpTraingOrGame, sizeOfButtons * 2 + sizeOfPadding * 3,
				sizeOfButtons + sizeOfPadding * 2, true);
		scene.setFill(null);
		return scene;
	}

	private void createNewWindow(Scene scene) {
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
		stage.centerOnScreen();
		stage.setAlwaysOnTop(true);
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
	
	//TODO: CSS independent sheet + better design
	//TODO: JavaDoc, Daniel's improvements
	//TODO: no server exceptions
	//TODO: Close client better
	

}
