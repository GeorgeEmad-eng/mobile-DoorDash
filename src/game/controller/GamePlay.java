package game.controller;

import game.engine.Game;
import game.engine.Role;
import game.view.BoardView;
import game.view.DiceView;
import game.view.StartScreen;
//import javafx.application.Application;
import javafx.stage.Stage;

public class GamePlay {

    private final Stage primaryStage;
    private Game gameEngine;
    private BoardView boardView;

    public GamePlay(Stage stage) {
        this.primaryStage = stage;
    }

   
   
    
    public void handleStartGame(Role chosenRole) {
        try {
            // 1. Initialize the Core Model engine
            this.gameEngine = new Game(chosenRole);

            // 2. Initialize the main game view
            this.boardView = new BoardView(primaryStage, this, gameEngine);
            
            // 3. Render the board view
            this.boardView.show();
            
        } catch (Exception ex) {
            System.err.println("Initialization Error starting the game loop: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private boolean isTurnInProgress = false;

    
    /**
     * Fired by the Board View when the "ROLL DICE" action occurs.
     * Prevents button spamming by locking inputs and raising a smooth popup notice.
     */
    public void handleRollDiceAction() {
        if (gameEngine == null || boardView == null) return;
        

        // 🆕 THE SPAM CHECK: If a turn is already processing, raise the warning popup and block execution
        if (isTurnInProgress) {
            boardView.showSpamWarningPopup("Hold on! Wait for the dice to finish rolling.");
            return;
        }

        // 🆕 Lock down the action pipeline
        isTurnInProgress = true;

        try {
            // 1. Advance backend calculations inside the Model engine
            gameEngine.playTurn();
            
            DiceView diceView = boardView.getDiceView();
            int trueRollResult = gameEngine.getLastDiceRoll();

            // 2. Pass control to view animation loop utility
            diceView.playRollAnimation(trueRollResult, () -> {
                
                // 3. CALLBACK ON FINISHED: Redraw the board layout items
                boardView.refreshAllViewComponents();
                
                // 🆕 Unlock the action pipeline so players can roll again safely!
                isTurnInProgress = false;
                
            });

        } catch (Exception ex) {
            System.out.println("Move skipped or rule exception: " + ex.getMessage());
            boardView.refreshAllViewComponents();
            
            // 🆕 Emergency fallback unlock if a core mechanics exception occurs
            isTurnInProgress = false;
        }
    }
    
    public void handleUsePowerupAction() {
        if (gameEngine == null || boardView == null) return;

        try {
          
            gameEngine.usePowerup();
            System.out.println("Power-up is activated successfully by " + gameEngine.getCurrent().getName());
            boardView.refreshAllViewComponents();

        } catch (game.engine.exceptions.OutOfEnergyException ex) {
            System.out.println("Action failed: " + ex.getMessage());
            //visuallalert can bee added here by ashraff
           
        }
    }


//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		StartScreen screen = new StartScreen();
//		
//		
//		primaryStage = screen ;
//		primaryStage.show();
//		
//		
//	}
}