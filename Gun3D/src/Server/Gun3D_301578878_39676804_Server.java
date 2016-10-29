package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Client.Gun3D_301578878_39676804_Client;
import Database.DBView;
import Database.DBcontroller;
import Database.DBcontrollerInterface;
import GameObjects.EventType;
import GameObjects.GameEvent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;



/**
 * This class provides a game server, manages online games and games history
 * 
 * <br>
 * Extends: {@link Application}
 * 
 * @author Daniel Menahem 39676804
 * @author Michael Shvarts 301578878
 * @version 1.0
 */
public class Gun3D_301578878_39676804_Server extends Application {

	/**
	 * The value of static final {@code LABEL_STYLE} is {@value}.
	 */
	private static final String LABEL_STYLE = "-fx-font-size: 15;-fx-font-weight: bold;";
	
	/**
	 * The value of static final {@code BUTTON_STYLE} is {@value}.
	 */
	private static final String BUTTON_STYLE = "-fx-background-color: #d5d6e3;";
	
	/**
	 * The value of static final {@code BORDER_STYLE} is {@value}.
	 */
	private static final String BORDER_STYLE = "-fx-border-color: #aaaab2;-fx-border-width: 2px;";
	
	/**
	 * The value of static final {@code ACTION_PANES_STYLE} is {@value}.
	 */
	private static final String ACTION_PANES_STYLE = "-fx-background-color: #fff9be;";
	
	/**
	 * The value of static final {@code NUMBER_OF_ATTEMPTS} is {@value}.
	 */
	private static final int NUMBER_OF_ATTEMPTS = 3;

	/**
	 * The {@code taLog} is a {@link TextArea}. present server log.
	 */
	private TextArea taLog = new TextArea();

	/**
	 * The {@code btnShowDB} is a {@link Button}. loads the DB view.
	 */
	private Button btnShowDB = new Button("Show DB Data");

	/**
	 * The {@code btnDelte} is a {@link Button}. deletes player from DB.
	 */
	private Button btnDelete = new Button("Delete Player");

	/**
	 * The {@code btnChange} is a {@link Button}. changes player name on DB.
	 */
	private Button btnChange = new Button("Change Player Name");

	/**
	 * The {@code paneMain} is a {@link BorderPane}. contains all of the GUI
	 * components.
	 */
	private BorderPane paneMain = new BorderPane();

	/**
	 * The {@code paneActions} is a {@link BorderPane}.
	 */
	private BorderPane paneActions = new BorderPane();

	/**
	 * The {@code paneDelete} is a {@link BorderPane}.
	 */
	private BorderPane paneDelete = new BorderPane();

	/**
	 * The {@code paneChange} is a {@link BorderPane}.
	 */
	private BorderPane paneChange = new BorderPane();

	/**
	 * The {@code paneDeleteControls} is a {@link GridPane}.
	 */
	private GridPane paneDeleteControls = new GridPane();

	/**
	 * The {@code paneChangeControls} is a {@link GridPane}.
	 */
	private GridPane paneChangeControls = new GridPane();
	
	/**
	 * The {@code paneCommands} is an {@link HBox}.
	 */
	private HBox paneCommands = new HBox();
	
	/**
	 * The {@code spLog} is an {@link ScrollPane}.
	 */
	private ScrollPane spLog = new ScrollPane(taLog);

	/**
	 * The {@code lblDeleteHead} is a {@link Label}.
	 */
	private Label lblDeleteHead = new Label("Delete Player");

	/**
	 * The {@code lblChangeHead} is a {@link Label}.
	 */
	private Label lblChangeHead = new Label("Change Player Name");

	/**
	 * The {@code lblNameForDelete} is a {@link Label}.
	 */
	private Label lblNameForDelate = new Label("Player Name: ");

	/**
	 * The {@code lblNameForChange} is a {@link Label}.
	 */
	private Label lblNameToChange = new Label("Current Name: ");

	/**
	 * The {@code lblReplacingName} is a {@link Label}.
	 */
	private Label lblReplacingName = new Label("New Name: ");

	/**
	 * The {@code tfNameForDelete} is a {@link TextField}.
	 */
	private TextField tfNameForDelete = new TextField();

	/**
	 * The {@code tfNameToChange} is a {@link TextField}.
	 */
	private TextField tfNameToChange = new TextField();

