package game.view.style.monsters;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;


public class MonsterGUI extends StackPane{
	private StackPane freezeLap;
	private ImageView monImage;
	private Label energyEffect;
	private Label role;
	private String typeColor;
	private double startYenergyEffect;
	private double endYenergyEffect;
	private double size;
	public MonsterGUI(String name,double size ,String typeColor){
		super();
		this.size=size;
		this.startYenergyEffect=-0.125*size;
		this.endYenergyEffect=-0.5*size;
		Image monImg = new Image(getClass().getResourceAsStream("/assets/"+name+".png"));
		monImage=new ImageView(monImg);
		energyEffect=new Label();
		energyEffect.setTranslateX(size*0.2);
		energyEffect.setVisible(false);
		freezeLap=new StackPane();
		iceCover();
		
		double right=(name.equals("s_Randall Boggs"))?-1:1;
		if(right==-1){
			monImage.setTranslateY(-18);
		}
		role =new Label();
		role.setTranslateX(size*-0.265*right);
		role.setTranslateY(-0.4*size);
		
		this.typeColor=typeColor;
		
		monSet();//
		monImage.setFitHeight(size*1.5);
		System.out.println(monImg.getWidth());
		if(monImg.getWidth()<500 ||right==-1){
			monImage.setPreserveRatio(true);
			monImage.setFitWidth(size);
		}else if(monImg.getWidth()==900){
			if(name.equals("s_Celia Mae")){
				monImage.setFitWidth(size*2);
				monImage.setFitHeight(size*1.25);
			}
			else
				monImage.setFitWidth(size*1.85);
			monImage.setFitHeight(size*1.8);
		}else if(monImg.getWidth()<900){
			monImage.setFitWidth(size*1.3);
			monImage.setFitHeight(size*1.45);
		}else{
			monImage.setFitWidth(size*1.6);
			monImage.setFitHeight(size*1.8);
		}
        monImage.setSmooth(true);
        
        this.getChildren().addAll(monImage,energyEffect,role,freezeLap);
        setAlignment(monImage, Pos.CENTER);
	}
	
	private void iceCover(){
		Rectangle ice =new Rectangle(size*1.05,size*1.28,Color.rgb(161, 229, 247, 0.48));
		ice.setArcHeight(17);
		ice.setArcWidth(28);
		ice.setTranslateY(-size/8+5);
		freezeLap.getChildren().add(ice);
		freezeLap.setVisible(false);
		
	}
	
	public void coldFreeze(){
		//this.freeze();
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
	
	public void setInCell(){
		Rectangle clip =new Rectangle(size*1.8,size*1.6);
		clip.setTranslateY(-size/8-8);
		this.setClip(clip);
	}
	
	void standFromCell(){//cell
		this.setClip(null);
		
	}
	
	public void monSet(){
		glow(2);
		this.setStyle(null);
	}
	
	public void affect(int amount) {
        if (amount >= 0) {
        	heading("+"+amount,"#00ff0f",25);
        } else {
    		heading(""+amount,"#ff0f0f",25);
        }
        energyEffect.setVisible(true);
        
        TranslateTransition floatUp = new TranslateTransition(Duration.millis(2000), energyEffect);
        floatUp.setFromY(startYenergyEffect);
        floatUp.setToY(endYenergyEffect);  // Float upwards above the monster's head
        
        PauseTransition pause = new PauseTransition(Duration.millis(3200));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(2000), energyEffect);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);// Creates the Dissolve/Fade Effect
        
        ParallelTransition composition = new ParallelTransition(floatUp,pause,fadeOut);
        //Execute transitions in parallel
        
        composition.setOnFinished(event -> energyEffect.setVisible(false));
        composition.play(); 
	}
	
	private void heading(String energy,String color,double size){
		String FONT="Forte";
		Font f1=Font.font(FONT,FontWeight.BOLD, size);
		this.energyEffect.setFont(f1);
		energyEffect.setText(energy);
		energyEffect.setStyle("-fx-text-fill: linear-gradient(to bottom, "+color+" ,#69888C);");
	}	//monImage.setEffect(harm);

	public void freeze(){
		glow(0);
		ColorAdjust c = new ColorAdjust();  
	    c.setBrightness(0.2);
	    c.setContrast(0.1);  
	    c.setSaturation(0.05);  
	    monImage.setEffect(c);
		this.getStyleClass().add("freezed");
		
	}
	
	DropShadow glow(double factor){
		DropShadow glow = new DropShadow();
		glow.setColor(Color.web(typeColor, 0.3333*factor));
		glow.setRadius(20+factor*5);
		glow.setSpread(0.6);
		monImage.setEffect(glow);
		return glow;
	}
	
	public void powerUPeffect(){
		DropShadow glowing=glow(2.8);
		Bloom bloom = new Bloom();  
		bloom.setThreshold(0.008);
		bloom.setInput(glowing);
		monImage.setEffect(bloom);  
		monImage.getStyleClass().add("powerup");
	}
	
	public void scarerGUI(){//this.getStyleClass().add("monster-token");
		role.setText("🦇");
		role.getStyleClass().add("scarer-active");
		
	}
	
	public void laugherGUI(){
		role.setText("😂");
		role.setTranslateX(size*-0.5);
		role.setTranslateY(size*-0.5);
		role.getStyleClass().add("laugher-active");
	}
	
}