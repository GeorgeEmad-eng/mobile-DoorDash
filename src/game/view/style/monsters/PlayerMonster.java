package game.view.style.monsters;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class PlayerMonster extends StackPane{
	private static final String[] list={"James P. Sullivan","Mike Wazowski","Randall Boggs","Celia Mae","Roz","Fungus","Henry J. Waternoose","Yeti"};
	private MonsterGUI[] playerList;
	private MonsterGUI player;
	public PlayerMonster(String player ,double cellSize ,String typeColor){
		MonsterGUI sPlayer=new MonsterGUI("s_"+player,cellSize-4,typeColor);
		MonsterGUI oPlayer=new MonsterGUI("o_"+player,cellSize-4,typeColor);
		this.playerList=new MonsterGUI[]{sPlayer,oPlayer};
		this.player=playerList[0];
		this.getChildren().add(this.player);
	}
	
	
	public MonsterGUI getPlayer() {
		return player;
	}


	public void setPlayer(MonsterGUI player) {
		this.player = player;
	}


	public static String[] getList() {
		return list;
	}


	public MonsterGUI[] getPlayerList() {
		return playerList;
	}


	public void openTheDoor(){
		this.player=playerList[1];
		// sound wooa according to the role
		PauseTransition openTimer = new PauseTransition(Duration.millis(1200));
        openTimer.setOnFinished(event -> standUp());
        openTimer.play();
	}
	
	public void moveMonster(double targetX, double targetY ,MonsterGUI monster,String destinationCell){//cell
		player.standFromCell();	
		TranslateTransition slideTransition = new TranslateTransition(Duration.millis(400), monster);
	    slideTransition.setToX(targetX);
	    slideTransition.setToY(targetY);
	
	    slideTransition.setOnFinished(event -> {//other way cell
	    	if (destinationCell.equals("door"))  this.openTheDoor();
	    	player.setInCell();
	    });        
    }
	
	public void standUp(){
		this.player=playerList[0];
	}
	
}
