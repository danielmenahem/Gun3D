package Client;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Test extends Application {
	
	private ArrayList<CannonShell> shells = new ArrayList<>();
	@Override
	public void start(Stage primaryStage) throws Exception {
		Pane pane = new Pane();
		//Scene scene = new Scene(pane, 1000, 700, true);
		Scene scene = new Scene(pane, 1000, 700);
		Target t1 = new Target(20, 200,300,4, scene.getX(), scene.getWidth());
		Target t2 = new Target(50, 500,100,0, scene.getX(), scene.getWidth());
		Label lbl = new Label();
		CannonShell cs = new CannonShell(135, 82, 130, scene.getHeight(), scene.getWidth());
		shells.add(cs);
		Cannon cannon = new Cannon(scene.getWidth(), scene.getHeight());
		pane.getChildren().addAll(t1, t2, cs, lbl, cannon);
		Timeline tl = new Timeline(new KeyFrame(Duration.millis(20), e->{
			for(CannonShell c : shells){
				c.moveShell();
			}
			Platform.runLater(() -> {
				lbl.setText("X: " + cs.getTranslateX() + " Y: " + cs.getTranslateY() + " Z: "+cs.getTranslateZ());
			});
		}));
		
		tl.setCycleCount(Timeline.INDEFINITE);
		tl.play();
		Timeline animation = new Timeline(new KeyFrame(Duration.millis(20), e1 -> t1.moveTarget()));
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.play(); // Start animation

		primaryStage.setScene(scene);
		primaryStage.show(); 
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				Platform.exit();
				System.exit(0);
			}
		});
		
		pane.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.LEFT)
				cannon.rotateLeft();
			else if(e.getCode() == KeyCode.RIGHT)
				cannon.rotateRight();
			else if(e.getCode() == KeyCode.UP)
				cannon.rotateForward();
			else if(e.getCode() == KeyCode.DOWN)
				cannon.rotateBackwords();
			CannonShell c = new CannonShell(cannon.getTheta(), 
					cannon.getPhi(), 125, scene.getHeight(), scene.getWidth());
			pane.getChildren().add(c);
			shells.add(c);
			
			pane.setFocusTraversable(true);
			pane.requestFocus();
		});
		
		pane.setFocusTraversable(true);
		pane.requestFocus();
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

}
