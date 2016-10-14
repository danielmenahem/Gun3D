package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import GameObjects.GameEvent;
import Utilities.EventType;
import database.DBcontroller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.shape.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import views.DBView2;

public class GameServer extends Application { 
	
	private static final int NUMBER_OF_ATTEMPTS = 3;
	
	private TextArea ta = new TextArea();
	private Button btnShowDB = new Button("Show DB Data");
	private Button btnDelete = new Button("Delete Player");
	private Button btnChange = new Button("Change Player Name");

	private BorderPane paneMain = new BorderPane();
	private BorderPane paneActions = new BorderPane();
	private BorderPane paneDelete = new BorderPane();
	private BorderPane paneChange = new BorderPane();
	private GridPane paneDeleteControls = new GridPane();
	private GridPane paneChangeControls = new GridPane();
	
	private Label lblDeleteHead = new Label("Delete Player");
	private Label lblChangeHead = new Label("Change Player Name");
	private Label lblNameForDelate = new Label("Player Name: ");
	private Label lblNameToChange = new Label("Current Name: ");
	private Label lblReplacingName = new Label("New Name: ");
	
	private TextField tfNameForDelete = new TextField();
	private TextField tfNameToChange = new TextField();
	private TextField tfReplacingName = new TextField();
	
	private Stage primaryStage;
	
	private ServerSocket serverSocket;
	private ArrayList<Socket> sockets;
	
	private DBcontroller db;
	private int lastGameID;
	private static Lock gameIdLock = new ReentrantLock();
	
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		sockets = new ArrayList<>();
		
		connectToDB();		
		buildGUI();
		
		primaryStage.setOnCloseRequest(e -> {
			onClose();
		});
		
