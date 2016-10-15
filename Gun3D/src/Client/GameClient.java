package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Client.GamePane.Difficulty;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class GameClient extends Application {
	private final int sizeOfButtons = 300;
	private final int sizeOfPadding = 30;

	private Stage primaryStage;
	private Scene scene;

	private Button btnTraining;
	private Button btnGame;
	private Button btnEasy;
	private Button btnMedium;
	private Button btnHard;
	
	private Label lblName;
	private TextField tfName;

	private GridPane gpGameSettings;
	private HBox hbTrainingOrGame;
	private HBox hbGameDifficulty;
	
	private Socket socket;
	private String host = "localhost";
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;
	
	private int gameID;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		
		

		buildGUI();

	}

	private void buildGUI() {
		buildGameOptionsPanel();
		buildGameDifficultyPanel();

		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		double width = primaryScreenBounds.getWidth();
		double height = primaryScreenBounds.getHeight();

		scene = new Scene(hbTrainingOrGame, sizeOfButtons * 2 + sizeOfPadding * 3, sizeOfButtons + sizeOfPadding * 2);
		scene.setFill(null);

		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle("Gun3D");
		primaryStage.show();
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				Platform.exit();
				System.exit(0);
			}
		});

	}

	private void buildGameDifficultyPanel() {
		gpGameSettings = new GridPane();
		lblName = new Label("Insert your name:");
		tfName = new TextField();
		
		btnEasy = new Button("Easy");
		btnEasy.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnEasy.setOnAction(e -> {
			startGame(Difficulty.Easy);
		});

		btnMedium = new Button("Medium");
		btnMedium.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnMedium.setOnAction(e -> {

		});

		btnHard = new Button("Hard");
		btnHard.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnHard.setOnAction(e -> {
			
		});

		gpGameSettings.add(lblName, 0, 0);
		gpGameSettings.add(tfName, 0, 1);
		gpGameSettings.add(btnEasy, 1, 0);
		gpGameSettings.add(btnMedium, 1, 1);
		gpGameSettings.add(btnHard, 1, 2);
		
		gpGameSettings.setPadding(new Insets(sizeOfPadding));
		gpGameSettings.setBackground(null);
		
	}

	private void buildGameOptionsPanel() {
		btnTraining = new Button("Training Mode");
		btnTraining.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;"
				+ "-fx-background-color: #E6E6FA, rgba(0,0,0,0.05),linear-gradient(#dcca8a, #c7a740), linear-gradient(#f9f2d6 0%, #f4e5bc 20%, #e6c75d 80%, #e2c045 100%),linear-gradient(#f6ebbe, #e6c34d);");
		btnTraining.setOnAction(e -> {

		});

		btnGame = new Button("Game Mode");
		btnGame.setStyle("-fx-background-radius: 50em; " + "-fx-min-width: " + sizeOfButtons + "px; "
				+ "-fx-min-height: " + sizeOfButtons + "px; " + "-fx-max-width: " + sizeOfButtons + "px; "
				+ "-fx-max-height: " + sizeOfButtons + "px;");
		btnGame.setOnAction(e -> {
			scene = new Scene(hbGameDifficulty, sizeOfButtons * 3 + sizeOfPadding * 3,
					sizeOfButtons + sizeOfPadding * 2);
			scene.setFill(null);
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
		});

		hbTrainingOrGame = new HBox(sizeOfPadding);
		hbTrainingOrGame.setBackground(null);
		hbTrainingOrGame.setPadding(new Insets(sizeOfPadding));
		hbTrainingOrGame.getChildren().addAll(btnTraining, btnGame);
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
