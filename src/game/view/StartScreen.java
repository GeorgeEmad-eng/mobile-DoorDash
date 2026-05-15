package game.view;

import game.engine.Game;
import game.engine.Role;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class StartScreen extends Application {

    private StackPane root;
    private VBox mainLayout;
    private VBox rolePopup;
    private VBox instructionsPopup;
    private StackPane overlay;
    private MediaPlayer mediaPlayer;

    // ✅ FIX: store stage globally
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {

        // ✅ FIX: assign stage
        this.primaryStage = stage;

        root = new StackPane();

        // Theme background (UNCHANGED)
        root.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 100%, #1a331a, #0f0225);");

        // Animated background (UNCHANGED)
        Pane animationPane = new Pane();
        createScatteredMonsterAnimation(animationPane);

        // Overlay (UNCHANGED)
        overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.6);");
        overlay.setVisible(false);

        setupMenus();
        setupVolumeControl();

        root.getChildren().addAll(animationPane, mainLayout, overlay, rolePopup, instructionsPopup);

        Scene scene = new Scene(root, 1500, 980);
        scene.getStylesheets().add(getClass().getResource("/game/view/css/style.css").toExternalForm());

        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setTitle("DooR DasH: Scare vs Laugh Touchdown");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupMenus() {

        mainLayout = new VBox(30);
        mainLayout.setAlignment(Pos.CENTER);

        Text title = new Text("MONSTERS, INC.");
        title.getStyleClass().add("mi-title");

        Button btnStart = new Button("START GAME");
        btnStart.getStyleClass().add("mi-button");
        btnStart.setOnAction(e -> showPopup(rolePopup));

        Button btnHowTo = new Button("HOW TO PLAY");
        btnHowTo.getStyleClass().add("mi-button");
        btnHowTo.setOnAction(e -> showPopup(instructionsPopup));

        Button btnExit = new Button("EXIT");
        btnExit.getStyleClass().addAll("mi-button", "btn-exit");
        btnExit.setOnAction(e -> System.exit(0));

        mainLayout.getChildren().addAll(title, btnStart, btnHowTo, btnExit);

        // ───────── ROLE POPUP ─────────
        rolePopup = createBasePopup("SELECT YOUR SIDE");

        Button btnScarer = new Button("SCARER");
        btnScarer.getStyleClass().add("mi-button");

        Button btnLaugher = new Button("LAUGHER");
        btnLaugher.getStyleClass().add("mi-button");
        btnLaugher.setStyle("-fx-background-color: #00ffe7;");

        Button btnBackRole = new Button("BACK");
        btnBackRole.getStyleClass().add("secondary-button");
        btnBackRole.setOnAction(e -> hidePopups());

        // ✅ FIXED: now works
        btnScarer.setOnAction(e -> startGame(Role.SCARER));
        btnLaugher.setOnAction(e -> startGame(Role.LAUGHER));

        rolePopup.getChildren().addAll(btnScarer, btnLaugher, btnBackRole);

        // ───────── INSTRUCTIONS POPUP ─────────
        instructionsPopup = createBasePopup("HOW TO PLAY");

        Text instrText = new Text(
                "1. Select a side: Scarer or Laugher.\n" +
                "2. Roll the dice to move across the floor.\n" +
                "3. Land on Door Cells to collect energy.\n" +
                "4. Avoid the CDA and traps!\n" +
                "5. Reach position 99 first to win!"
        );

        instrText.getStyleClass().add("mi-instr-text");

        Button btnBackInstr = new Button("BACK");
        btnBackInstr.getStyleClass().add("secondary-button");
        btnBackInstr.setOnAction(e -> hidePopups());

        instructionsPopup.getChildren().addAll(instrText, btnBackInstr);
    }

    // ✅ FIXED GAME START METHOD
    private void startGame(Role role) {

        try {

            Game game = new Game(role);

            BoardView boardView = new BoardView(primaryStage);

            boardView.show(game);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private VBox createBasePopup(String titleStr) {

        VBox popup = new VBox(20);
        popup.setAlignment(Pos.CENTER);
        popup.getStyleClass().add("mi-popup");
        popup.setMaxSize(500, 450);
        popup.setVisible(false);
        popup.setScaleX(0);
        popup.setScaleY(0);

        Text title = new Text(titleStr);
        title.getStyleClass().add("mi-prompt");

        popup.getChildren().add(title);

        return popup;
    }

    private void showPopup(VBox popup) {

        overlay.setVisible(true);
        popup.setVisible(true);

        ScaleTransition st = new ScaleTransition(Duration.millis(300), popup);
        st.setToX(1);
        st.setToY(1);
        st.play();
    }

    private void hidePopups() {

        overlay.setVisible(false);

        rolePopup.setVisible(false);
        rolePopup.setScaleX(0);
        rolePopup.setScaleY(0);

        instructionsPopup.setVisible(false);
        instructionsPopup.setScaleX(0);
        instructionsPopup.setScaleY(0);
    }

    private void setupVolumeControl() {

        StackPane gearContainer = new StackPane();

        Text gearIcon = new Text("⚙");
        gearIcon.getStyleClass().add("gear-icon");

        gearContainer.getChildren().add(gearIcon);
        gearContainer.setPadding(new Insets(20));

        Slider volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setMaxWidth(150);
        volumeSlider.setVisible(false);
        volumeSlider.getStyleClass().add("volume-slider");

        gearContainer.setOnMouseClicked(e ->
                volumeSlider.setVisible(!volumeSlider.isVisible())
        );

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null)
                mediaPlayer.setVolume(newVal.doubleValue());
        });

        VBox volumeBox = new VBox(10, gearContainer, volumeSlider);
        volumeBox.setAlignment(Pos.TOP_RIGHT);
        volumeBox.setPickOnBounds(false);

        StackPane.setAlignment(volumeBox, Pos.TOP_RIGHT);
        root.getChildren().add(volumeBox);
    }

    private void createScatteredMonsterAnimation(Pane pane) {

        for (int i = 0; i < 15; i++) {

            Group monster = new Group();

            Circle face = new Circle(25,
                    Color.web(i % 2 == 0 ? "#5b8fd4" : "#6abf69", 0.2));

            Circle eye = new Circle(8, Color.WHITE);
            eye.setOpacity(0.4);

            Circle pupil = new Circle(3, Color.BLACK);
            pupil.setOpacity(0.4);

            monster.getChildren().addAll(face, eye, pupil);

            monster.setLayoutX(Math.random() * 1500 - 200);
            monster.setLayoutY(Math.random() * 1200 - 200);

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getBounds();

            double w = bounds.getWidth();
            double l = bounds.getHeight();

            TranslateTransition tt =
                    new TranslateTransition(
                            Duration.seconds(15 + Math.random() * 10),
                            monster
                    );

            tt.setByX(Math.random() * w - 200);
            tt.setByY(Math.random() * l - 200);

            tt.setCycleCount(Animation.INDEFINITE);
            tt.setAutoReverse(true);
            tt.play();

            pane.getChildren().add(monster);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}