package game.view.style.monsters;

import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class MonstersGroup {
	private double cellSize;
	private StackPane freezeLap;
	
	private MonsterGUI player;
	private MonsterGUI opponent;
	private MonsterGUI[] playerList;
	private MonsterGUI[] opponentList;
	private static ArrayList<MonsterGUI> stationed_monsters;
	public MonstersGroup(String player ,String opponent ,ArrayList<String> stationed_monsters,double cellSize ,String typeColor){
		this.cellSize=cellSize;
		MonsterGUI sPlayer=new MonsterGUI("s_"+player,cellSize,typeColor);
		MonsterGUI oPlayer=new MonsterGUI("o_"+player,cellSize,typeColor);
		this.playerList=new MonsterGUI[]{sPlayer,oPlayer};
		this.player=playerList[0];
		freezeLap=new StackPane();
		iceCover();
		
		MonsterGUI sOpponent=new MonsterGUI("s_"+opponent,cellSize,typeColor);
		MonsterGUI oOpponent=new MonsterGUI("o_"+opponent,cellSize,typeColor);
		this.opponentList=new MonsterGUI[]{sOpponent,oOpponent};
		this.opponent=opponentList[0];
		
		this.stationed_monsters=new ArrayList<MonsterGUI>();
		for (String monster : stationed_monsters) {
			MonsterGUI monsterS=new MonsterGUI("s_"+monster,cellSize,typeColor);
			this.stationed_monsters.add(monsterS);
		}
	}

	private void iceCover(){
		Rectangle ice =new Rectangle(cellSize*1.05,cellSize*1.28,Color.rgb(161, 229, 247, 0.48));
		ice.setArcHeight(17);
		ice.setArcWidth(28);
		ice.setTranslateY(-cellSize/8+5);
		freezeLap.getChildren().add(ice);
		freezeLap.setVisible(false);
		
	}
	public void coldFreeze(MonsterGUI monster){
		
		monster.freeze();
		freezeLap.setVisible(true);
		freezeLap.setOnMouseEntered(e->{
			//Label onIce= new Label("Freezing"); 
			Glow glow = new Glow();   
			glow.setLevel(8);
			freezeLap.setEffect(glow);
			//freezeLap.getChildren().add(onIce);
		});
		freezeLap.setOnMouseExited(e->freezeLap.setEffect(null));
	}
	
	public void setInCell(MonsterGUI monster){
		Rectangle clip =new Rectangle(cellSize*1.3,cellSize*1.5);
		clip.setTranslateY(-cellSize/8-10);
		monster.setClip(clip);
	}
	
	public MonsterGUI getPlayer() {
		return player;
	}
	public MonsterGUI getOpponent() {
		return opponent;
	}
	public ArrayList<MonsterGUI> getStationed_monsters() {
		return stationed_monsters;
	}

	private void standFromCell(MonsterGUI monster){//cell
		monster.setClip(null);
		
	}
	
	public void moveMonster(double targetX, double targetY ,MonsterGUI monster,String destinationCell){//cell
		this.standFromCell(monster);	
		TranslateTransition slideTransition = new TranslateTransition(Duration.millis(400), monster);
	    slideTransition.setToX(targetX);
	    slideTransition.setToY(targetY);
	
	    slideTransition.setOnFinished(event -> {//other way cell
	    	if (destinationCell.equals("door"))  this.openTheDoor(monster);
	    	this.setInCell(monster);
	    });
	        
    }
	public void openTheDoor(MonsterGUI monster){
		changeImage(monster, 1);
		// sound wooa according to the role
		PauseTransition openTimer = new PauseTransition(Duration.millis(1200));
        openTimer.setOnFinished(event -> standUp(monster));
        openTimer.play();
	}
	
	public void standUp(MonsterGUI monster){
		changeImage(monster, 0);
	}
	
	private void changeImage(MonsterGUI monster , int indx){
		if(this.player==monster)this.player=playerList[indx];
		else if(this.opponent==monster)this.opponent=opponentList[indx]; 
	}
	
	
}
