package game.view.start;

import game.engine.dataloader.DataLoader;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameInstructions extends Application{
	private static int currentLetter=0;
	private static ArrayList<VBox> letters;
	
	static int nextLetter(int dir){
		int dist=currentLetter+dir;
		if(dist<0) dist=letters.size()-1;
		else if(dist==letters.size()) dist=0;
		currentLetter=dist;
		return dist;
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		letters=new ArrayList<VBox>();
		ArrayList<GameSection> instructionData=DataLoader.readIconLabels();
		
		for (GameSection gameSection : instructionData)	{
			VBox letterView= new VBox();
			letterView.getChildren().addAll(gameSection.getTitle(),gameSection.letter);
			letters.add(letterView);
		}
		
		
		NavButton left=new NavButton("❮");
		NavButton right=new NavButton("❯ ");
		
		Text letterNumber=new Text(currentLetter+1+"");
		letterNumber.setFont(Font.font(20));
		letterNumber.setFill(Color.WHITE);
		
		StackPane leftSide=new StackPane();
		leftSide.getChildren().add(left);
		
		StackPane rightSide=new StackPane();
		rightSide.getChildren().add(right);
		
		StackPane footer= new StackPane();
		footer.getChildren().add(letterNumber);
		
		BorderPane root = new BorderPane();
		root.setStyle(
		    "-fx-background-color: linear-gradient(to left,  #1F5C66, #301D61);" +
		    "-fx-padding: 25;"
		);
		
		root.setRight(rightSide);
		root.setLeft(leftSide);
		root.setCenter(letters.get(currentLetter));
		root.setBottom(footer);
		
		left.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				root.setCenter(letters.get(nextLetter(-1)));
				letterNumber.setText(currentLetter+1+"");
			}
		});
		right.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				root.setCenter(letters.get(nextLetter(1)));
				letterNumber.setText(currentLetter+1+"");
			}
		});
		
		Scene scene = new Scene(root,1050,980);
		
		primaryStage.setTitle("How to play ?");
		primaryStage.setScene(scene);
		primaryStage.show();
	
	}

	public static void main(String[] args) {
		launch(args);
	}
}