		btnShowDB.setOnAction(e -> {
			viewDB();
		});	
		btnDelete.setOnAction(e -> {
			deletePlayer();
		});
		btnChange.setOnAction(e -> {
			changePlayerName();
		});
		
		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(8000);
				writeToLog("Server started at " + new Date() + '\n');
				while (true) { 
					dealWithNewClient(serverSocket.accept());
				}
			} 
			catch (IOException ex) {}
		}).start();
	}
	
	private void changePlayerName() {
		if(tfNameToChange.getText().equals("") || tfReplacingName.getText().equals(""))
			writeToLog("Name Change Error: Please insert both names to make the change\n");
		else{
			try {
				db.changePlayerName(tfNameToChange.getText(), tfReplacingName.getText());
				writeToLog("Name change message: All events under the name " + tfNameToChange.getText()
				+ "have been replaced by the name " +  tfReplacingName.getText());
			} 
			catch (SQLException e) {
				writeToLog("Name Change Error: Old player name does not exist\n");
			}
		}
		tfNameToChange.setText("");
		tfReplacingName.setText("");
	}

	
	private void deletePlayer() {
		if(tfNameForDelete.getText().equals(""))
			writeToLog("Delete Error: Please insert name to deleting\n");
		else{
			try {
				db.deletePlayer(tfNameForDelete.getText());
				writeToLog("Delete message: The player " + tfNameForDelete.getText() + " deleted successfuly\n");
			} 
			catch (SQLException e) {
				writeToLog("Delete Error: Inserted player name does not exist\n");
			}
			tfNameForDelete.setText("");
		}
	}

	
	private void dealWithNewClient(Socket socket){
		InetAddress inetAddress = socket.getInetAddress();
		writeToLog("Starting thread for client at " + new Date() + '\n');
		writeToLog("Client host name is " + inetAddress.getHostName() + "\n");
		writeToLog("Client IP Address is " + inetAddress.getHostAddress() + "\n");					
		new Thread(new HandleAClient(socket)).start();
	}
	
	
	private void connectToDB(){
		try {
			db = new DBcontroller();
			this.lastGameID = db.getCurrentNumberOfGames();
		} catch (Exception e) {
			close();
		}
	}
	
	
	private void viewDB(){
		Platform.runLater(() -> {
			primaryStage.setAlwaysOnTop(false);
			try {
				Stage dialog = new DBView2(db);
				dialog.show();
			} 
			catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}
	
	
	private void writeToLog(String line){
		Platform.runLater(() -> { 
			ta.appendText(line);
		});
	}
	
	
	private void buildGUI(){
		ta.setEditable(false);
		paneChangeControls.add(lblNameToChange, 0, 0);
		paneChangeControls.add(tfNameToChange, 1, 0);
		paneChangeControls.add(lblReplacingName, 0, 1);
		paneChangeControls.add(tfReplacingName, 1, 1);
		paneChangeControls.add(btnChange, 1, 2);
		paneChangeControls.setAlignment(Pos.CENTER);
		paneChangeControls.setPadding(new Insets(5, 12.5, 15, 14.5));
		
		paneChange.setTop(lblChangeHead);
		paneChange.setBottom(paneChangeControls);
		
		paneDeleteControls.add(lblNameForDelate, 0, 0);
		paneDeleteControls.add(tfNameForDelete, 1, 0);
		paneDeleteControls.add(btnDelete, 1, 1);
		paneDeleteControls.setAlignment(Pos.CENTER);
		paneDeleteControls.setPadding(new Insets(5, 12.5, 15, 14.5));
		
		paneDelete.setTop(lblDeleteHead);
		paneDelete.setBottom(paneDeleteControls);
		
		paneActions.setTop(paneChange);
		paneActions.setBottom(paneDelete);
		paneMain.setRight(paneActions);
		paneMain.setTop(btnShowDB);
		paneMain.setCenter(new ScrollPane(ta));
		paneMain.setShape(new Line());
		
		BorderPane.setAlignment(lblDeleteHead, Pos.CENTER);
		BorderPane.setAlignment(lblChangeHead, Pos.CENTER);
		
		lblChangeHead.setStyle("-fx-font-size: 15;-fx-font-weight: bold;");
		lblDeleteHead.setStyle("-fx-font-size: 15;-fx-font-weight: bold;");		
		
		Scene scene = new Scene(paneMain, 760, 250);
		primaryStage.setTitle("Game Server");
		primaryStage.setScene(scene);
		primaryStage.show(); 
		primaryStage.setAlwaysOnTop(true);
	}
	
	
	private void onClose(){
		for(int i = 0; i< sockets.size(); i++){
			try {
				sockets.get(i).close();
			} catch (IOException e) {
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
		close();
	}
	
	
	private void close(){
		db.closeConnection();
		Platform.runLater(() -> {					
			Platform.exit();
			System.exit(0);
		});
	}

	
	class HandleAClient implements Runnable {
		private Socket socket;
		private int gameID;
		
		/** Construct a thread */
		public HandleAClient(Socket socket) {
			this.socket = socket;
			sockets.add(socket);
		}
		
		private void closeClient(){
			try {
				this.socket.close();
			} catch (IOException e) {
			}
			sockets.remove(socket);
		}

		/** Run a thread */
		public void run() {
			try {
				gameIdLock.lock();
				lastGameID++;
				this.gameID = lastGameID;
				gameIdLock.unlock();
				
				ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream()); 
				ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());
				outputToClient.writeInt(this.gameID);
				
				GameEvent event = null;
				Boolean loop = true;
				int failedReadAttempts = 0;
				
				while (loop) {
					try {
						event = (GameEvent)inputFromClient.readObject();
						failedReadAttempts = 0;
						if(event.getEvent() != EventType.END_GAME){
							db.insertEvent(event.getName(), event.getGameID(), 
									event.getGameScore(), event.getEvent());
						}
						else{
							loop = false;
							closeClient();
						}
					}
					catch (ClassNotFoundException e) {} 
					catch (SQLException e) {
						boolean success = false;
						for(int i = 0; i<NUMBER_OF_ATTEMPTS; i++){
							try {
								db.insertEvent(event.getName(), event.getGameID(), 
										event.getGameScore(), event.getEvent());
								success = true;
								i = NUMBER_OF_ATTEMPTS;
							} catch (SQLException e1) {
							}
						}
						if(!success){
							loop = false;
							closeClient();
						}
					}
					
					catch(IOException e){
						failedReadAttempts++;
						if(failedReadAttempts >= NUMBER_OF_ATTEMPTS){
							loop = false;
							closeClient();
						}
					}
				}
			} 
			catch (SocketException ex) {
				onClose();
			} 
			catch (IOException ex) {}
		}
	}
	

	public static void main(String[] args) {
		launch(args);
	}
	

}
