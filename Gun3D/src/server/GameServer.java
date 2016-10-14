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
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import views.DBView2;

public class GameServer extends Application { 
	
	private static final int NUMBER_OF_ATTEMPTS = 3;
	
	private TextArea ta = new TextArea();
	private Button btnShowDB = new Button("Show DB Data");
	private BorderPane paneMain = new BorderPane();
	private ServerSocket serverSocket;
	private ArrayList<Socket> sockets;
	private DBcontroller db;
	private int lastGameID;
	private static Lock gameIdLock = new ReentrantLock();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			db = new DBcontroller();
			this.lastGameID = db.getCurrentNumberOfGames();
		} catch (Exception e) {
			close();
		}
		
		sockets = new ArrayList<>();
		ta.setEditable(false);
		paneMain.setTop(btnShowDB);
		paneMain.setBottom(new ScrollPane(ta));
		
		btnShowDB.setOnAction(e -> {
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
		});
		
		Scene scene = new Scene(paneMain, 450, 200);
		primaryStage.setTitle("GameServer");
		primaryStage.setScene(scene);
		primaryStage.show(); 
		primaryStage.setAlwaysOnTop(true);
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				onClose();
			}
		});
		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(8000);
				Platform.runLater(() -> {
					ta.appendText("Server started at " + new Date() + '\n');
				});
				while (true) { 
					Socket socket = serverSocket.accept();
					InetAddress inetAddress = socket.getInetAddress();
					Platform.runLater(() -> { 
						ta.appendText("Starting thread for client at " + new Date() + '\n');
						ta.appendText("Client host name is " + inetAddress.getHostName() + "\n");
						ta.appendText("Client IP Address is " + inetAddress.getHostAddress() + "\n");
					});
					
					new Thread(new HandleAClient(socket)).start();
				}
			} catch (IOException ex) {
			}
		}).start();
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
					catch (ClassNotFoundException e) {
					} 
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
			catch (IOException ex) {
			}
		}
	}
	

	public static void main(String[] args) {
		launch(args);
	}
	

}
