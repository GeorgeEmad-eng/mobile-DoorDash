package game.view;

import game.engine.Game;
import game.engine.Role;
//import game.engine.monsters.Monster;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SelectRoleView {

    private Stage stage;

    public SelectRoleView(Stage stage) {
        this.stage = stage;
    }

    public void show() {

        Label title = new Label("🎮 Select Your Role");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button scarerBtn = new Button("👻 SCARER");
        Button laugherBtn = new Button("😂 LAUGHER");

        scarerBtn.setPrefWidth(200);
        laugherBtn.setPrefWidth(200);

        scarerBtn.setOnAction(e -> startGame(Role.SCARER));
        laugherBtn.setOnAction(e -> startGame(Role.LAUGHER));

        VBox root = new VBox(20, title, scarerBtn, laugherBtn);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 400, 300);

        stage.setScene(scene);
        stage.setTitle("Monster Game - Select Role");
        stage.show();
    }

    private void startGame(Role role) {

        try {

            // Create game
            Game game = new Game(role);

            // Open board view
            BoardView boardView = new BoardView(stage);

            // Show board with CSS
            boardView.show(game);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}