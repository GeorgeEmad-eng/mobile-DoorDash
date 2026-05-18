package game.view;

import game.controller.GamePlay;
import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.engine.cells.Cell;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.monsters.Monster;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URL;

public class BoardView {

	private final Stage stage;
	private final GamePlay controller; 
	private final Game game;             

	// ── Live state labels (States panel – right side) ──────────────
	// ── Live stat layouts (Right side panel) ──────────────────────
	private VBox energySectionContainer; // Wraps title, pct, and progress bar together
	private Label statsEnergyValueLabel;
	private ProgressBar statsEnergyBar;
	private Label statsStatusLabel;
	private Label statsPositionValueLabel;
	private Label statsCellTypeLabel;


	// ── Player UI Cards (Bottom Left) ────────────────────────────
	private Label playerNameLabel;
	private Label playerRoleTagLabel;
	private Label playerTurnStatusLabel;
	private Label playerStatusConditionLabel; // 🆕 Permanent dynamic condition tag
	private ImageView playerCanister;

	// ── Opponent UI Cards (Bottom Right) ──────────────────────────
	private Label opponentNameLabel;
	private Label opponentRoleTagLabel;
	private Label opponentTurnStatusLabel;
	private Label opponentStatusConditionLabel; // 🆕 Permanent dynamic condition tag
	private ImageView oppCanister;
	// ── Mid-control Panels ────────────────────────────────────────
	private Label turnLabel;
	private BoardGridPane board;
	private DiceView diceView;

	public BoardView(Stage stage, GamePlay controller, Game game) {
		this.stage = stage;
		this.controller = controller;
		this.game = game;
	}

