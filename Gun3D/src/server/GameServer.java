package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import GameObjects.GameEvent;
import Utilities.Event;
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
import views.DBView;
import views.DBView2;

public class GameServer extends Application { 
	
	private TextArea ta = new TextArea();
	private Button btnShowDB = new Button("Show DB Data");
	private BorderPane paneMain = new BorderPane();
	private ServerSocket serverSocket;
	private Socket socket;
	private DBcontroller db;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			db = new DBcontroller();
		} catch (Exception e) {
		}
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
				Platform.exit();
				System.exit(0);
			}
		});
		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(8000);
				Platform.runLater(() -> {
					ta.appendText("Server started at " + new Date() + '\n');
				});
				while (true) { 
					socket = serverSocket.accept();
					
					Platform.runLater(() -> { 
						ta.appendText("Starting thread for client at " + new Date() + '\n');
						
						InetAddress inetAddress = socket.getInetAddress();
						ta.appendText("Client host name is " + inetAddress.getHostName() + "\n");
						ta.appendText("Client IP Address is " + inetAddress.getHostAddress() + "\n");
					});
					
					new Thread(new HandleAClient(socket)).start();
				}
			} catch (IOException ex) {
			}
		}).start();
	}

	class HandleAClient implements Runnable {
		private Socket socket;
		private int clientNumber;
		
		/** Construct a thread */
		public HandleAClient(Socket socket) {
			this.socket = socket;
		}

		/** Run a thread */
		public void run() {
			try {
				int gameId = db.getCurrentNumberOfGames() + 1;
				ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream()); 
				ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());
				outputToClient.writeInt(this.clientNumber);
				GameEvent event;
				Boolean loop = true;
				while (loop) {
					try {
						event = (GameEvent)inputFromClient.readObject();
						if(event.getEvent() != Event.END_GAME){
							db.insertEvent(event.getName(), gameId, event.getGameScore(), event.getEvent());
						}
						else{
							loop = false;
						}
					} 
					catch (ClassNotFoundException e) {
					} 
					catch (SQLException e) {
					}
				}
			} catch (SocketException ex) {
				try {
					serverSocket.close();
				} catch (IOException e) {
				}
			} catch (IOException ex) {
			}

		}
	}
	

	public static void main(String[] args) {
		launch(args);
	}
	

}
