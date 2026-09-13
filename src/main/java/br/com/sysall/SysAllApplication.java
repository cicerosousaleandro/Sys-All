package br.com.sysall;

import br.com.sysall.shared.AppMetadata;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Ponto de entrada e tela inicial do Sys-All. */
public final class SysAllApplication extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle(AppMetadata.name());
        stage.setMinWidth(900);
        stage.setMinHeight(560);
        stage.setScene(new Scene(createHome(), 1024, 650));
        stage.show();
    }

    private BorderPane createHome() {
        var root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F7FA;");
        root.setTop(createHeader());
        root.setCenter(createContent());
        root.setBottom(createFooter());
        return root;
    }

    private VBox createHeader() {
        var name = new Label(AppMetadata.name());
        name.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: #163A5F;");
        var subtitle = new Label("Gestão simples, rápida e organizada");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #5E6D79;");
        var header = new VBox(3, name, subtitle);
        header.setPadding(new Insets(24, 38, 20, 38));
        header.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #DCE4EA transparent;");
        return header;
    }

    private VBox createContent() {
        var title = new Label("Módulos do sistema");
        title.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: #263746;");

        var paymentButton = new Button("Pagamentos Avulsos");
        paymentButton.setMaxWidth(Double.MAX_VALUE);
        paymentButton.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: white; "
                + "-fx-background-color: #1F6F8B; -fx-background-radius: 8; -fx-padding: 16 24;");
        paymentButton.setOnAction(event -> showDevelopmentMessage());

        var paymentCard = createCard(
                "Pagamentos Avulsos",
                "Cadastre trabalhadores, monte a lista semanal, imprima os recibos e consulte pagamentos por período.",
                paymentButton,
                false);

        var warehouseCard = createCard(
                "Almoxarifado",
                "Controle de materiais, entradas, saídas e relatórios. Módulo planejado para uma próxima entrega.",
                new Label("Em planejamento"),
                true);

        var cards = new HBox(20, paymentCard, warehouseCard);
        HBox.setHgrow(paymentCard, Priority.ALWAYS);
        HBox.setHgrow(warehouseCard, Priority.ALWAYS);

        var content = new VBox(20, title, cards);
        content.setPadding(new Insets(34, 38, 34, 38));
        return content;
    }

    private VBox createCard(String titleText, String descriptionText, javafx.scene.Node action, boolean muted) {
        var title = new Label(titleText);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + (muted ? "#6E7D88" : "#163A5F") + ";");
        var description = new Label(descriptionText);
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 14px; -fx-text-fill: #556571;");
        var card = new VBox(14, title, new Separator(), description, action);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(24));
        card.setMinHeight(230);
        card.setPrefWidth(400);
        card.setStyle("-fx-background-color: " + (muted ? "#EDF1F4" : "white")
                + "; -fx-background-radius: 10; -fx-border-color: #D9E2E8; -fx-border-radius: 10;");
        return card;
    }

    private HBox createFooter() {
        var version = new Label("Versão " + AppMetadata.version() + " - " + AppMetadata.releaseName());
        version.setStyle("-fx-font-size: 12px; -fx-text-fill: #6A7883;");
        var footer = new HBox(version);
        footer.setPadding(new Insets(13, 38, 13, 38));
        footer.setStyle("-fx-background-color: white; -fx-border-color: #DCE4EA transparent transparent transparent;");
        return footer;
    }

    private void showDevelopmentMessage() {
        var message = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        message.setTitle("Sys-All");
        message.setHeaderText("Pagamentos Avulsos");
        message.setContentText("Este módulo está em desenvolvimento para a primeira entrega v1.0.0.");
        message.showAndWait();
    }
}
