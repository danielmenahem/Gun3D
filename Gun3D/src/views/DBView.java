package views;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.management.OperationsException;

import Utilities.Event;
import Utilities.nameScoreComparator;
import Utilities.nameTimeComparator;
import Utilities.scoreComparator;
import database.Record;
import database.DBcontroller;
import database.DBcontrollerInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DBView extends Application {
	private final String[] options = { "All events by name and then by game time",
			"All events by name and then by game score (descending)", "All games by game score (descending)",
			"All players with 3 games or more by rank", // average of 3 top
														// games
			"All games by most hits (descending)", "All games by most misses (descending)" };

	private ComboBox<String> cbxQueryChoice;
	private ObservableList<String> queryOptions;

	private Label lblQueryChoice;

	private Button btnRun;

	private VBox vbContainer;

	private GridPane gridPane;

	private Stage stage;
	private Scene scene;

	private DBcontrollerInterface db;
	private TableView<Record> table;
	private List<Record> list;
	private ObservableList<Record> data;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		db = new DBcontroller();

		//fakeDbTester();
		buildGUI();
	}

	private void fakeDbTester() {
		for (int i = 0; i < 10; i++) {
			// 10 players
			int maxGames = ThreadLocalRandom.current().nextInt(0, 5 + 1);
			// each has a different number of games
			for (int j = 0; j < maxGames; j++) {
				int gameNumber = db.getCurrentNumberOfGames() + 1;
				int numberOfEvent = ThreadLocalRandom.current().nextInt(0, 15 + 1);
				int scoreRandom = ThreadLocalRandom.current().nextInt(0, 500 + 1);
				// each game has a different number of events
				for (int k = 0; k < numberOfEvent; k++) {
					try {
						//randomize hit of miss
						int hitOrMiss = ThreadLocalRandom.current().nextInt(0, 1 + 1);
						db.insertEvent("Player" + i, gameNumber, scoreRandom, Event.values()[hitOrMiss]);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void buildGUI() {
		gridPane = new GridPane();
		gridPane.setPadding(new Insets(10, 10, 10, 10));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		lblQueryChoice = new Label("Choose a query:");
		cbxQueryChoice = new ComboBox<>();
		queryOptions = FXCollections.observableArrayList(options);
		cbxQueryChoice.setItems(queryOptions);
		btnRun = new Button("Run");
		btnRun.setOnAction(e -> {
			runQuery(cbxQueryChoice.getSelectionModel().getSelectedItem().toString());
		});

		gridPane.add(lblQueryChoice, 0, 0);
		gridPane.add(cbxQueryChoice, 0, 1);
		gridPane.add(btnRun, 1, 1);

		table = new TableView<Record>();

		TableColumn tcPlayerID = new TableColumn<>("Player ID");
		tcPlayerID.setCellValueFactory(new PropertyValueFactory("playerID"));
		TableColumn tcGameID = new TableColumn<>("Game ID");
		tcGameID.setCellValueFactory(new PropertyValueFactory("gameID"));
		TableColumn tcGameScore = new TableColumn<>("Score");
		tcGameScore.setCellValueFactory(new PropertyValueFactory("score"));
		TableColumn tcEventType = new TableColumn<>("Event Type");
		tcEventType.setCellValueFactory(new PropertyValueFactory("event"));
		TableColumn tcTimeStamp = new TableColumn<>("Time Stamp");
		tcTimeStamp.setCellValueFactory(new PropertyValueFactory("timeStamp"));

		table.getColumns().setAll(tcPlayerID, tcGameID, tcGameScore, tcEventType, tcTimeStamp);
		table.setPrefWidth(450);
		table.setPrefHeight(300);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		vbContainer = new VBox(20);
		vbContainer.getChildren().addAll(gridPane, table);

		scene = new Scene(vbContainer, 500, 475);

		stage.setTitle("Gun3D DB view");
		stage.setScene(scene);

		stage.show();
		stage.setAlwaysOnTop(true);
		stage.setOnCloseRequest(e -> {
			try {
				Platform.exit();
				System.exit(0);
			} catch (Exception ex) {
			}
		});

	}

	private void runQuery(String string) {
		List list = null;
		try {
			list = db.getAllEvents();
			System.out.println(list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObservableList<Record> cm = FXCollections.observableList(list);
		// "All events by name and then by game time",
		// "All events by name and then by game score (descending)",
		// "All games by game score (descending)",
		// "All players with 3 games or more by rank", // average of 3 top games
		// "All games by most hits (descending)",
		// "All games by most misses (descending)"

		// All events by name and then by game time
		if (string.equals(options[0])) {
			cm.sort(new nameTimeComparator());
			table.setItems(cm);
		}
		// All events by name and then by game score (descending)
		if (string.equals(options[1])) {
			cm.sort(new nameScoreComparator());
			table.setItems(cm);
		}
		// All games by game score (descending)
		if (string.equals(options[2])) {
			cm.sort(new scoreComparator());
			table.setItems(cm);
		}

		// All players with 3 games or more by rank", // average of 3 top games
		if (string.equals(options[3])) {
			ObservableList<Record> topPlayers = createTopPlayersList(cm);
			topPlayers.sort(new scoreComparator());
			table.setItems(topPlayers);
		}

		// All games by most hits (descending)
		if (string.equals(options[4])) {
			ObservableList<Record> gamesWithNumberOfHits = createGamesWithNumberOfEvents(Event.HIT);
			gamesWithNumberOfHits.sort(new scoreComparator());
			table.setItems(gamesWithNumberOfHits);
		}
		
		// All games by most misses (descending)
		if (string.equals(options[5])) {
			ObservableList<Record> gamesWithNumberOfHits = createGamesWithNumberOfEvents(Event.MISS);
			gamesWithNumberOfHits.sort(new scoreComparator());
			table.setItems(gamesWithNumberOfHits);
		}

	}
	
	private ObservableList<Record> createTopPlayersList(ObservableList<Record> cm) {
		// TODO Auto-generated method stub
		return null;
	}

	private ObservableList<Record> createGamesWithNumberOfEvents(Event event) {
		List<Record> list = new ArrayList<Record>();
		for (int i = 1; i <= db.getCurrentNumberOfGames(); i++) {
			list.add(getRecordPerGameAccordingToEvent(i, event));
		}
		
		return FXCollections.observableList(list);
	}



	
	
	private Record getRecordPerGameAccordingToEvent(int gameId, Event event) {
		int counter = 0;
		List<Record> eventsByGameList = null;
		try {
			eventsByGameList = db.getEventsByGameID(gameId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		for (int i = 0;  i < eventsByGameList.size(); i++) {
			if (eventsByGameList.get(i).getEvent() == event) {
				counter++;
			}
		}
 		return new Record(eventsByGameList.get(0).getPlayerID(), gameId, counter, event, null);
	}


	public static void main(String[] args) {
		Application.launch(args);
	}

}
