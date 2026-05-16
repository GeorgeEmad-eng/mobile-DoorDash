package game.view.start;

import game.engine.dataloader.DataLoader;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameInstructions extends BorderPane {
	private static int currentLetter=0;
	private static ArrayList<VBox> letters;
	private static NavButton left;
	private static NavButton right;
	private static Text letterNumber;
	
	
	static int nextLetter(int dir){
		int dist=currentLetter+dir;
		if(dist<0) dist=letters.size()-1;
		else if(dist==letters.size()) dist=0;
		currentLetter=dist;
		return dist;
	}
	public GameInstructions() throws IOException{
		
		letters=new ArrayList<VBox>();
		ArrayList<GameSection> instructionData=DataLoader.readIconLabels();
		
		for (GameSection gameSection : instructionData)	{
			VBox letterView= new VBox();
			letterView.getChildren().addAll(gameSection.getTitle(),gameSection.backWindowPopUp);
			letters.add(letterView);
		}
		
		left=new NavButton("❮");
		right=new NavButton("❯ ");
		
		letterNumber=new Text(currentLetter+1+"");
		letterNumber.setFont(Font.font(20));
		letterNumber.setFill(Color.WHITE);
		
		StackPane leftSide=new StackPane();
		leftSide.getChildren().add(left);
		
		StackPane rightSide=new StackPane();
		rightSide.getChildren().add(right);
		
		StackPane footer= new StackPane();
		footer.getChildren().add(letterNumber);
		this.setStyle(
		    "-fx-background-color: rgba(31, 92, 102, 0.78);"+
		    "-fx-padding: 25;"+
		    "-fx-border-color: #0b8a90;"+
		    "-fx-border-width: 3;"+
		    "-fx-border-radius: 20;"+
		    "-fx-background-radius: 20;"
		);
		
		this.setRight(rightSide);
		this.setLeft(leftSide);
		this.setCenter(letters.get(currentLetter));
		this.setBottom(footer);
		
		left.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				setCenter(letters.get(nextLetter(-1)));
				letterNumber.setText(currentLetter+1+"");
			}
		});
		right.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				setCenter(letters.get(nextLetter(1)));
				letterNumber.setText(currentLetter+1+"");
			}
		});		
	System.out.println("hello");
	}
}
