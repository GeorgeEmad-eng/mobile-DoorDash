package game.view;

import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.engine.cells.Cell;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.monsters.Monster;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.function.BiConsumer;

public class BoardGridPane extends GridPane {

    private final int ROWS = Constants.BOARD_ROWS;
    private final int COLS = Constants.BOARD_COLS;

    private static final double CELL_SIZE = 80;

    private Game game;

    // Callback fired when the user clicks a cell: (cellNumber, cell)
    private BiConsumer<Integer, Cell> cellClickListener;

    public BoardGridPane(Game game) {
        this.game = game;

        setHgap(4);
        setVgap(4);
        setAlignment(Pos.CENTER);
        

        if (game == null || game.getBoard() == null) {
            System.out.println("ERROR: Game or Board is NULL");
            return;
        }

        drawBoard();
    }

    /** Register the callback that fires when a cell is clicked. */
    public void setCellClickListener(BiConsumer<Integer, Cell> listener) {
        this.cellClickListener = listener;
    }

    // ── Helpers ──────────────────────────────────────────────────

    private boolean contains(int[] arr, int n) {
        if (arr == null) return false;
        for (int v : arr) if (v == n) return true;
        return false;
    }

    public Cell getCellFromBoard(int index) {
        if (game == null || game.getBoard() == null) return null;
        int row = index / COLS;
        int col = index % COLS;
        if (row % 2 == 1) col = COLS - 1 - col;
        Cell[][] board = game.getBoard().getBoardCells();
        if (board == null || board[row] == null) return null;
        return board[row][col];
    }

    private int getCellNumber(int boardRow, int col) {
        int base = boardRow * COLS;
        return (boardRow % 2 == 0) ? base + col : base + (COLS - 1 - col);
    }

    // ── Cell type → CSS class ────────────────────────────────────
    private String getCellStyle(int n,Cell cell) {
        if (n == Constants.STARTING_POSITION)                 return "start-cell";
        if (n == Constants.WINNING_POSITION)                  return "end-cell";
        if (contains(Constants.MONSTER_CELL_INDICES,  n))     return "monster-cell";
        if (contains(Constants.CONVEYOR_CELL_INDICES, n))     return "conveyor-cell";
        if (contains(Constants.SOCK_CELL_INDICES,     n))     return "sock-cell";
        if (contains(Constants.CARD_CELL_INDICES,     n))     return "card-cell";
        if (cell instanceof DoorCell) {
            DoorCell dc = (DoorCell) cell;
            return dc.getRole() == Role.SCARER ? "scare-door-cell" : "laugh-door-cell";
        }
        return "normal-cell";
    }

    // ── Cell type → icon character drawn inside the cell ─────────
    private String getCellIcon(int n,Cell cell) {
        if (n == Constants.STARTING_POSITION)                 return "🏠";
        if (n == Constants.WINNING_POSITION)                  return "🏁";
        if (contains(Constants.MONSTER_CELL_INDICES,  n))     return "⚡";   // door icon
        if (contains(Constants.CONVEYOR_CELL_INDICES, n))     return "→";
        if (contains(Constants.SOCK_CELL_INDICES,     n))     return "🧦";
        if (contains(Constants.CARD_CELL_INDICES,     n))     return "🃏";
        if (cell instanceof DoorCell) {
            DoorCell dc = (DoorCell) cell;
            if (dc.isActivated()) return "❌"; // Cross indicator if door is used/exhausted
            return dc.getRole() == Role.SCARER ? "⚡" : "😂"; 
        }
        return null;
    }

    // ── Cell type → background image file ────────────────────────
    private String getCellImageFilename(int n,Cell cell) {
        if (n == Constants.STARTING_POSITION)                 return "start.png";
        if (n == Constants.WINNING_POSITION)                  return "end.png";
        if (contains(Constants.CONVEYOR_CELL_INDICES, n))     return "conveyor.png";
        if (contains(Constants.SOCK_CELL_INDICES,     n))     return "sock.png";
        if (contains(Constants.CARD_CELL_INDICES,     n))     return "card.png";
        if (cell instanceof DoorCell) {
            DoorCell dc = (DoorCell) cell;
            if (dc.isActivated()) {
                return "door_exhausted.png"; // Renders used up door look
            }
            return dc.getRole() == Role.SCARER ? "scare_door.png" : "laugh_door.png";
        }
        return null;
    }

    private ImageView loadCellImage(String filename) {
        URL url = getClass().getResource("/assets/" + filename);
        if (url == null) return null;
        try {
            Image img = new Image(url.toExternalForm(), CELL_SIZE*2, CELL_SIZE, false, true);
            if (img.isError()) return null;
            ImageView iv = new ImageView(img);
            iv.setFitWidth(CELL_SIZE*2);
            iv.setFitHeight(CELL_SIZE);
            iv.setPreserveRatio(false);
            iv.setSmooth(true);
            return iv;
        } catch (Exception e) {
            return null;
        }
    }

