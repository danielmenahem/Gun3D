package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
	
	double gameWidth;
	double gameHeight;
	
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
		//primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.show();
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				Platform.exit();
				System.exit(0);
			}
		});
	}



	private void startGame(Difficulty difficulty) {
		
		
		
	}


	private Scene gameSettingsScene() {
		GridPane gpGameSettings = new GridPane();
		Label lblName = new Label("Insert your name:");
		TextField tfName = new TextField();
		Button btnBack = new Button("Back");
		btnBack.setOnAction(e -> {
			primaryStage.setScene(trainingOrGameScene());
			primaryStage.centerOnScreen();
		});
		
		Button btnEasy = new Button("Easy");
		btnEasy.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnEasy.setOnAction(e -> {
			startGame(Difficulty.Easy);
		});

		Button btnMedium = new Button("Medium");
		btnMedium.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnMedium.setOnAction(e -> {
			startGame(Difficulty.Medium);
		});

		Button btnHard = new Button("Hard");
		btnHard.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnHard.setOnAction(e -> {
			startGame(Difficulty.Hard);
		});

		gpGameSettings.add(lblName, 0, 0);
		gpGameSettings.add(tfName, 1, 0);
		gpGameSettings.add(btnBack, 2, 0);
		gpGameSettings.add(btnEasy, 0, 1);
		gpGameSettings.add(btnMedium, 1, 1);
		gpGameSettings.add(btnHard, 2, 1);
		
		gpGameSettings.setHgap(sizeOfPadding);
		gpGameSettings.setVgap(sizeOfPadding);
		gpGameSettings.setPadding(new Insets(sizeOfPadding));
		gpGameSettings.setBackground(null);
		
		Scene scene = new Scene(gpGameSettings, sizeOfButtons * 3 + sizeOfPadding * 3,
				sizeOfButtons + sizeOfPadding * 5);
		scene.setFill(null);
		return scene;	
	}

	private Scene trainingScene() {
		secondsCounter = 0;
		BorderPane bpTraining = new BorderPane();
		BorderPane bpOption = new BorderPane();
		
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
		
		Button btnBack = new Button("Back");
		btnBack.setOnAction(e -> {
			timer.stop();
			primaryStage.setScene(trainingOrGameScene());
			primaryStage.centerOnScreen();
		});
		
		GamePane gp = new GamePane(gameWidth, gameHeight);
		gp.startTraining(trainingDifficulty);
		
		bpOption.setLeft(lblTimer);
		bpOption.setRight(btnBack);
		bpOption.setBackground(null);
		
		bpTraining.setTop(bpOption);
		bpTraining.setCenter(gp);
		bpTraining.setBackground(null);
		
		Scene scene = new Scene(bpTraining,gameWidth, gameHeight, true);
		scene.setFill(null);
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
			primaryStage.setScene(trainingScene());
			primaryStage.centerOnScreen();
		});

		Button btnGame = new Button("Game Mode");
		btnGame.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnGame.setOnAction(e -> {
			primaryStage.setScene(gameSettingsScene());
			primaryStage.centerOnScreen();
		});

		gpTraingOrGame.add(btnTraining, 0, 0);
		gpTraingOrGame.add(btnGame, 1, 0);
		gpTraingOrGame.setHgap(sizeOfPadding);
		gpTraingOrGame.setPadding(new Insets(sizeOfPadding));
		gpTraingOrGame.setBackground(null);
		
		Scene scene = new Scene(gpTraingOrGame, sizeOfButtons * 2 + sizeOfPadding * 3, sizeOfButtons + sizeOfPadding * 2);
		scene.setFill(null);	
		return scene;
	}

	private void connectToServer(){
		try{
			socket = new Socket(host, 8000);
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());
			gameID = (int) fromServer.readObject();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	/*
	class HandleSever implements Runnable {
		private boolean keepAlive = true;
		private Object message = null;

		public Object getMessage() {
			return message;
		}

		public void setMessage(Object message) {
			this.message = message;
		}

		public boolean isKeepAlive() {
			return keepAlive;
		}

		public void setKeepAlive(boolean keepAlive) {
			this.keepAlive = keepAlive;
		}

		public void run() {
			while (keepAlive) {
				try {
					if (message != null) {
						toServer.writeObject(message);
						message = null;
					}
					Thread.sleep(50);
				} 
				catch (Exception e) {}
			}
		}
	}
	*/
	public static void main(String[] args) {
		launch(args);
	}

}
