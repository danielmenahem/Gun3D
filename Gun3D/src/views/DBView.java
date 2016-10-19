package views;

import java.sql.SQLException;

import GameObjects.EventType;
import database.Record;
import database.DBcontroller;
import database.DBcontrollerInterface;
import javafx.application.Application;
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

/**
 * A GUI stage to display DB queries
 * 
 * @author Daniel Menahem 39676804
 * @author Michael Shvarts 301578878
 * @version 1.0
 *
 *          JavaDoc made under the assumption that the HTML generated doc will
 *          include private fields. Under different circumstances, the private
 *          attributes would be documented in the public getters, and the rest
 *          would link there. Private attributes documentation was made so
 *          everything will be documented.
 */

public class DBView extends Stage {
	/**
	 * Contains query options.
	 */
	private final String[] options = { "All events by name and then by game time",
			"All events by name and then by game score (descending)", "All games by game score (descending)",
			"All players with 3 games or more by rank", "All games by most hits (descending)",
			"All games by most misses (descending)" };
	/**
	 * Minimum games to count for top players.
	 */
	private final int MIN_GAMES_TO_COUNT_FOR_TOP_PLAYERS = 3;
	/**
	 * Displays the query options.
	 */
	private ComboBox<String> cbxQueryChoice;
	/**
	 * Contains query options for the combobox.
	 */
	private ObservableList<String> queryOptions;
	/**
	 * "Choose a query" label.
	 */
	private Label lblQueryChoice;
	/**
	 * "Run" query button.
	 */
	private Button btnRun;
	/**
	 * Vertical box to contain the options grid pane and the table view.
	 */
	private VBox vbContainer;
	/**
	 * A gridpane to contain the elemnts need to choose and run a query.
	 */
	private GridPane gpOptions;
	/*
	 * Main scene to contain the GUI elements.
	 */
	private Scene scene;
	/**
	 * The database controller that performs the queries and implements.
	 * {@link DBcontrollerInterface}
	 */
	private DBcontrollerInterface db;
	/**
	 * The table view to display the results.
	 */
	private TableView<Record> table;
	/**
	 * Column for player ID.
	 */
	private TableColumn<Record, String> tcPlayerID;
	/**
	 * Column for game id/number of games.
	 */
	private TableColumn<Record, String> tcGameID;
	/**
	 * Column for game score/average score/number of hits/misses.
	 */
	private TableColumn<Record, String> tcGameScore;
	/**
	 * Column for event type hit/miss.
	 */
	private TableColumn<Record, String> tcEventType;
	/**
	 * Column for timestamp.
	 */
	private TableColumn<Record, String> tcTimeStamp;

	/**
	 * The constructor that builds this view
	 * 
	 * @param db
	 *            {@link DBView#db}
	 */
	public DBView(DBcontroller db) {
		this.db = db;
		buildGUI();
	}

	/**
	 * Builds the GUI elements
	 */
	private void buildGUI() {
		gpOptions = new GridPane();
		gpOptions.setPadding(new Insets(10, 10, 10, 10));
		gpOptions.setHgap(10);
		gpOptions.setVgap(10);
		
		lblQueryChoice = new Label("Choose a query:");
		
		cbxQueryChoice = new ComboBox<>();
		queryOptions = FXCollections.observableArrayList(options);
		cbxQueryChoice.setItems(queryOptions);
		cbxQueryChoice.getSelectionModel().selectFirst();
		
		btnRun = new Button("Run");
		btnRun.setOnAction(e -> {
			runQuery(cbxQueryChoice.getSelectionModel().getSelectedIndex());
		});

		gpOptions.add(lblQueryChoice, 0, 0);
		gpOptions.add(cbxQueryChoice, 0, 1);
		gpOptions.add(btnRun, 1, 1);

		table = new TableView<Record>();

		tcPlayerID = new TableColumn<>("Player ID");
		tcPlayerID.setCellValueFactory(new PropertyValueFactory<Record, String>("playerID"));
		
		tcGameID = new TableColumn<>("Game ID");
		tcGameID.setCellValueFactory(new PropertyValueFactory<Record, String>("gameID"));
		
		tcGameScore = new TableColumn<>("Score");
		tcGameScore.setCellValueFactory(new PropertyValueFactory<Record, String>("score"));
		
		tcEventType = new TableColumn<>("Event Type");
		tcEventType.setCellValueFactory(new PropertyValueFactory<Record, String>("event"));
		
		tcTimeStamp = new TableColumn<>("Time Stamp");
		tcTimeStamp.setCellValueFactory(new PropertyValueFactory<Record, String>("timeStamp"));

		table.setPrefWidth(450);
		table.setPrefHeight(300);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		vbContainer = new VBox(20);
		vbContainer.setPadding(new Insets(10));
		vbContainer.getChildren().addAll(gpOptions, table);

		scene = new Scene(vbContainer, 500, 475);

		this.setTitle("Gun3D DB view");
		this.setScene(scene);
		this.show();
		this.setAlwaysOnTop(true);
	}

	/**
	 * Runs the selected query, modifies the table per query
	 * 
	 * @param index
	 *            of the selected query
	 */
	@SuppressWarnings("unchecked")
	private void runQuery(int index) {
		try {
			switch (index) {
			case 0: {
				// All events by name and then by game time
				table.setItems(FXCollections.observableList(db.getAllEventsByNameAndThenByTimeDescending()));
				table.getColumns().setAll(tcPlayerID, tcGameID, tcGameScore, tcEventType, tcTimeStamp);
				tcGameID.setText("Game ID");
				tcGameScore.setText("Score");
				break;
			}
			case 1: {
				// All events by name and then by game score
				table.setItems(FXCollections.observableList(db.getAllEventsByNameAndThenByScoreDescending()));
				table.getColumns().setAll(tcPlayerID, tcGameID, tcGameScore, tcEventType, tcTimeStamp);
				tcGameID.setText("Game ID");
				tcGameScore.setText("Score");
				break;
			}
			case 2: {
				// All games by game score
				table.setItems(FXCollections.observableList(db.getAllEventsByGameScoreDescending()));
				table.getColumns().setAll(tcPlayerID, tcGameID, tcGameScore);
				tcGameID.setText("Game ID");
				tcGameScore.setText("Score");
				break;
			}
			case 3: {
				// Average scores of players with more than 3 games
				table.setItems(FXCollections.observableList(
						db.getAverageScoresOfPlayersWithXGamesOrMoreDescending(MIN_GAMES_TO_COUNT_FOR_TOP_PLAYERS)));
				table.getColumns().setAll(tcPlayerID, tcGameID, tcGameScore);
				tcGameID.setText("Number Of Games");
				tcGameScore.setText("Average Score");
				break;
			}
			case 4: {
				// All games by most hits (descending)
				table.setItems(FXCollections.observableList(db.getAllGamesByMostEventsDescending(EventType.HIT)));
				table.getColumns().setAll(tcPlayerID, tcGameID, tcGameScore);
				tcGameID.setText("Game ID");
				tcGameScore.setText("Number of hits");
				break;
			}
			case 5: {
				// All games by most misses (descending)
				table.setItems(FXCollections.observableList(db.getAllGamesByMostEventsDescending(EventType.MISS)));
				table.getColumns().setAll(tcPlayerID, tcGameID, tcGameScore);
				tcGameID.setText("Game ID");
				tcGameScore.setText("Number of misses");

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
