package views;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.management.OperationsException;

import database.DBRecord;
import database.DBcontroller;
import database.DBcontrollerInterface;
import database.nameScoreComparator;
import database.nameTimeComparator;
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

public class DBView2 extends Stage{
	private final String[] options = {"All events by name and then by game time",
	        "All events by name and then by game score (descending)",
	        "All games by game score (descending)",
	        "All players with 3 games or more by rank", // average of 3 top games
	        "All games by most hits (descending)",
	        "All games by most misses (descending)"};
	
	private ComboBox<String> cbxQueryChoice;
	private ObservableList<String> queryOptions;
	
	private Label lblQueryChoice;
	
	private Button btnRun;
	
	private VBox vbContainer;
	

	private GridPane gridPane;

	//private Stage stage;
	private Scene scene;

	private DBcontrollerInterface db; 
	private TableView<DBRecord> table;
	private List<DBRecord> list;
	private ObservableList<DBRecord> data;

	public DBView2(DBcontroller db) throws Exception {
		this.db = db;

		
		buildGUI();		
	}

	private void buildGUI() {
		gridPane = new GridPane();
		gridPane.setPadding(new Insets(10, 10, 10, 10));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		lblQueryChoice = new Label("Choose a query:");
		cbxQueryChoice = new ComboBox<>();
		queryOptions = 	FXCollections.observableArrayList(options);	
		cbxQueryChoice.setItems(queryOptions);
		btnRun = new Button("Run");
		btnRun.setOnAction(e -> {
			runQuery(cbxQueryChoice.getSelectionModel().getSelectedItem().toString());
		});
		
		gridPane.add(lblQueryChoice, 0, 0);
		gridPane.add(cbxQueryChoice, 0, 1);
		gridPane.add(btnRun, 1, 1);
		
		table = new TableView<DBRecord>();
		
		TableColumn tcPlayerID = new TableColumn<>("Player ID");
		tcPlayerID.setCellValueFactory(new PropertyValueFactory("playerID"));
		TableColumn tcGameID = new TableColumn<>("Game ID");
		tcGameID.setCellValueFactory(new PropertyValueFactory("gameID"));
		TableColumn tcGameScore = new TableColumn<>("Game Score");
		tcGameScore.setCellValueFactory(new PropertyValueFactory("gameScore"));
		TableColumn tcEventType = new TableColumn<>("Event Type");
		tcEventType.setCellValueFactory(new PropertyValueFactory("eventType"));
		TableColumn tcTimeStamp = new TableColumn<>("Time Stamp");
		tcTimeStamp.setCellValueFactory(new PropertyValueFactory("timeStamp"));
		
		table.getColumns().setAll(tcPlayerID, tcGameID, tcGameScore, tcEventType, tcTimeStamp);
		table.setPrefWidth(450);
		table.setPrefHeight(300);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		vbContainer = new VBox(20);
		vbContainer.getChildren().addAll(gridPane,table);
		
		scene = new Scene(vbContainer, 500, 475);
		
		setTitle("Gun3D view");
		setScene(scene);
		
		show();
		setAlwaysOnTop(true);
		setOnCloseRequest(e -> {
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
		ObservableList<DBRecord> cm = FXCollections.observableList(list);

		
		
		if (string.equals(options[0])) {
			cm.sort(new nameTimeComparator());
		}
		if (string.equals(options[1])) {
			cm.sort(new nameScoreComparator());
			
		} 
		
		table.setItems(cm);
		/*
		if (string.equals(options[2])) 
		if (string.equals(options[3])) 
		if (string.equals(options[4])) 
		if (string.equals(options[5])) 
		;
		
			
		*/
		/*"All events by name and then by game time",
	        "All events by name and then by game score (descending)",
	        "All games by game score (descending)",
	        "All players with 3 games or more by rank", // average of 3 top games
	        "All games by most hits (descending)",
	        "All games by most misses (descending)"};*/
		
	}



	public static void main(String[] args) {
		Application.launch(args);
	}

}
