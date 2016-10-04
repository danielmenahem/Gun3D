package views;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.management.OperationsException;

import Utilities.EventType;
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
	private final String[] options = {"All events by name and then by game time",
			"All events by name and then by game score (descending)", "All games by game score (descending)",
			"All players with 3 games or more by rank",
			"All games by most hits (descending)", "All games by most misses (descending)" };
	private final int MIN_GAMES_TO_COUNT_FOR_TOP_PLAYERS = 3;

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

		// fakeDbTester();
		buildGUI();
	}

	private void fakeDbTester() {
		for (int i = 0; i < 10; i++) {
			// 10 players
			int maxGames = ThreadLocalRandom.current().nextInt(0, 5 + 1);
			// each has a different number of games
			for (int j = 0; j < maxGames; j++) {
				int gameNumber = 0;
				try {
					gameNumber = db.getCurrentNumberOfGames() + 1;
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int numberOfEvent = ThreadLocalRandom.current().nextInt(0, 15 + 1);
				int scoreRandom = ThreadLocalRandom.current().nextInt(0, 500 + 1);
				// each game has a different number of events
				for (int k = 0; k < numberOfEvent; k++) {
					try {
						// randomize hit of miss
						int hitOrMiss = ThreadLocalRandom.current().nextInt(0, 1 + 1);
						db.insertEvent("Player" + i, gameNumber, scoreRandom, EventType.values()[hitOrMiss]);
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
			runQuery(cbxQueryChoice.getSelectionModel().getSelectedIndex());
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

	private void runQuery(int index) {
		try {
			switch (index) {
			case 0: {
				// All events by name and then by game time
				table.setItems(FXCollections.observableList(db.getAllEventsByNameAndThenByTimeDescending()));
				break;
			}
			case 1: {
				// All events by name and then by game score
				table.setItems(FXCollections.observableList(db.getAllEventsByNameAndThenByScoreDescending()));
				break;
			}
			case 2: {
				// All games by game score
				table.setItems(FXCollections.observableList(db.getAllEventsByGameScoreDescending()));
				break;
			}
			case 3: {
				// Average scores of players with more than 3 games
				table.setItems(FXCollections.observableList(
						db.getAverageScoresOfPlayersWithXGamesOrMoreDescending(MIN_GAMES_TO_COUNT_FOR_TOP_PLAYERS)));
				break;
			}
			case 4: {
				// All games by most hits (descending)
				table.setItems(FXCollections.observableList(db.getAllGamesByMostEventsDescending(EventType.HIT)));
				break;
			}
			case 5: {
				// All games by most misses (descending)
				table.setItems(FXCollections.observableList(db.getAllGamesByMostEventsDescending(EventType.MISS)));
			}
			default: {
				// TODO: error message
				break;
			}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
