package Client;

import javafx.application.Application;
import javafx.stage.Stage;

public class GameClient extends Application {
	private Stage stage;
 
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		
		buildGUI();
		
	}
	
	
	private void buildGUI() {

		stage.setTitle("Gun3D DB view");
		
	}


	public static void main(String[] args) {
		launch(args);
	}

}
