package com.doordash.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.engine.cards.Card;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.dataloader.DataLoader;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;

public class MainActivity extends Activity {
    private static final int NAVY = Color.rgb(18, 53, 91);
    private static final int DARK = Color.rgb(13, 17, 23);
    private static final int PANEL = Color.rgb(22, 27, 34);
    private static final int INK = Color.rgb(23, 30, 40);
    private static final int MINT = Color.rgb(14, 124, 123);
    private static final int GOLD = Color.rgb(239, 184, 76);
    private static final int RED = Color.rgb(179, 59, 59);

    private Game game;
    private GameBoardView boardView;
    private ImageView diceImageView;
    private TextView turnText;
    private TextView playerText;
    private TextView opponentText;
    private TextView lastActionText;
    private LinearLayout playerArt;
    private LinearLayout opponentArt;
    private final Map<String, Bitmap> bitmapCache = new HashMap<String, Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataLoader.initialize(this);
        showRoleSelection();
    }

    private void showRoleSelection() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));
        root.setBackgroundColor(DARK);

        ImageView startImage = new ImageView(this);
        Bitmap startBitmap = loadBitmap("assets/start.png");
        if (startBitmap != null) {
            startImage.setImageBitmap(startBitmap);
            startImage.setAdjustViewBounds(true);
            root.addView(startImage, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(220)
            ));
        }

        TextView title = new TextView(this);
        title.setText("Door Dash");
        title.setTextColor(Color.WHITE);
        title.setTextSize(34);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        root.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("Choose your team");
        subtitle.setTextColor(Color.rgb(201, 209, 217));
        subtitle.setTextSize(18);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(4), 0, dp(20));
        root.addView(subtitle, matchWrap());

        Button scarer = primaryButton("Play as Scarer");
        scarer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(Role.SCARER);
            }
        });
        root.addView(scarer, buttonParams());

        Button laugher = primaryButton("Play as Laugher");
        laugher.setBackgroundColor(MINT);
        laugher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(Role.LAUGHER);
            }
        });
        root.addView(laugher, buttonParams());

        setContentView(root);
    }

    private void startGame(Role role) {
        try {
            game = new Game(role);
            showGameScreen();
            refreshUi("Game started. Roll the dice when you are ready.");
        } catch (Exception exception) {
            showError("Could not start game", exception.getMessage());
        }
    }

    private void showGameScreen() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(DARK);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12), dp(12), dp(12), dp(18));
        scrollView.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView title = new TextView(this);
        title.setText("Door Dash");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        root.addView(title, matchWrap());

        turnText = label("", 17, Color.rgb(88, 166, 255), true);
        root.addView(turnText, matchWrap());

        LinearLayout artRow = new LinearLayout(this);
        artRow.setOrientation(LinearLayout.HORIZONTAL);
        artRow.setGravity(Gravity.CENTER);
        artRow.setPadding(0, dp(8), 0, dp(8));
        playerArt = monsterPanel();
        opponentArt = monsterPanel();
        artRow.addView(playerArt, weightWrap(1));
        artRow.addView(opponentArt, weightWrap(1));
        root.addView(artRow, matchWrap());

        playerText = label("", 14, Color.rgb(201, 209, 217), false);
        opponentText = label("", 14, Color.rgb(201, 209, 217), false);
        root.addView(playerText, matchWrap());
        root.addView(opponentText, matchWrap());

        boardView = new GameBoardView(this);
        root.addView(boardView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER);
        controls.setPadding(0, dp(12), 0, dp(8));

        diceImageView = new ImageView(this);
        diceImageView.setImageBitmap(loadBitmap("assets/dice_1.png"));
        diceImageView.setAdjustViewBounds(true);
        diceImageView.setPadding(dp(6), dp(6), dp(6), dp(6));
        diceImageView.setBackgroundColor(PANEL);
        diceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rollDice();
            }
        });
        controls.addView(diceImageView, new LinearLayout.LayoutParams(dp(76), dp(76)));

        Button power = primaryButton("Power Up");
        power.setBackgroundColor(Color.rgb(124, 58, 237));
        power.setTextColor(Color.WHITE);
        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usePowerup();
            }
        });
        controls.addView(power, weightButton());
        root.addView(controls, matchWrap());

        Button newGame = primaryButton("New Game");
        newGame.setBackgroundColor(INK);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRoleSelection();
            }
        });
        root.addView(newGame, buttonParams());

        lastActionText = label("", 14, Color.rgb(201, 209, 217), false);
        lastActionText.setPadding(0, dp(8), 0, 0);
        root.addView(lastActionText, matchWrap());

        setContentView(scrollView);
    }

    private void rollDice() {
        if (game == null) {
            return;
        }

        Monster mover = game.getCurrent();
        int from = mover.getPosition();
        try {
            game.playTurn();
            int roll = game.getLastDiceRoll();
            updateDiceFace(roll);
            playDiceSound();
            Card card = Board.getLastDrawnCard();
            String message = mover.getName() + " rolled " + roll + " and moved from "
                    + from + " to " + mover.getPosition() + ".";

            if (card != null) {
                message += "\nCard: " + card.getName() + " - " + card.getDescription();
                showCard(card);
            }

            refreshUi(message);
            checkWinner();
        } catch (Exception exception) {
            updateDiceFace(game.getLastDiceRoll());
            refreshUi("Turn skipped: " + exception.getMessage());
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void usePowerup() {
        if (game == null) {
            return;
        }

        Monster user = game.getCurrent();
        try {
            game.usePowerup();
            refreshUi(user.getName() + " used a power-up.");
            checkWinner();
        } catch (OutOfEnergyException exception) {
            Toast.makeText(this, "Not enough energy for a power-up.", Toast.LENGTH_SHORT).show();
            refreshUi(exception.getMessage());
        }
    }

    private void refreshUi(String message) {
        if (game == null) {
            return;
        }

        Monster player = game.getPlayer();
        Monster opponent = game.getOpponent();
        Monster current = game.getCurrent();

        turnText.setText("Turn: " + current.getName() + " (" + current.getRole() + ")");
        playerText.setText(stats("You", player));
        opponentText.setText(stats("Opponent", opponent));
        lastActionText.setText(message);
        bindMonsterPanel(playerArt, "You", player);
        bindMonsterPanel(opponentArt, "Opponent", opponent);
        boardView.invalidate();
    }

    private void updateDiceFace(int value) {
        if (diceImageView == null) {
            return;
        }

        String asset = diceAsset(value);
        Bitmap bitmap = loadBitmap(asset);
        if (bitmap != null) {
            diceImageView.setImageBitmap(bitmap);
        }
    }

    private String diceAsset(int value) {
        if (value == 4) {
            return "assets/dice _4.png";
        }

        return value >= 1 && value <= 6
                ? "assets/dice_" + value + ".png"
                : "assets/dice_blank.png";
    }

    private void playDiceSound() {
        try {
            AssetFileDescriptor descriptor = getAssets().openFd("assets/dice_roll.mp3");
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });
            player.prepare();
            player.start();
        } catch (Exception ignored) {
        }
    }

    private String stats(String label, Monster monster) {
        return String.format(Locale.US, "%s: %s | %s | Energy %d | Cell %d",
                label,
                monster.getName(),
                monster.getRole(),
                monster.getEnergy(),
                monster.getPosition());
    }

    private void showCard(Card card) {
        new AlertDialog.Builder(this)
                .setTitle(card.getName())
                .setMessage(card.getDescription())
                .setPositiveButton("OK", null)
                .show();
    }

    private void checkWinner() {
        Monster winner = game.getWinner();
        if (winner == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Victory")
                .setMessage(winner.getName() + " wins with " + winner.getEnergy() + " energy.")
                .setPositiveButton("New Game", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        showRoleSelection();
                    }
                })
                .setNegativeButton("Stay", null)
                .show();
    }

    private void showError(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message == null ? "Unknown error" : message)
                .setPositiveButton("OK", null)
                .show();
    }

    private LinearLayout monsterPanel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER);
        panel.setPadding(dp(6), 0, dp(6), 0);
        return panel;
    }

    private void bindMonsterPanel(LinearLayout panel, String title, Monster monster) {
        panel.removeAllViews();

        ImageView image = new ImageView(this);
        Bitmap bitmap = loadBitmap(assetForMonster(monster));
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        }
        image.setAdjustViewBounds(true);
        panel.addView(image, new LinearLayout.LayoutParams(dp(96), dp(96)));

        TextView text = label(title + "\n" + monster.getName(), 12, Color.rgb(201, 209, 217), true);
        text.setGravity(Gravity.CENTER);
        panel.addView(text, matchWrap());
    }

    private String assetForMonster(Monster monster) {
        String name = monster.getName();
        String boardAsset = boardAssetForMonster(name);
        if (boardAsset != null && loadBitmap(boardAsset) != null) return boardAsset;
        if ("Mike Wazowski".equals(name)) return "assets/mikewazowski.png";
        if ("James P. Sullivan".equals(name)) return "assets/jamespsullivan.png";
        if ("Henry J. Waternoose".equals(name)) return "assets/henryjwaternoose.png";
        if ("Fungus".equals(name)) return "assets/fungus.png";
        if ("Roz".equals(name)) return "assets/roz.png";
        if ("Yeti".equals(name)) return "assets/yeti.png";
        if ("Randall Boggs".equals(name)) return "assets/s_Randall Boggs.png";
        if ("Celia Mae".equals(name)) return "assets/s_Celia Mae.png";
        return "assets/canister.png";
    }

    private String boardAssetForMonster(String name) {
        if ("Mike Wazowski".equals(name)) return "assets/s_Mike Wazowski.PNG";
        if ("James P. Sullivan".equals(name)) return "assets/s_James P. Sullivan.PNG";
        if ("Henry J. Waternoose".equals(name)) return "assets/s_Henry J. Waternoose.PNG";
        if ("Fungus".equals(name)) return "assets/s_Fungus.png";
        if ("Roz".equals(name)) return "assets/s_Roz.png";
        if ("Yeti".equals(name)) return "assets/s_Yeti.png";
        if ("Randall Boggs".equals(name)) return "assets/s_Randall Boggs.png";
        if ("Celia Mae".equals(name)) return "assets/s_Celia Mae.png";
        return null;
    }

    private Bitmap loadBitmap(String assetPath) {
        if (assetPath == null) {
            return null;
        }

        if (bitmapCache.containsKey(assetPath)) {
            return bitmapCache.get(assetPath);
        }

        try {
            InputStream stream = getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            bitmapCache.put(assetPath, bitmap);
            return bitmap;
        } catch (Exception ignored) {
            bitmapCache.put(assetPath, null);
            return null;
        }
    }

    private Button primaryButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(15);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setBackgroundColor(NAVY);
        button.setMinHeight(dp(48));
        return button;
    }

    private TextView label(String text, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) {
            view.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        }
        return view;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    private LinearLayout.LayoutParams weightWrap(float weight) {
        return new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight);
    }

    private LinearLayout.LayoutParams weightButton() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(52), 1);
        params.setMargins(dp(4), 0, dp(4), 0);
        return params;
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(52)
        );
        params.setMargins(0, dp(6), 0, dp(6));
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private class GameBoardView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();
        private final Rect srcRect = new Rect();
        private final RectF dstRect = new RectF();

        GameBoardView(Activity activity) {
            super(activity);
            setMinimumHeight(dp(340));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, width);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (game == null) {
                return;
            }

            float boardSize = Math.min(getWidth(), getHeight());
            float cell = boardSize / Constants.BOARD_COLS;
            canvas.drawColor(DARK);

            for (int index = 0; index < Constants.BOARD_SIZE; index++) {
                int[] rowCol = cellToScreenRowCol(index);
                float left = rowCol[1] * cell;
                float top = rowCol[0] * cell;
                rect.set(left + 1, top + 1, left + cell - 1, top + cell - 1);

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(colorForCell(game.getBoard().getCell(index)));
                canvas.drawRoundRect(rect, dp(4), dp(4), paint);

                Bitmap cellBitmap = loadBitmap(assetForCell(index, game.getBoard().getCell(index)));
                if (cellBitmap != null) {
                    srcRect.set(0, 0, cellBitmap.getWidth(), cellBitmap.getHeight());
                    dstRect.set(rect);
                    canvas.drawBitmap(cellBitmap, srcRect, dstRect, paint);
                }

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(1));
                paint.setColor(borderColorForCell(index, game.getBoard().getCell(index)));
                canvas.drawRoundRect(rect, dp(4), dp(4), paint);

                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(cell * 0.22f);
                paint.setColor(Color.argb(190, 235, 240, 246));
                canvas.drawText(String.valueOf(index), left + cell * 0.10f, top + cell * 0.28f, paint);

                drawStationedMonster(canvas, index, cell);
            }

            drawTransportArrows(canvas, cell);
            drawMonster(canvas, game.getPlayer(), cell, true);
            drawMonster(canvas, game.getOpponent(), cell, false);
        }

        private int colorForCell(Cell cell) {
            if (cell instanceof DoorCell) return Color.rgb(20, 35, 55);
            if (cell instanceof CardCell) return Color.rgb(28, 18, 0);
            if (cell instanceof ConveyorBelt) return Color.rgb(10, 31, 15);
            if (cell instanceof ContaminationSock) return Color.rgb(32, 8, 56);
            if (cell instanceof MonsterCell) return Color.rgb(26, 16, 64);
            return PANEL;
        }

        private int borderColorForCell(int index, Cell cell) {
            if (index == Constants.STARTING_POSITION) return Color.rgb(56, 189, 248);
            if (index == Constants.WINNING_POSITION) return Color.rgb(244, 63, 94);
            if (cell instanceof DoorCell) {
                DoorCell door = (DoorCell) cell;
                return door.getRole() == Role.SCARER
                        ? Color.rgb(239, 68, 68)
                        : Color.rgb(56, 189, 248);
            }
            if (cell instanceof CardCell) return Color.rgb(217, 119, 6);
            if (cell instanceof ConveyorBelt) return Color.rgb(34, 197, 94);
            if (cell instanceof ContaminationSock) return Color.rgb(168, 85, 247);
            if (cell instanceof MonsterCell) return Color.rgb(124, 58, 237);
            return Color.rgb(33, 38, 45);
        }

        private String assetForCell(int index, Cell cell) {
            if (index == Constants.STARTING_POSITION) return "assets/start.png";
            if (index == Constants.WINNING_POSITION) return "assets/end.png";
            if (cell instanceof ConveyorBelt) return index == 66 ? "assets/conveyorleft.png" : "assets/conveyor.png";
            if (cell instanceof ContaminationSock) return "assets/sock.png";
            if (cell instanceof CardCell) return "assets/card.png";
            if (cell instanceof DoorCell) {
                DoorCell door = (DoorCell) cell;
                if (door.isActivated()) return "assets/door_exhausted.png";
                return door.getRole() == Role.SCARER ? "assets/scare_door.png" : "assets/laugh_door.png";
            }
            return null;
        }

        private void drawStationedMonster(Canvas canvas, int index, float cellSize) {
            Cell cell = game.getBoard().getCell(index);
            if (!(cell instanceof MonsterCell)) {
                return;
            }

            Monster stationed = ((MonsterCell) cell).getCellMonster();
            if (stationed == null) {
                return;
            }

            drawMonsterImage(canvas, stationed, index, cellSize, 0.50f, 0.54f, 0.70f, 155);
        }

        private void drawMonster(Canvas canvas, Monster monster, float cellSize, boolean first) {
            drawMonsterImage(canvas, monster, monster.getPosition(), cellSize,
                    first ? 0.34f : 0.66f,
                    first ? 0.64f : 0.66f,
                    0.58f,
                    255);

            int[] rowCol = cellToScreenRowCol(monster.getPosition());
            float cx = rowCol[1] * cellSize + cellSize * (first ? 0.37f : 0.63f);
            float cy = rowCol[0] * cellSize + cellSize * 0.22f;
            float radius = cellSize * 0.15f;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(first ? Color.rgb(29, 78, 216) : Color.rgb(185, 28, 28));
            canvas.drawCircle(cx, cy, radius, paint);

            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(radius * 0.95f);
            canvas.drawText(first ? "P1" : "P2", cx, cy + radius * 0.34f, paint);
            paint.setTextAlign(Paint.Align.LEFT);
        }

        private void drawMonsterImage(Canvas canvas, Monster monster, int index, float cellSize,
                                      float xFraction, float yFraction, float scale, int alpha) {
            Bitmap bitmap = loadBitmap(assetForMonster(monster));
            if (bitmap == null) {
                return;
            }

            int[] rowCol = cellToScreenRowCol(index);
            float size = cellSize * scale;
            float cx = rowCol[1] * cellSize + cellSize * xFraction;
            float cy = rowCol[0] * cellSize + cellSize * yFraction;
            dstRect.set(cx - size / 2f, cy - size / 2f, cx + size / 2f, cy + size / 2f);
            srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            paint.setAlpha(alpha);
            canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
            paint.setAlpha(255);
        }

        private void drawTransportArrows(Canvas canvas, float cellSize) {
            for (int source : Constants.CONVEYOR_CELL_INDICES) {
                Cell cell = game.getBoard().getCell(source);
                if (cell instanceof ConveyorBelt) {
                    drawArrow(canvas, source, source + ((ConveyorBelt) cell).getEffect(), cellSize,
                            Color.rgb(34, 197, 94));
                }
            }

            for (int source : Constants.SOCK_CELL_INDICES) {
                Cell cell = game.getBoard().getCell(source);
                if (cell instanceof ContaminationSock) {
                    drawArrow(canvas, source, source + ((ContaminationSock) cell).getEffect(), cellSize,
                            Color.rgb(255, 68, 68));
                }
            }
        }

        private void drawArrow(Canvas canvas, int source, int destination, float cellSize, int color) {
            destination = Math.max(0, Math.min(Constants.BOARD_SIZE - 1, destination));
            int[] startCell = cellToScreenRowCol(source);
            int[] endCell = cellToScreenRowCol(destination);

            float sx = startCell[1] * cellSize + cellSize / 2f;
            float sy = startCell[0] * cellSize + cellSize / 2f;
            float ex = endCell[1] * cellSize + cellSize / 2f;
            float ey = endCell[0] * cellSize + cellSize / 2f;

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(3f, cellSize * 0.055f));
            paint.setColor(Color.argb(210, Color.red(color), Color.green(color), Color.blue(color)));
            canvas.drawLine(sx, sy, ex, ey, paint);

            float dx = ex - sx;
            float dy = ey - sy;
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            if (length < 1f) {
                return;
            }

            float ux = dx / length;
            float uy = dy / length;
            float arrowLength = cellSize * 0.24f;
            float arrowWidth = cellSize * 0.13f;
            float baseX = ex - ux * arrowLength;
            float baseY = ey - uy * arrowLength;
            float px = -uy * arrowWidth;
            float py = ux * arrowWidth;

            Path path = new Path();
            path.moveTo(ex, ey);
            path.lineTo(baseX + px, baseY + py);
            path.lineTo(baseX - px, baseY - py);
            path.close();

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawPath(path, paint);
        }

        private int[] cellToScreenRowCol(int index) {
            int row = index / Constants.BOARD_COLS;
            int col = index % Constants.BOARD_COLS;
            if (row % 2 == 1) {
                col = Constants.BOARD_COLS - 1 - col;
            }
            return new int[]{Constants.BOARD_ROWS - 1 - row, col};
        }
    }
}
