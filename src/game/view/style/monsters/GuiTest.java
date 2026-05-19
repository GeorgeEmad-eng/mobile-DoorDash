package game.view.style.monsters;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GuiTest extends Application{
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		final double CELL_SIZE=80;
		StackPane root = new StackPane();
		String[] list={"James P. Sullivan","Mike Wazowski","Randall Boggs","Celia Mae","Roz","Fungus","Henry J. Waternoose","Yeti"};
		
		VBox items= new VBox();//board pane
        for (int i = 0; i < list.length; i++) {
        	StackPane cellPane = new StackPane();
        	cellPane.setPrefSize(CELL_SIZE, CELL_SIZE);
        	cellPane.setMinSize(CELL_SIZE, CELL_SIZE);
        	cellPane.setMaxSize(CELL_SIZE, CELL_SIZE);
        	cellPane.getStyleClass().add("board-cell");
        	MonsterGUI mon1 =new MonsterGUI("o_"+list[i],CELL_SIZE-4,"#4CA2F7");
        	
        	mon1.scarerGUI();
        	//mon1.powerUPeffect();
        	
        	Rectangle clip1 =new Rectangle(CELL_SIZE*1.5,CELL_SIZE*1.5);
    		clip1.setTranslateY(-CELL_SIZE/8-10);
    		Rectangle freezeLap =new Rectangle(CELL_SIZE*1.05,CELL_SIZE*1.28,Color.rgb(161, 229, 247, 0.48));
    		freezeLap.setArcWidth(28);
    		freezeLap.setArcHeight(17);
    		freezeLap.setTranslateY(-CELL_SIZE/8+5);
    		mon1.setClip(clip1);
    		mon1.coldFreeze();
    		mon1.affect(-450);
    		//mon1.affect(320);
    		cellPane.getChildren().add(mon1);
//    		cellPane.getChildren().add(freezeLap);
    		items.getChildren().add(cellPane);
        }
		
		//mon1.freeze();
		//mon1.freeze();
		// ^ --mon1.monSet();
		//mon1.powerUPeffect();
		
//		MonsterGUI mon2 =new MonsterGUI("s_Mike Wazowski",CELL_SIZE-20,"#97DE4A");
//		mon2.laugherGUI();
		//cellPane.setLeft(mon1);
		
		root.getChildren().add(items);

		Scene scene =new Scene(root,999,999);
		scene.getStylesheets().add(getClass().getResource("/game/view/css/monster.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/game/view/css/board.css").toExternalForm());
		primaryStage.setTitle("GUItest");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	public static void main(String[] args) {
		launch();
	}
}
