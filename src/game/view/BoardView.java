package game.view;

import game.engine.Game;
import game.engine.cells.Cell;
import game.engine.cells.MonsterCell;
import game.engine.monsters.Monster;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;

public class BoardView {

    private Stage stage;

    // ── Live stat labels (Stats panel – right side) ──────────────
    private Label statsEnergyValueLabel;
    private ProgressBar statsEnergyBar;
    private Label statsStatusLabel;
    private Label statsPositionValueLabel;
    private Label statsCellTypeLabel;

    // ── Player card (bottom-left) ────────────────────────────────
    private Label playerNameLabel;
    private Label playerRoleTagLabel;
    private Label playerTurnStatusLabel;

    // ── Opponent card (bottom-right) ─────────────────────────────
    private Label opponentNameLabel;
    private Label opponentRoleTagLabel;
    private Label opponentTurnStatusLabel;

    // ── Turn / last-roll display ──────────────────────────────────
    private Label turnLabel;
    private HBox  diceDisplay;

    // ── Reference so we can pass click-callback ───────────────────
    private BoardGridPane board;

    public BoardView(Stage stage) {
        this.stage = stage;
    }

    public void show(Game game) {

        if (game == null || game.getBoard() == null)
            throw new IllegalStateException("Game not ready");

        // ── Board (center) ───────────────────────────────────────
        board = new BoardGridPane(game);

        // Register click callback so cells update the Stats panel
        board.setCellClickListener((cellNumber, cell) ->
                updateStatsPanel(cellNumber, cell, game)
        );

        // ── Stats Panel (right) ───────────────────────────────────
        VBox statsPanel = buildStatsPanel(game);

        // ── Bottom bar ────────────────────────────────────────────
        HBox bottomBar = buildBottomBar(game);

        // ── Root layout ───────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-root");

        root.setCenter(board);
        root.setRight(statsPanel);
        root.setBottom(bottomBar);

        BorderPane.setMargin(board,      new Insets(12, 0, 0, 12));
        BorderPane.setMargin(statsPanel, new Insets(0));

        // ── Scene ─────────────────────────────────────────────────
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

    // ═══════════════════════════════════════════════════════════════
    //  RIGHT STATS PANEL
    // ═══════════════════════════════════════════════════════════════
    private VBox buildStatsPanel(Game game) {

        // ── Header ────────────────────────────────────────────────
        Label header = new Label("Stats");
        header.getStyleClass().add("stats-header");

        // ── Energy row ────────────────────────────────────────────
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

        // ── Status badge ─────────────────────────────────────────
        statsStatusLabel = new Label("● ACTIVE");
        statsStatusLabel.getStyleClass().add("status-badge-active");

        // ── Position card ─────────────────────────────────────────
        Label posTitle = new Label("Position");
        posTitle.getStyleClass().add("stats-card-title");

        statsPositionValueLabel = new Label(String.valueOf(current.getPosition()));
        statsPositionValueLabel.getStyleClass().add("stats-card-value");

        VBox posCard = new VBox(4, posTitle, statsPositionValueLabel);
        posCard.getStyleClass().add("stats-card");
        posCard.setAlignment(Pos.CENTER);

        // ── Cell info card ────────────────────────────────────────
        Label cellTypeTitle = new Label("Cell Type");
        cellTypeTitle.getStyleClass().add("stats-card-title");

        statsCellTypeLabel = new Label("—");
        statsCellTypeLabel.getStyleClass().add("stats-card-value-small");

        VBox cellCard = new VBox(4, cellTypeTitle, statsCellTypeLabel);
        cellCard.getStyleClass().add("stats-card");
        cellCard.setAlignment(Pos.CENTER);

        // ── Legend ────────────────────────────────────────────────
        Label legendTitle = new Label("LEGEND");
        legendTitle.getStyleClass().add("legend-title");

        VBox legend = new VBox(8,
                legendTitle,
                legendRow("#f5c542", "Laugh Door"),
                legendRow("#9b59b6", "Scare Door"),
                legendRow("#2ecc71", "Conveyor"),
                legendRow("#e67e22", "Hazard")
        );
        legend.getStyleClass().add("legend-box");

        // ── Assemble ─────────────────────────────────────────────
        VBox panel = new VBox(16,
                header,
                energyRow,
                statsEnergyBar,
                statsStatusLabel,
                posCard,
                cellCard,
                legend
        );

        panel.getStyleClass().add("stats-panel");
        panel.setPrefWidth(220);
        panel.setMinWidth(200);
        panel.setPadding(new Insets(24, 20, 24, 20));

        return panel;
    }

    private HBox legendRow(String hex, String text) {
        Circle dot = new Circle(7, Color.web(hex));
        Label lbl  = new Label(text);
        lbl.getStyleClass().add("legend-item-label");
        HBox row = new HBox(10, dot, lbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ═══════════════════════════════════════════════════════════════
    //  BOTTOM BAR  — player card | dice | opponent card
    // ═══════════════════════════════════════════════════════════════
    private HBox buildBottomBar(Game game) {

        // ── Player card ───────────────────────────────────────────
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

        VBox playerInfo = new VBox(2, playerNameLabel, playerRoleTagLabel, playerTurnStatusLabel);
        playerInfo.setAlignment(Pos.CENTER_LEFT);

        HBox playerCard = new HBox(12, playerAvatar, playerInfo);
        playerCard.setAlignment(Pos.CENTER_LEFT);
        playerCard.getStyleClass().add("bottom-player-card");
        playerCard.setPadding(new Insets(10, 20, 10, 16));
        HBox.setHgrow(playerCard, Priority.ALWAYS);

        // ── Center: dice + roll button ────────────────────────────
        diceDisplay = buildDiceDisplay(0);

        turnLabel = new Label("🎯 " + game.getCurrent().getName() + "'s turn");
        turnLabel.getStyleClass().add("bottom-turn-label");

        Button rollBtn = new Button("ROLL DICE");
        rollBtn.getStyleClass().add("roll-button");
        rollBtn.setPrefWidth(130);
        rollBtn.setPrefHeight(42);

        rollBtn.setOnAction(e -> {
            try {
                game.playTurn();
            } catch (Exception ex) {
                System.out.println("Move skipped: " + ex.getMessage());
            }

            board.refresh();

            // Refresh stats panel for the now-current monster
            Monster cur = game.getCurrent();
            updateStatsPanelForMonster(cur, null, game);

            // Refresh bottom cards
            refreshBottomCards(game);

            // Update last dice roll display (last 1-6)
            int lastRoll = game.getLastDiceRoll(); // assumes Game exposes this
            refreshDice(lastRoll);

            turnLabel.setText("🎯 " + cur.getName() + "'s turn");
        });

        VBox centerBox = new VBox(6, diceDisplay, rollBtn, turnLabel);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(8, 24, 8, 24));

        // ── Opponent card ─────────────────────────────────────────
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

        VBox oppInfo = new VBox(2, opponentNameLabel, opponentRoleTagLabel, opponentTurnStatusLabel);
        oppInfo.setAlignment(Pos.CENTER_RIGHT);

        HBox oppCard = new HBox(12, oppInfo, oppAvatar);
        oppCard.setAlignment(Pos.CENTER_RIGHT);
        oppCard.getStyleClass().add("bottom-opponent-card");
        oppCard.setPadding(new Insets(10, 16, 10, 20));
        HBox.setHgrow(oppCard, Priority.ALWAYS);

        // ── Assemble ─────────────────────────────────────────────
        HBox bar = new HBox(0, playerCard, centerBox, oppCard);
        bar.setAlignment(Pos.CENTER);
        bar.getStyleClass().add("bottom-bar");
        bar.setPrefHeight(100);

        return bar;
    }

    // ── Dice pip display ─────────────────────────────────────────
    private HBox buildDiceDisplay(int value) {
        HBox box = new HBox(6);
        box.setAlignment(Pos.CENTER);
        refreshDiceInto(box, value);
        return box;
    }

    private void refreshDiceInto(HBox box, int value) {
        box.getChildren().clear();
        // Show a single die face with the rolled number
        StackPane die = new StackPane();
        die.setPrefSize(52, 52);
        die.getStyleClass().add("dice-face");

        Label val = new Label(value == 0 ? "?" : String.valueOf(value));
        val.getStyleClass().add("dice-value");
        die.getChildren().add(val);
        box.getChildren().add(die);
    }

    private void refreshDice(int value) {
        if (diceDisplay != null) refreshDiceInto(diceDisplay, value);
    }

    // ── Stats panel: clicked cell updates ────────────────────────
    private void updateStatsPanel(int cellNumber, Cell cell, Game game) {

        // Determine which monster is relevant (occupant or player)
        Monster m = null;
        if (cell != null && cell.isOccupied()) {
            m = cell.getMonster();
        } else if (cell instanceof MonsterCell) {
            m = ((MonsterCell) cell).getCellMonster();
        }

        if (m == null) m = game.getCurrent();

        updateStatsPanelForMonster(m, cell, game);

        // Update cell type label
        statsCellTypeLabel.setText(resolveCellTypeName(cellNumber));
    }

    private void updateStatsPanelForMonster(Monster m, Cell cell, Game game) {
        if (m == null) return;

        double pct = clampEnergy(m);
        statsEnergyBar.setProgress(pct);
        statsEnergyValueLabel.setText(formatEnergyPct(pct));

        statsPositionValueLabel.setText(String.valueOf(m.getPosition()));

        // Status
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
        Monster opp = (cur == game.getPlayer()) ? game.getOpponent() : game.getPlayer();

        playerTurnStatusLabel.setText(
                game.getPlayer() == cur ? "Turn Active" : "Waiting..."
        );
        playerTurnStatusLabel.getStyleClass().setAll(
                game.getPlayer() == cur ? "bottom-turn-active" : "bottom-turn-waiting"
        );

        opponentTurnStatusLabel.setText(
                game.getOpponent() == cur ? "Turn Active" : "Waiting..."
        );
        opponentTurnStatusLabel.getStyleClass().setAll(
                game.getOpponent() == cur ? "bottom-turn-active" : "bottom-turn-waiting"
        );
    }

    private String resolveCellTypeName(int cellNumber) {
        if (cellNumber == game.engine.Constants.STARTING_POSITION) return "Start";
        if (cellNumber == game.engine.Constants.WINNING_POSITION)  return "Finish 🏁";
        if (contains(game.engine.Constants.MONSTER_CELL_INDICES,  cellNumber)) return "Monster Cell 👾";
        if (contains(game.engine.Constants.CONVEYOR_CELL_INDICES, cellNumber)) return "Conveyor ⚙️";
        if (contains(game.engine.Constants.SOCK_CELL_INDICES,     cellNumber)) return "Sock 🧦";
        if (contains(game.engine.Constants.CARD_CELL_INDICES,     cellNumber)) return "Card 🃏";
        return "Normal Cell";
    }

    private boolean contains(int[] arr, int n) {
        if (arr == null) return false;
        for (int v : arr) if (v == n) return true;
        return false;
    }

    private double clampEnergy(Monster m) {
        double max = 100.0; // adjust if your engine has a max constant
        double pct = m.getEnergy() / max;
        return Math.max(0, Math.min(1, pct));
    }

    private String formatEnergyPct(double pct) {
        return (int) Math.round(pct * 100) + "%";
    }
}