	/**
	 * The {@code tfReplacingName} is a {@link TextField}.
	 */
	private TextField tfReplacingName = new TextField();

	/**
	 * According to instructions, option to open a client from server.
	 */
	private Button btnNewGame = new Button("New Game/Player");
	/**
	 * The {@code primaryStage} is a {@link Stage}. The application's primary
	 * Stage
	 */
	private Stage primaryStage;

	/**
	 * The {@code serverSocket} is a {@link ServerSocket}. The listening socket
	 * of the server
	 */
	private ServerSocket serverSocket;

	/**
	 * The {@code sockets} is a {@link ArrayList}. Holds ongoing sockets
	 * 
	 * @see Socket
	 */
	private ArrayList<Socket> sockets;

	/**
	 * The {@code dbManager} is a {@link DBcontroller}. Controls all
	 * interactions with DB.
	 */
	private DBcontrollerInterface dbManager;

	/**
	 * The {@code lastGameID} holds the last game id.
	 */
	private int lastGameID;

	/**
	 * The {@code gameIdLock} is a {@link ReentrantLock}. protects the
	 * {@code lastGameID} from parallel use
	 */
	private static Lock gameIdLock = new ReentrantLock();

	
	/**
	 * Builds the GUI and sets up the server.
	 * 
	 * @param primaryStage
	 *            the desired stage of the start
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		sockets = new ArrayList<>();

		connectToDB();
		buildGUI();

		primaryStage.setOnCloseRequest(e -> {
			onClose();
		});
		
		setButtonsActions();
		
		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(8000);
				writeToLog("Server started at " + new Date() + '\n');
				while (true) {
					dealWithNewClient(serverSocket.accept());
				}
			} catch (IOException ex) {
			}
		}).start();
	}
	
	
	/**
	 * Sets the application buttons actions
	 * */
	private void setButtonsActions(){
		btnShowDB.setOnAction(e -> {
			viewDB();
		});
		
		btnShowDB.setOnMouseEntered(e -> {
			btnDelete.setEffect(new Glow());
		});	
		
		btnShowDB.setOnMouseExited(e -> {
			btnShowDB.setEffect(null);
		});
		
		btnDelete.setOnAction(e -> {
			deletePlayer();
		});	
		
		btnDelete.setOnMouseEntered(e -> {
			btnDelete.setEffect(new Glow());
		});
		
		btnDelete.setOnMouseExited(e -> {
			btnDelete.setEffect(null);
		});
		
		btnChange.setOnAction(e -> {
			changePlayerName();
		});
		
		btnChange.setOnMouseEntered(e -> {
			btnDelete.setEffect(new Glow());
		});	
		
		btnChange.setOnMouseExited(e -> {
			btnChange.setEffect(null);
		});
		
		btnNewGame.setOnAction(e -> {
			newGame();
		});	
		
		btnNewGame.setOnMouseEntered(e -> {
			btnDelete.setEffect(new Glow());
		});
		
		btnNewGame.setOnMouseExited(e -> {
			btnNewGame.setEffect(null);
		});
	}

	
	/**
	 * Starts a new stand alone game, thus creating a new player, per
	 * instructions
	 */
	private void newGame() {
		try {
			Gun3D_301578878_39676804_Client gc = new Gun3D_301578878_39676804_Client();
			gc.start(new Stage());
			gc.playAServerGame();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	
	/**
	 * Changes player name in DB based on the inserted text in the intended
	 * TextFileds errors will be printed in server log
	 */
	private void changePlayerName() {
		if (tfNameToChange.getText().equals("") || tfReplacingName.getText().equals(""))
			writeToLog("Name Change Error: Please insert both names to make the change\n");
		else {
			try {
				dbManager.changePlayerName(tfNameToChange.getText(), tfReplacingName.getText());
				writeToLog("Name change message: All events under the name " + tfNameToChange.getText()
						+ " have been replaced by the name " + tfReplacingName.getText() + "\n");
			} catch (SQLException ex) {
				writeToLog("Delete Error: SQL Exception\n");
			} catch (Exception e) {
				writeToLog("Name Change Error: Old player name does not exist\n");
			}
		}
		tfNameToChange.setText("");
		tfReplacingName.setText("");
	}

	
	/**
	 * Deletes player name in DB based on the inserted text in the intended
	 * TextFiled errors will be printed in server log
	 */
	private void deletePlayer() {
		if (tfNameForDelete.getText().equals(""))
			writeToLog("Delete Error: Please insert name to delete\n");
		else {
			try {
				dbManager.deletePlayer(tfNameForDelete.getText());
				writeToLog("Delete message: The player " + tfNameForDelete.getText() + " deleted successfuly\n");
			} catch (SQLException ex) {
				writeToLog("Delete Error: SQL Exception\n");
			} catch (Exception e) {
				writeToLog("Delete Error: Inserted player name does not exist\n");
			}
			tfNameForDelete.setText("");
		}
	}

	
	/**
	 * Gets new socket and opens new thread for it documents details in server
	 * log
	 */
	private void dealWithNewClient(Socket socket) {
		InetAddress inetAddress = socket.getInetAddress();
		writeToLog("Starting thread for client at " + new Date() + '\n');
		writeToLog("Client host name is " + inetAddress.getHostName() + "\n");
		writeToLog("Client IP Address is " + inetAddress.getHostAddress() + "\n");
		new Thread(new HandleAClient(socket)).start();
	}

	
	/**
	 * Starts a connection do DB * @exception e close the server if the
	 * connection failed;
	 */
	private void connectToDB() {
		try {
			dbManager = new DBcontroller();
			this.lastGameID = dbManager.getCurrentNumberOfGames();
		} catch (Exception e) {
			close();
		}
	}

	
	/**
	 * Opens the DB view
	 */
	private void viewDB() {
		Platform.runLater(() -> {
			primaryStage.setAlwaysOnTop(false);
			try {
				Stage dialog = new DBView(dbManager);
				dialog.show();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}

	
	/**
	 * Writes messages to sever log
	 **/
	private void writeToLog(String line) {
		Platform.runLater(() -> {
			taLog.appendText(line);
		});
	}

	
	/**
	 * Builds the application GUI
	 */
	private void buildGUI() {
		organizeControls();
		setControlsPaddingAndAlignment();
		setControlsStyle();

		Scene scene = new Scene(paneMain, 800, 310);
		primaryStage.setTitle("Game Server");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setAlwaysOnTop(true);
	}
	
	
	/**
	 * Sets the application controls style
	 * */
	private void setControlsStyle(){
		paneMain.getCenter().setStyle(BORDER_STYLE);
		paneActions.setStyle(BORDER_STYLE);
		paneChange.setStyle(ACTION_PANES_STYLE);
		paneDelete.setStyle(ACTION_PANES_STYLE);
		lblChangeHead.setStyle(LABEL_STYLE);
		lblDeleteHead.setStyle(LABEL_STYLE);
		btnChange.setStyle(BUTTON_STYLE);
		btnDelete.setStyle(BUTTON_STYLE);
		btnNewGame.setStyle(BUTTON_STYLE);
		btnShowDB.setStyle(BUTTON_STYLE);
		paneCommands.setStyle("-fx-background-color: #46475a;" + BORDER_STYLE);
	}
	
	
	/**
	 * Sets the application controls padding and alignment
	 * */
	private void setControlsPaddingAndAlignment(){
		paneChangeControls.setAlignment(Pos.CENTER);
		paneChangeControls.setPadding(new Insets(20, 12.5, 15, 14.5));
		paneChangeControls.setHgap(5);
		paneChangeControls.setVgap(5);
		
		paneDeleteControls.setAlignment(Pos.CENTER);
		paneDeleteControls.setPadding(new Insets(5, 12.5, 15, 14.5));
		paneDeleteControls.setHgap(5);
		paneDeleteControls.setVgap(5);
		
		paneCommands.setSpacing(5);
		paneCommands.setPadding(new Insets(2,2,4,2));
		
		paneActions.setPadding(new Insets(10, 15, 15, 15));
		spLog.setPadding(new Insets(10,0,0,10));
		
		BorderPane.setAlignment(lblDeleteHead, Pos.CENTER);
		BorderPane.setAlignment(lblChangeHead, Pos.CENTER);
	}
	
	
	/**
	 * Organizes the application controls
	 * */
	private void organizeControls(){
		taLog.setEditable(false);
		taLog.setMinHeight(250);
		
		paneChangeControls.add(lblNameToChange, 0, 0);
		paneChangeControls.add(tfNameToChange, 1, 0);
		paneChangeControls.add(lblReplacingName, 0, 1);
		paneChangeControls.add(tfReplacingName, 1, 1);
		paneChangeControls.add(btnChange, 1, 2);

		paneChange.setTop(lblChangeHead);
		paneChange.setBottom(paneChangeControls);
	
		paneDeleteControls.add(lblNameForDelate, 0, 0);
		paneDeleteControls.add(tfNameForDelete, 1, 0);
		paneDeleteControls.add(btnDelete, 1, 1);

		paneDelete.setTop(lblDeleteHead);
		paneDelete.setBottom(paneDeleteControls);

		paneCommands.getChildren().addAll(btnNewGame, btnShowDB);

		paneActions.setTop(paneChange);
		paneActions.setBottom(paneDelete);
		
		paneMain.setTop(paneCommands);
		paneMain.setRight(paneActions);
		paneMain.setCenter(spLog);
		paneMain.setShape(new Line());
	}

	
	/**
	 * Prepares the application for close. close the application
	 */
	private void onClose() {
		for (int i = 0; i < sockets.size(); i++) {
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

	
	/**
	 * Close the application
	 */
	private void close() {
		dbManager.closeConnection();
		Platform.runLater(() -> {
			Platform.exit();
			System.exit(0);
		});
	}

	
	/**
	 * This class provides a task to manage online clients
	 * 
	 * <br>
	 * Implements: {@link Runnable}
	 * 
	 * @version 1.0
	 */
	class HandleAClient implements Runnable {
		private Socket socket;
		private int gameID;

		/**
		 * Construct a task
		 */
		public HandleAClient(Socket socket) {
			this.socket = socket;
			sockets.add(socket);
		}

		/**
		 * Closes client socket
		 */
		private void closeClient() {
			try {
				writeToLog("Client " + socket.getInetAddress().getHostName() + " session has ended\n");
				this.socket.close();
			} catch (IOException e) {
			}
			sockets.remove(socket);
		}
		
		
		/**
		 * Sets {@link HandleAClient#gameID}
		 * */
		private void setGameID(){
			gameIdLock.lock();
			lastGameID++;
			this.gameID = lastGameID;
			gameIdLock.unlock();
		}

		
		/**
		 * Writes an event to DB
		 * @param event the event for save ({@link GameEvent})
		 * @return true if the writing succeeded
		 * */
		private boolean insertEventToDB(GameEvent event){
			boolean success = false;
			for (int i = 0; i < NUMBER_OF_ATTEMPTS; i++) {
				try {
					dbManager.insertEvent(event.getPlayerName(), event.getGameID(), event.getGameScore(),
							event.getEvent());
					success = true;
					i = NUMBER_OF_ATTEMPTS;
				} catch (SQLException e1) {
				}
			}
			return success;
		}
		
		
		/**
		 * Runs a task
		 * 
		 * @exception SQLException
		 *                closes the client socket
		 * @exception IOException
		 *                closes the client socket
		 * @exception SocketException
		 *                closes client socket
		 */
		@Override
		public void run() {
			try {
				setGameID();
				ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());
				outputToClient.writeInt(this.gameID);
				outputToClient.flush();
				writeToLog("Sent gameID: " + gameID + "\n");
				GameEvent event = null;
				Boolean loop = true;
				int failedReadAttempts = 0;

				while (loop) {
					try {
						event = (GameEvent) inputFromClient.readObject();
						failedReadAttempts = 0;
						if (event.getEvent() != EventType.END_GAME) {
							if (!insertEventToDB(event)) {
								loop = false;
								closeClient();
							}
						} 
						else {
							loop = false;
						}
					} 
					catch (ClassNotFoundException e) {
					} 
					catch (IOException e) {
						failedReadAttempts++;
						if (failedReadAttempts >= NUMBER_OF_ATTEMPTS) {
							loop = false;
							closeClient();
						}
					}
				}
				
				closeClient();
			}
			catch (SocketException ex) {
				closeClient();
			} 
			catch (IOException ex) {
			}
		}
	}

	
	/**
	 * Main of {@code Gun3D_301578878_39676804_Server} to launch the application
	 * 
	 * @param args
	 *            the command line arguments
	 * @see start
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
