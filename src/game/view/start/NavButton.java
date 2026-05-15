package game.view.start;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class NavButton extends Button{
	
	public NavButton(String shape){
		super(shape);
		String buttonCss="-fx-background-color: transparent;" +
			    "-fx-text-fill: white;" +
			    "-fx-font-size: 24px;" +
			    "-fx-font-weight: bold;" +
			    "-fx-cursor: hand;";
		this.setStyle(buttonCss);
		DropShadow blueGlow = new DropShadow();
		blueGlow.setColor(Color.CYAN);
		blueGlow.setRadius(25);
		blueGlow.setSpread(0.6);
		
		this.setOnMouseEntered(e ->{
			this.setEffect(blueGlow);
			this.setStyle(
				"-fx-background-color: transparent;" +
				"-fx-background-radius: 20px;" +
				"-fx-font-size: 28px;"+
				"-fx-text-fill: white;" +
				"-fx-cursor: hand;");
			});
		this.setOnMouseExited(e ->{
			this.setEffect(null);
			this.setStyle(buttonCss);
		});
	}
}