    // ── Monster label (player/opponent token) ────────────────────
    private Label buildMonsterLabel(Cell cell, Monster player, Monster opponent) {
        if (cell == null || !cell.isOccupied()) return null;
        Monster m = cell.getMonster();
        if (m == null) return null;

        Label label = new Label();
        label.getStyleClass().add("monster-label");
        label.getStyleClass().add("monster-name-label");

        if (m == player) {
            label.setText("P1");
            label.getStyleClass().add("monster-player");
        } else if (m == opponent) {
            label.setText("P2");
            label.getStyleClass().add("monster-opponent");
        }

        StackPane.setAlignment(label, Pos.TOP_RIGHT);
        return label;
    }

    // ── Draw all cells ───────────────────────────────────────────
    private void drawBoard() {
        if (game == null || game.getBoard() == null) return;

        Monster player   = game.getPlayer();
        Monster opponent = game.getOpponent();

        for (int boardRow = 0; boardRow < ROWS; boardRow++) {
            int gridRow = (ROWS - 1) - boardRow;

            for (int col = 0; col < COLS; col++) {
                int  cellNumber = getCellNumber(boardRow, col);
                Cell cell       = getCellFromBoard(cellNumber);

                StackPane cellPane = new StackPane();
                cellPane.setPrefSize(CELL_SIZE*2, CELL_SIZE);
                cellPane.setMinSize(CELL_SIZE, CELL_SIZE);
                cellPane.setMaxSize(CELL_SIZE*2, CELL_SIZE);
                cellPane.getStyleClass().add("board-cell");
                cellPane.getStyleClass().add(getCellStyle(cellNumber,cell));

                // ── Layer 1: background image ─────────────────────
                String imgFile = getCellImageFilename(cellNumber,cell);
                if (imgFile != null) {
                    ImageView bg = loadCellImage(imgFile);
                    if (bg != null) {
                        StackPane.setAlignment(bg, Pos.CENTER);
                        cellPane.getChildren().add(bg);
                    }
                }

                // ── Layer 2: cell number top-left ─────────────────
                Label coordLabel = new Label(String.valueOf(cellNumber));
                coordLabel.getStyleClass().add("coord-label");
                StackPane.setAlignment(coordLabel, Pos.TOP_LEFT);
                cellPane.getChildren().add(coordLabel);

                // ── Layer 3: special cell icon (center) ───────────
                String icon = getCellIcon(cellNumber,cell);
                if (icon != null && imgFile == null) {
                    // Only show icon if no image is covering it
                    Label iconLabel = new Label(icon);
                    iconLabel.getStyleClass().add("cell-icon-label");
                    StackPane.setAlignment(iconLabel, Pos.CENTER);
                    cellPane.getChildren().add(iconLabel);
                }

                // ── Layer 4: monster-cell stationed avatar ─────────
                if (cell instanceof MonsterCell) {
                    MonsterCell mc = (MonsterCell) cell;
                    Monster stationed = mc.getCellMonster();
                    if (stationed != null) {
                        // Show a compact icon box matching the reference image style
                        StackPane iconBox = buildDoorIconBox(cellNumber, stationed);
                        StackPane.setAlignment(iconBox, Pos.CENTER);
                        cellPane.getChildren().add(iconBox);
                    }
                }

                // ── Layer 5: active P1/P2 badge ───────────────────
                Label monsterLabel = buildMonsterLabel(cell, player, opponent);
                if (monsterLabel != null)
                    cellPane.getChildren().add(monsterLabel);

                // ── Click → update Stats panel ────────────────────
                final int cn   = cellNumber;
                final Cell cel = cell;
                cellPane.setOnMouseClicked(e -> {
                    if (cellClickListener != null) {
                        cellClickListener.accept(cn, cel);
                    }
                    // Highlight selected
                    cellPane.getStyleClass().remove("cell-selected");
                    cellPane.getStyleClass().add("cell-selected");
                });

                add(cellPane, col, gridRow);
            }
        }
    }

    private StackPane buildDoorIconBox(
            int cellNumber,
            Monster stationed
    ) {

        boolean isScareDoor =
                contains(
                        Constants.MONSTER_CELL_INDICES,
                        cellNumber
                );

        String boxStyle =
                isScareDoor
                        ? "door-box-scare"
                        : "door-box-laugh";

        StackPane box = new StackPane();

        // Bigger container
        box.setPrefSize(100, 100);

        box.setMinSize(78, 78);

        box.setMaxSize(78, 78);

        box.getStyleClass().add("door-box");

        box.getStyleClass().add(boxStyle);

        // Bigger avatar
        StackPane avatar =
                AvatarUtil.buildAvatar(stationed, 64);

        StackPane.setAlignment(
                avatar,
                Pos.CENTER
        );

        box.getChildren().add(avatar);

        return box;
    }

    public void refresh() {
        getChildren().clear();
        drawBoard();
    }
}