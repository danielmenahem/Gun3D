package views;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.List;

import database.DBRecord;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DBView extends Application{
	private ComboBox<String> cbxQueryChoice;
	private ObservableList<String> queryOptions;
	
	private TextField tfStudent;
	private TextField tfTeacher;

	private Button btnRun;

	private GridPane borderPane;

	private Stage stage;
	private Scene scene;

	private TableView<DBRecord> table;
	private List<DBRecord> list;
	private ObservableList<DBRecord> data;

	@Override
	public void start(Stage primaryStage) throws Exception {
		buildGUI();
		generateCBXOptions();
		
	}

	private void buildGUI() {
		cbxQueryChoice = new ComboBox<>();
	}

	private void generateCBXOptions() {
		queryOptions = 	FXCollections.observableArrayList(
		        "All events by name and then by game time",
		        "All events by name and then by game score (descending)",
		        "All games by game score (descending)",
		        "All players with 3 games or more by rank", // average of 3 top games
		        "All games by most hits (descending)",
		        "All games by most misses (descending)"
		    );	
		cbxQueryChoice.setItems(queryOptions);
	}
	
	
}
