package game.view;

import game.engine.cards.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CardPopup {

    public static void show(Stage ownerStage, Card drawnCard, Runnable onCloseAction) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(ownerStage);
        popupStage.initStyle(StageStyle.UTILITY); // Ensures the window frame renders reliably across OS environments
        popupStage.setTitle("🃏 Card Drawn!");

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));

        // Match the deep dark space theme layout styles of your board dashboard panel
        contentBox.setStyle("-fx-background-color: #0d1117; -fx-border-color: #d97706; -fx-border-width: 2px;");

        Label headerLabel = new Label("YOU DREW A CARD!");
        headerLabel.setStyle("-fx-text-fill: #d97706; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Display the specific card data loaded from your milestone structures
        Label nameLabel = new Label(drawnCard.getName().toUpperCase());
        nameLabel.setStyle("-fx-text-fill: #f0f6fc; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label descLabel = new Label(drawnCard.getDescription());
        descLabel.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px; -fx-alignment: center; -fx-text-alignment: center;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(360);

        Button btnAction = new Button("EXECUTE CARD EFFECT");
        btnAction.setStyle("-fx-background-color: linear-gradient(to bottom, #1d6eea, #1558c0); -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 16; -fx-background-radius: 6;");

        btnAction.setOnAction(e -> {
            // 1. Safe check to ensure we only close if it's currently open
            if (popupStage.isShowing()) {
                popupStage.close();
            }
            
            // 2. Defer the heavy game logic/callback until the loop finishes tearing down
            if (onCloseAction != null) {
                javafx.application.Platform.runLater(() -> onCloseAction.run());
            }
        });
        contentBox.getChildren().addAll(headerLabel, nameLabel, descLabel, btnAction);

        Scene scene = new Scene(contentBox, 420, 280);
        popupStage.setScene(scene);
        popupStage.setResizable(false);
        popupStage.centerOnScreen();

        // This blocks game interaction in the background until the player clicks the button
        popupStage.showAndWait(); 
    }
}