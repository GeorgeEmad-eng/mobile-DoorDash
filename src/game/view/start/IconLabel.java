package game.view.start;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class IconLabel extends HBox{
		private static final Color FILL_P1=Color.ALICEBLUE;
		private static final Double ICON_OPACITY=0.75;
		private static final String FONT="Impact";
		private static final FontWeight WEIGHT=FontWeight.BOLD;
		private static boolean rev=false;
		private Text txt;
		private Text icon;
		public IconLabel(String sen,String iconFill,HFormat hf,boolean rev,boolean coverIt){
		
			super();
			txt=new Text(sen.substring(2));
			txt.setFill(FILL_P1);
			icon =new Text(sen.substring(0,2));
			icon.setFill(Color.web(iconFill,ICON_OPACITY));
			this.heading(hf);
			if(rev) super.getChildren().addAll(txt,icon);
			else super.getChildren().addAll(icon,txt);
			if(coverIt)this.fillBad();
			
		}
		public IconLabel(String sen,String iconFill){
			this(sen,iconFill,HFormat.DESCRIBTION,rev,true);	
			rev=!rev;
			this.setOnMouseEntered(e->{
				heading(HFormat.ENLARGE);
				fillBad(Color.BLACK,"-fx-border-radius: 28;"
							       +"-fx-border-color: #F0F8FF ;"
							       +"-fx-border-width: 2;");
				//"-fx-background-color: linear-gradient(to bottom, #1b1b2f ,#69888C);"+
				//this.setOpacity(0.28);
				txt.setFill(Color.WHEAT);
			});
			this.setOnMouseExited(new EventHandler<Event>(){

				@Override
				public void handle(Event event) {
					heading(HFormat.DESCRIBTION);
					txt.setFill(FILL_P1);
					fillBad();
				}
				
			});
		}
		
		public void heading(double size,int enlarge){
			
			
			Font f1=Font.font(FONT,WEIGHT, size);
			Font f2=Font.font(FONT,WEIGHT, size+enlarge);
			
			txt.setFont(f1);
			txt.setTranslateY(enlarge/4);
			icon.setFont(f2);
		}
		
		public void heading(HFormat hf){
			final int ICON_ENLARGE=16;
			double size=10;
			switch(hf){
			case TITLE:size=25;break;
			case SUBTITLE:size=18;break;
			case DESCRIBTION:size=16;break;
			case ENLARGE:size=20;break;
			default :System.out.println("not determined size for the HFormat");
			}
			this.heading(size,ICON_ENLARGE);
			
		}
		
		public void fillBad(Color c,String CSS){
			DropShadow whiteGlow = new DropShadow();
			whiteGlow.setColor(c);
			whiteGlow.setRadius(20);
			whiteGlow.setSpread(0.5);
			this.setStyle(CSS);
			this.setAlignment(Pos.CENTER);
			this.setSpacing(7);
			this.setOpacity(2);
			this.setEffect(whiteGlow);
			VBox.setMargin(this, new Insets(0, 0, 5, 0));

		}
		public void fillBad(){
			fillBad(Color.WHITE,"-fx-background-color: linear-gradient(to bottom, #1b1b2f ,#69888C);"
					+"-fx-min-width:700;"
				    +"-fx-max-width:800;");
		}
}
