package game.view.start;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameSection{
	private IconLabel title;
	ArrayList<IconLabel> items;
	ListView<IconLabel> letter;
	StackPane backWindowPopUp;
    public GameSection(IconLabel title) {
    	this.title=title;
        this.items = new ArrayList<>();
        letter=new ListView<IconLabel>();
        letter.setStyle("-fx-background-color: transparent;"+
        		"-fx-control-inner-background: transparent;");
        letter.setCellFactory(param -> new ListCell<IconLabel>() {
            @Override
            protected void updateItem(IconLabel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(item);
                    }
                }
            });
        backWindowPopUp= new StackPane();
        backWindowPopUp.getChildren().add(letter);
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
		letter.getItems().add(iconlabel);
	}
	public ArrayList<IconLabel> getItems() {
		return items;
	}
	public void fillBad(){
		backWindowPopUp.setStyle("-fx-background-color: rgba(44, 28, 92, 0.89);"+
				"-fx-padding: 25;"+
			    "-fx-border-color: #b8a900;"+
			    "-fx-border-width: 3;"+
			    "-fx-border-radius: 20;"+
			    "-fx-background-radius: 20;");
		backWindowPopUp.setAlignment(Pos.CENTER);
		VBox.setMargin(letter, new Insets(1));
	}
	
	
}
