package game.view;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Popup extends VBox{
	
    private StackPane overlay;
	public Popup(String titleStr){
		super(20);
		
		// Overlay (UNCHANGED)
        overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.6);");
		
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("mi-popup");
        if(titleStr.equals("HOW TO PLAY ?")) this.setMaxSize(950, 650);
        else this.setMaxSize(500, 450);

        this.hidePopup();
        
        Text title = new Text(titleStr);
        title.getStyleClass().add("mi-prompt");

        this.getChildren().add(title);
	}

    public void showPopup() {

        overlay.setVisible(true);
        this.setVisible(true);

        ScaleTransition st = new ScaleTransition(Duration.millis(300), this);
        st.setToX(1);
        st.setToY(1);
        st.play();
    }

    public void hidePopup() {

        overlay.setVisible(false);

        this.setVisible(false);
        this.setScaleX(0);
        this.setScaleY(0);
    }
}