	public void show() {
		if (game == null || game.getBoard() == null)
			throw new IllegalStateException("Game model not ready for presentation mapping");

		board = new BoardGridPane(game);

		// UI-bound display selection updates stay local to View presentation mechanics
		board.setCellClickListener((cellNumber, cell) -> updateStatsPanel(cellNumber, cell, game));

		BorderPane statsPanel = buildStatsPanel(game);
		HBox bottomBar = buildBottomBar(game);

		BorderPane root = new BorderPane();
		root.getStyleClass().add("game-root");
		root.setCenter(board);
		root.setRight(statsPanel);
		root.setBottom(bottomBar);

		BorderPane.setMargin(board, new Insets(12, 0, 0, 12));
		BorderPane.setMargin(statsPanel, new Insets(0));

		Scene scene = new Scene(root, 1440, 900);
		URL css = getClass().getResource("/game/view/css/board.css");
		if (css != null) {
			scene.getStylesheets().add(css.toExternalForm());
		} else {
			System.out.println("board.css NOT FOUND");
		}

		stage.setTitle("Monsters, Inc. — Door Dash");
		stage.setMinWidth(1300);
		stage.setMinHeight(820);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Unified programmatic presentation refresh. 
	 * Called exclusively by the controller when backend state alterations occur.
	 */
	public void refreshAllViewComponents() {
		board.refresh();

		Monster currentTurnMonster = game.getCurrent();

		// Reset Right Stats summary panel back to tracking the active moving monster
		updateStatsPanelForMonster(currentTurnMonster, null, game);

		Cell currentCell = board.getCellFromBoard(currentTurnMonster.getPosition());
		statsCellTypeLabel.setText(resolveCellTypeName(currentTurnMonster.getPosition(), currentCell));
		refreshBottomCards(game);

		int lastRollResult = game.getLastDiceRoll();
		//refreshDice(lastRollResult);
		diceView.setDiceValue(lastRollResult);

		turnLabel.setText("🎯 " + currentTurnMonster.getName() + "'s turn");
	}

	private BorderPane buildStatsPanel(Game game) {
		//adels
		// ── 1. Create the Main Wrapper Panel (Now a BorderPane) ──
        BorderPane panel = new BorderPane();
        panel.getStyleClass().add("stats-panel");
        panel.setPrefWidth(220);
        panel.setMinWidth(200);
        panel.setPadding(new Insets(24, 20, 24, 20));
        //end adels
		Label header = new Label("Stats");
		header.getStyleClass().add("stats-header");

		Label energyTitle = new Label("Energy");
		energyTitle.getStyleClass().add("stats-section-label");

		Monster current = game.getCurrent();
		double energyPct = clampEnergy(current);

		statsEnergyBar = new ProgressBar(energyPct);
		statsEnergyBar.getStyleClass().add("energy-bar");
		statsEnergyBar.setMaxWidth(Double.MAX_VALUE);

		statsEnergyValueLabel = new Label(formatEnergyPct(energyPct));
		statsEnergyValueLabel.getStyleClass().add("energy-pct-label");

		HBox energyRow = new HBox(8, energyTitle, statsEnergyValueLabel);
		energyRow.setAlignment(Pos.CENTER_LEFT);

		// 🆕 Wrap Energy Row & Bar inside an independent layout VBox container to show/hide it cleanly
		energySectionContainer = new VBox(8, energyRow, statsEnergyBar);

		statsStatusLabel = new Label("● ACTIVE");
		statsStatusLabel.getStyleClass().add("status-badge-active");

		Label posTitle = new Label("Position");
		posTitle.getStyleClass().add("stats-card-title");

		statsPositionValueLabel = new Label(String.valueOf(current.getPosition()));
		statsPositionValueLabel.getStyleClass().add("stats-card-value");

		VBox posCard = new VBox(4, posTitle, statsPositionValueLabel);
		posCard.getStyleClass().add("stats-card");
		posCard.setAlignment(Pos.CENTER);

		Label cellTypeTitle = new Label("Cell Type");
		cellTypeTitle.getStyleClass().add("stats-card-title");

		statsCellTypeLabel = new Label("—");
		statsCellTypeLabel.getStyleClass().add("stats-card-value-small");

		VBox cellCard = new VBox(4, cellTypeTitle, statsCellTypeLabel);
		cellCard.getStyleClass().add("stats-card");
		cellCard.setAlignment(Pos.CENTER);
		/// adels part 
		// Gather metrics together into a single layout container
        VBox topContent = new VBox(16, header, energySectionContainer, statsStatusLabel, posCard, cellCard);
        panel.setTop(topContent); // Lock it completely to the top boundaries

        // ── 3. Rotated Deck Section (Permanently anchored at the BOTTOM LEFT) ──
        ImageView deckImageView = new ImageView();
     // ✅ Scale it up so it takes up substantial vertical space (acts as height when rotated)
     deckImageView.setFitWidth(380);          
     deckImageView.setFitHeight(220); // Scale the height proportionally        
     deckImageView.setPreserveRatio(true);    
     deckImageView.setSmooth(true);           

     // Apply the 90-degree clockwise rotation
     deckImageView.setRotate(-90);

     try {
         URL deckUrl = getClass().getResource("/assets/deck.png");
         if (deckUrl != null) {
             deckImageView.setImage(new Image(deckUrl.toExternalForm()));
         }
     } catch (Exception e) {
         System.out.println("Could not load deck asset: " + e.getMessage());
     }

     // ✅ THE SECRET INGREDIENT: Wrap it in a Group so JavaFX layout engine
     // respects the 90-degree rotated bounds instead of the original horizontal bounds.
     javafx.scene.Group rotatedGroup = new javafx.scene.Group(deckImageView);

     // Wrap inside a StackPane to anchor the group cleanly in the center of the column
     StackPane deckCenterer = new StackPane(rotatedGroup);
     deckCenterer.setAlignment(Pos.CENTER);

     // Wrap the centerer in your bottom layout container 
     VBox deckWrapper = new VBox(deckCenterer);
     deckWrapper.setAlignment(Pos.BOTTOM_CENTER);

     // ✅ Add comfortable top padding to give it space from the cards above
     deckWrapper.setPadding(new Insets(40, 0, 20, 0)); 

     panel.setBottom(deckWrapper);

        panel.setBottom(deckWrapper);
        return panel;
    }
	////end adels

		//Label legendTitle = new Label("LEGEND");
		//legendTitle.getStyleClass().add("legend-title");
		///no need for this part 
		//        VBox legend = new VBox(8,
		//                legendTitle,
		//                legendRow("#f5c542", "Laugh Door"),
		//                legendRow("#9b59b6", "Scare Door"),
		//                legendRow("#2ecc71", "Conveyor"),
		//                legendRow("#e67e22", "Hazard")
		//        );
		//        legend.getStyleClass().add("legend-box");

		// Assemble right side stack
//		VBox panel = new VBox(16, header, energySectionContainer, statsStatusLabel, posCard, cellCard);
//		panel.getStyleClass().add("stats-panel");
//		panel.setPrefWidth(220);
//		panel.setMinWidth(200);
//		panel.setPadding(new Insets(24, 20, 24, 20));
//
//		return panel;
//	}

	//    private HBox legendRow(String hex, String text) {
	//        Circle dot = new Circle(7, Color.web(hex));
	//        Label lbl = new Label(text);
	//        lbl.getStyleClass().add("legend-item-label");
	//        HBox row = new HBox(10, dot, lbl);
	//        row.setAlignment(Pos.CENTER_LEFT);
	//        return row;
	//    }

	private HBox buildBottomBar(Game game) {
		// ── Player 1 View Construction Setup ──
		StackPane playerAvatar = AvatarUtil.buildAvatar(game.getPlayer(), 52);
		Label p1Badge = new Label("P1");
		p1Badge.getStyleClass().add("player-badge-p1");
		StackPane.setAlignment(p1Badge, Pos.TOP_LEFT);
		playerAvatar.getChildren().add(p1Badge);

		playerNameLabel = new Label(game.getPlayer().getName());
		playerNameLabel.getStyleClass().add("bottom-player-name");

		playerRoleTagLabel = new Label(game.getPlayer().getRole() + "  A");
		playerRoleTagLabel.getStyleClass().add("bottom-role-tag");

		playerTurnStatusLabel = new Label("Turn Active");
		playerTurnStatusLabel.getStyleClass().add("bottom-turn-active");

		// 🆕 Setup status label for P1 card
		playerStatusConditionLabel = new Label("● NORMAL");
		playerStatusConditionLabel.getStyleClass().add("bottom-condition-normal");

		VBox playerInfo = new VBox(2, playerNameLabel, playerRoleTagLabel, playerTurnStatusLabel, playerStatusConditionLabel);
		playerInfo.setAlignment(Pos.CENTER_LEFT);
		playerCanister = new  ImageView();
		playerCanister.setFitWidth(	138);          // Hard limit on width (pixels)
		playerCanister.setFitHeight(155);         // Hard limit on height (pixels)
		playerCanister.setPreserveRatio(true);   // Locks aspect ratio so it doesn't stretch distortionally
		playerCanister.setSmooth(true);          // Smooth resizing algorithm


		HBox playerCard = new HBox(12, playerAvatar, playerInfo,playerCanister);
		playerCard.setAlignment(Pos.CENTER_LEFT);
		playerCard.getStyleClass().add("bottom-player-card");
		playerCard.setPadding(new Insets(10, 20, 10, 16));
		HBox.setHgrow(playerCard, Priority.ALWAYS);



		// ── Turn Management Setup ──
		diceView = new DiceView(); // 🆕 Simply instantiate the custom view
		turnLabel = new Label("🎯 " + game.getCurrent().getName() + "'s turn");
		turnLabel.getStyleClass().add("bottom-turn-label");

		Button rollBtn = new Button("ROLL DICE");
		// ... (styling stays the same)
		rollBtn.setOnAction(e -> controller.handleRollDiceAction());

		// ⚡ Power Up Button Setup
		Button powerupBtn = new Button("USE POWERUP");
		powerupBtn.getStyleClass().add("powerup-button");
		powerupBtn.setPrefWidth(120);
		powerupBtn.setPrefHeight(42);
		powerupBtn.setOnAction(e -> controller.handleUsePowerupAction());

		// 🆕 Side-by-Side Row: Packs the dice face image and both buttons together horizontally
		HBox centerControlsRow = new HBox(14, diceView, rollBtn, powerupBtn);
		centerControlsRow.setAlignment(Pos.CENTER);

		// Stack the horizontal controls row directly on top of the text label
		VBox centerBox = new VBox(8, centerControlsRow, turnLabel);
		centerBox.setAlignment(Pos.CENTER);
		centerBox.setPadding(new Insets(8, 24, 8, 24));

		// ── Player 2 View Construction Setup ──
		StackPane oppAvatar = AvatarUtil.buildAvatar(game.getOpponent(), 52);
		Label p2Badge = new Label("P2");
		p2Badge.getStyleClass().add("player-badge-p2");
		StackPane.setAlignment(p2Badge, Pos.TOP_RIGHT);
		oppAvatar.getChildren().add(p2Badge);

		opponentNameLabel = new Label(game.getOpponent().getName());
		opponentNameLabel.getStyleClass().add("bottom-player-name");

		opponentRoleTagLabel = new Label(game.getOpponent().getRole() + "  B");
		opponentRoleTagLabel.getStyleClass().add("bottom-role-tag");

		opponentTurnStatusLabel = new Label("Waiting...");
		opponentTurnStatusLabel.getStyleClass().add("bottom-turn-waiting");

		// 🆕 Setup status label for P2 card
		opponentStatusConditionLabel = new Label("● NORMAL");
		opponentStatusConditionLabel.getStyleClass().add("bottom-condition-normal");

		VBox oppInfo = new VBox(2, opponentNameLabel, opponentRoleTagLabel, opponentTurnStatusLabel, opponentStatusConditionLabel);
		oppInfo.setAlignment(Pos.CENTER_RIGHT);
		oppCanister =new ImageView();
		oppCanister.setFitWidth(138);          // Hard limit on width (pixels)
		oppCanister.setFitHeight(155);         // Hard limit on height (pixels)
		oppCanister.setPreserveRatio(true);   // Locks aspect ratio so it doesn't stretch distortionally
		oppCanister.setSmooth(true);          // Smooth resizing algorithm

		HBox oppCard = new HBox(12,oppCanister, oppInfo, oppAvatar);
		oppCard.setAlignment(Pos.CENTER_RIGHT);
		oppCard.getStyleClass().add("bottom-opponent-card");
		oppCard.setPadding(new Insets(10, 16, 10, 20));
		HBox.setHgrow(oppCard, Priority.ALWAYS);

		HBox bar = new HBox(0, playerCard, centerBox, oppCard);
		bar.setAlignment(Pos.CENTER);
		bar.getStyleClass().add("bottom-bar");
		bar.setPrefHeight(100);

		// Load the initial static values for the bottom bars
		refreshBottomCards(game);

		return bar;
	}
	///this is callled in the controller
	public DiceView getDiceView() {
		return this.diceView;
	}



	// ── Cell Click Presentation Manager ─────────────────────────
	private void updateStatsPanel(int cellNumber, Cell cell, Game game) {
		Monster m = null;

		// Check if an active moving character is standing on the clicked cell
		if (cell != null && cell.isOccupied()) {
			m = cell.getMonster();
		} 
		// Or check if it's a fixed station monster inside a door cell
		else if (cell instanceof MonsterCell) {
			m = ((MonsterCell) cell).getCellMonster();
		}

		// 1. Update the Cell Type Label dynamically (will now show Door details)
		statsCellTypeLabel.setText(resolveCellTypeName(cellNumber, cell));

		if (m != null) {
			// ─── SHOW MONSTER ENERGY & STATUS ───
			if (!energySectionContainer.isVisible()) {
				energySectionContainer.setManaged(true);
				energySectionContainer.setVisible(true);
				statsStatusLabel.setVisible(true);

				javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
						javafx.util.Duration.millis(250), energySectionContainer
						);
				fadeIn.setFromValue(0.0);
				fadeIn.setToValue(1.0);
				fadeIn.play();
			}
			updateStatsPanelForMonster(m, cell, game);

		} else if (cell instanceof DoorCell) {
			// ─── 🆕 SPECIAL DOOR CELL STATE (NO MONSTER PRESENT) ───
			// Hide the active monster's energy bars since we are inspecting an empty door cell
			energySectionContainer.setVisible(false);
			energySectionContainer.setManaged(false);

			// Keep the status badge visible to display the Door's activation state!
			statsStatusLabel.setVisible(true);
			statsPositionValueLabel.setText(String.valueOf(cellNumber));

			game.engine.cells.DoorCell dc = (game.engine.cells.DoorCell) cell;
			if (dc.isActivated()) {
				statsStatusLabel.setText("❌ EXHAUSTED");
				statsStatusLabel.getStyleClass().setAll("status-badge-frozen"); // Reusing a red/gray style
			} else {
				statsStatusLabel.setText("● DOOR ACTIVE");
				statsStatusLabel.getStyleClass().setAll("status-badge-active"); // Reusing your green active style
			}

		} else {
			// ─── STANDARD EMPTY TILE (SHIFT UP / HIDE ENERGY & STATUS) ───
			if (energySectionContainer.isVisible() || statsStatusLabel.isVisible()) {
				statsStatusLabel.setVisible(false);
				statsPositionValueLabel.setText(String.valueOf(cellNumber));

				javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
						javafx.util.Duration.millis(200), energySectionContainer
						);
				fadeOut.setFromValue(1.0);
				fadeOut.setToValue(0.0);

				fadeOut.setOnFinished(event -> {
					energySectionContainer.setVisible(false);
					energySectionContainer.setManaged(false); 
				});
				fadeOut.play();
			} else {
				statsPositionValueLabel.setText(String.valueOf(cellNumber));
			}
		}
	}
	private void updateStatsPanelForMonster(Monster m, Cell cell, Game game) {
		if (m == null) return;

		double pct = clampEnergy(m);
		statsEnergyBar.setProgress(pct);
		statsEnergyValueLabel.setText(formatEnergyPct(pct));
		statsPositionValueLabel.setText(String.valueOf(m.getPosition()));

		// Assign correct text style classes to side status badge
		if (m.isFrozen()) {
			statsStatusLabel.setText("❄ FROZEN");
			statsStatusLabel.getStyleClass().setAll("status-badge-frozen");
		} else if (m.isShielded()) {
			statsStatusLabel.setText("🛡 SHIELDED");
			statsStatusLabel.getStyleClass().setAll("status-badge-shielded");
		} else if (m.isConfused()) {
			statsStatusLabel.setText("😵 CONFUSED");
			statsStatusLabel.getStyleClass().setAll("status-badge-confused");
		} else {
			statsStatusLabel.setText("● ACTIVE");
			statsStatusLabel.getStyleClass().setAll("status-badge-active");
		}
	}

	private void refreshBottomCards(Game game) {
		Monster cur = game.getCurrent();
		Monster p1 = game.getPlayer();
		Monster p2 = game.getOpponent();

		// 1. Update Turn Status labels
		playerTurnStatusLabel.setText(p1 == cur ? "Turn Active" : "Waiting...");
		playerTurnStatusLabel.getStyleClass().setAll(p1 == cur ? "bottom-turn-active" : "bottom-turn-waiting");

		opponentTurnStatusLabel.setText(p2 == cur ? "Turn Active" : "Waiting...");
		opponentTurnStatusLabel.getStyleClass().setAll(p2 == cur ? "bottom-turn-active" : "bottom-turn-waiting");

		// 2. 🆕 Update Permanent Dynamic Condition for Player 1
		updatePlayerConditionLabel(p1, playerStatusConditionLabel);

		// 3. 🆕 Update Permanent Dynamic Condition for Player 2
		updatePlayerConditionLabel(p2, opponentStatusConditionLabel);
		updateCanisterIcon(p1, playerCanister);
		updateCanisterIcon(p2, oppCanister);
	}

	// 🆕 Helper method to sync condition texts and styles onto specific player badges
	private void updatePlayerConditionLabel(Monster player, Label statusLabel) {
		if (statusLabel == null || player == null) return;

		if (player.isFrozen()) {
			statusLabel.setText("❄ FROZEN");
			statusLabel.getStyleClass().setAll("bottom-condition-frozen");
		} else if (player.isShielded()) {
			statusLabel.setText("🛡 SHIELDED");
			statusLabel.getStyleClass().setAll("bottom-condition-shielded");
		} else if (player.isConfused()) {
			statusLabel.setText("😵 CONFUSED");
			statusLabel.getStyleClass().setAll("bottom-condition-confused");
		} else {
			statusLabel.setText("● NORMAL");
			statusLabel.getStyleClass().setAll("bottom-condition-normal");
		}
	}

	private String resolveCellTypeName(int cellNumber, Cell cell) {
		if (cellNumber == Constants.STARTING_POSITION) return "Start";
		if (cellNumber == Constants.WINNING_POSITION) return "Finish 🏁";

		// ── Now clean, readable, and error-free! ──
		if (cell instanceof DoorCell) {
			DoorCell dc = (DoorCell) cell;
			return (dc.getRole() == Role.SCARER) ? "Scare Door ⚡" : "Laugh Door 😂";
		}

		if (contains(Constants.MONSTER_CELL_INDICES, cellNumber)) return "Monster Cell 👾";
		if (contains(Constants.CONVEYOR_CELL_INDICES, cellNumber)) return "Conveyor ⚙️";
		if (contains(Constants.SOCK_CELL_INDICES, cellNumber)) return "Sock 🧦";
		if (contains(Constants.CARD_CELL_INDICES, cellNumber)) return "Card 🃏";
		return "Normal Cell";
	}

	private boolean contains(int[] arr, int n) {
		if (arr == null) return false;
		for (int v : arr) if (v == n) return true;
		return false;
	}
	// too check energy level ranges and selectr the correct canister image
	private void updateCanisterIcon(Monster m, ImageView canisterView) {
		if (m == null || canisterView == null) return;

		double energy = m.getEnergy();
		String filename;

		// 3 Phases Logic mapping:
		if (energy > 650) {
			filename = "canisterfull.png";   
		} else if (energy > 250) {
			filename = "canistermedium.png";  
		} else {
			filename = "canisterlow.png";  
		}

		try {
			URL url = getClass().getResource("/assets/" + filename);
			if (url != null) {
				Image img = new Image(url.toExternalForm());
				canisterView.setImage(img);
			}
		} catch (Exception e) {
			System.out.println("Could not load canister image asset: " + filename);
		}
	}

	private double clampEnergy(Monster m) {
		double max = 1000.0;
		double pct = m.getEnergy();
		return Math.max(0, pct);
	}

	private String formatEnergyPct(double pct) {
		return (int) Math.round(pct * 1.00) + "";
	}






	////////////////pop up for multiple pressing on the roll dice to make the audio work 
	// prperly laggging 


	public void showSpamWarningPopup(String messageText) {
		// Find the top-most visual layer of your scene layout tree
		if (!(stage.getScene().getRoot() instanceof BorderPane)) return;
		BorderPane rootPane = (BorderPane) stage.getScene().getRoot();

		// Check if a popup warning is already floating on screen to avoid layering copies
		if (rootPane.lookup(".spam-warning-popup") != null) return;

		// Construct the alert container box
		HBox warningPopup = new HBox(12);
		warningPopup.getStyleClass().add("spam-warning-popup");
		warningPopup.setAlignment(Pos.CENTER);
		warningPopup.setMaxWidth(380);
		warningPopup.setMaxHeight(50);
		warningPopup.setPadding(new Insets(12, 20, 12, 20));

		// Text setup inside container
		Label warningLabel = new Label("⚠️ " + messageText);
		warningLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px;");
		warningPopup.getChildren().add(warningLabel);

		// Position it right at the top-center of your window space
		BorderPane.setAlignment(warningPopup, Pos.TOP_CENTER);
		BorderPane.setMargin(warningPopup, new Insets(20, 0, 0, 0));

		// Inject the node onto your main window canvas layout
		rootPane.setTop(warningPopup);

		// ── Smooth Animation Pass ──
		// 1. Initial fade-in entry sequence
		javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
				javafx.util.Duration.millis(200), warningPopup
				);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);

		// 2. Delayed fade-out exit sequence
		javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
				javafx.util.Duration.millis(300), warningPopup
				);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.setDelay(javafx.util.Duration.seconds(1.2)); // Show on screen for 1.2 seconds

		// 3. Clean up: Once completely faded out, completely purge the node layout from view
		fadeOut.setOnFinished(e -> rootPane.setTop(null));

		// Chain animation timelines together sequential path
		fadeIn.setOnFinished(e -> fadeOut.play());
		fadeIn.play();
	}

	public void showVictoryOverlay(String winnerName, int position, int energy) {
	    if (!(stage.getScene().getRoot() instanceof BorderPane)) return;
	    BorderPane rootPane = (BorderPane) stage.getScene().getRoot();

	    // Prevent duplicate overlays if already showing
	    if (rootPane.lookup(".victory-card") != null) return;

	    // 1. Semi-transparent overlay to lightly dim the background board grid
	    StackPane modalityBlocker = new StackPane();
	    modalityBlocker.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
	    modalityBlocker.setAlignment(Pos.CENTER);

	    // 2. Build a standalone card box with a solid dark purple background and bright borders
	    VBox winBox = new VBox(25);
	    winBox.getStyleClass().add("victory-card");
	    winBox.setStyle(
	        "-fx-background-color: #1e1b4b; " + 
	        "-fx-border-color: #00e5d4; " +      
	        "-fx-border-width: 3px; " +
	        "-fx-border-radius: 16px; " +
	        "-fx-background-radius: 16px; " +
	        "-fx-padding: 40px; " +
	        "-fx-effect: dropshadow(three-pass-box, rgba(0, 229, 212, 0.4), 20, 0, 0, 0);"
	    );
	    winBox.setAlignment(Pos.CENTER);
	    winBox.setMaxWidth(500);
	    winBox.setMaxHeight(380);

	    // 3. Header title label using text colors that contrast nicely on screen
	    Label titleLabel = new Label("🎉 VICTORY REACHED! 🎉");
	    titleLabel.setStyle(
	        "-fx-font-family: 'Bangers'; " +
	        "-fx-font-size: 38px; " +
	        "-fx-text-fill: #a8e63d;" 
	    ); 

	    // 4. Main stats statement body
	    Label detailsLabel = new Label(
	        "Congratulations, " + winnerName + "!\n\n" +
	        "🏆 Final Position: " + position + "\n" +
	        "⚡ Energy Collected: " + energy + "\n\n" +
	        "You safely completed the Door Dash loop!"
	    );
	    detailsLabel.setStyle(
	        "-fx-font-family: 'Nunito'; " +
	        "-fx-font-size: 16px; " +
	        "-fx-text-fill: #ffffff; " + 
	        "-fx-text-alignment: center; " +
	        "-fx-line-spacing: 6px; " +
	        "-fx-font-weight: bold;"
	    );

	    // 5. Return to Start Window Button
	    Button closeButton = new Button("BACK TO MAIN MENU");
	    closeButton.getStyleClass().add("mi-button"); 
	    closeButton.setOnAction(e -> {
	        try {
	            // 🆕 Open the Start Screen stage window first
	            StartScreen menu = new StartScreen();
	            menu.show();
	            
	            // 🆕 Close the current running board stage game window loop
	            stage.close();
	        } catch (Exception ex) {
	            System.err.println("Error returning to StartScreen: " + ex.getMessage());
	            ex.printStackTrace();
	            stage.close(); // Fallback safety close
	        }
	    });

	    // Assemble layout items sequentially
	    winBox.getChildren().addAll(titleLabel, detailsLabel, closeButton);
	    modalityBlocker.getChildren().add(winBox);

	    // Inject the clean layout modal covering the central workspace
	    rootPane.setCenter(modalityBlocker);
	}








}



