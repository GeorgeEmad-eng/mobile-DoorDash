package game.view.start;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class GameSection{
	private IconLabel title;
	ArrayList<IconLabel> items;
	VBox letter;
    public GameSection(IconLabel title) {
    	this.title=title;
        this.items = new ArrayList<>();
        letter=new VBox();
        this.fillBad();
    }
    
    public IconLabel getTitle() {
		return title;
	}

	public void setTitle(IconLabel title) {
		this.title = title;
	}
	
	public void addItems(IconLabel iconlabel){
		this.items.add(iconlabel);
		letter.getChildren().add(iconlabel);
	}
	public ArrayList<IconLabel> getItems() {
		return items;
	}
	public void fillBad(){
		letter.setStyle(
				"-fx-background-color: linear-gradient(to top, #78708C ,#B38FA9);"+
			    "-fx-padding: 6;"+
			    "-fx-border-radius: 8;" +
		        "-fx-border-color: cyan;" +
		        "-fx-border-width: 1.5;");
		letter.setAlignment(Pos.CENTER);
		//letter.setOpacity(0.67);
		VBox.setMargin(letter, new Insets(1));
	}
}
