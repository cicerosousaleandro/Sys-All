package br.com.sysall.payments.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class PaymentsWindow {

    private static final String BACKGROUND = "#E9E1D5";
    private static final String SURFACE = "#F8F5EF";
    private static final String PRIMARY = "#173A5E";
    private static final String SECONDARY = "#6B6258";
    private static final String BORDER = "#D4C8B8";

    private PaymentsWindow() {
    }

    public static void show(Stage owner) {
        var stage = new Stage();

        stage.setTitle("Sys-All - Pagamentos Avulsos");

        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
        }

        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.setScene(new Scene(createRoot(stage), 1000, 680));

        stage.centerOnScreen();
        stage.show();
    }

    private static BorderPane createRoot(Stage stage) {
        var root = new BorderPane();

        root.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );

        root.setTop(createHeader());
        root.setCenter(createActions(stage));

        return root;
    }

    private static VBox createHeader() {
        var title = new Label("Pagamentos Avulsos");

        title.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        var subtitle = new Label(
                "Cadastros, listas semanais, pagamentos e histórico"
        );

        subtitle.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + SECONDARY + ";"
        );

        var header = new VBox(3, title, subtitle);

        header.setPadding(
                new Insets(14, 26, 14, 26)
        );

        header.setStyle(
                "-fx-background-color: " + SURFACE + ";" +
                        "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        return header;
    }

    private static GridPane createActions(Stage stage) {
        var grid = new GridPane();

        grid.setHgap(16);
        grid.setVgap(16);
        grid.setPadding(new Insets(22));
        grid.setAlignment(Pos.TOP_CENTER);

        grid.add(
                createAction(
                        "👥",
                        "Trabalhadores",
                        "Cadastre e gerencie os trabalhadores avulsos.",
                        "#2E6F95",
                        () -> TrabalhadoresWindow.show(stage)
                ),
                0,
                0
        );

        grid.add(
                createAction(
                        "💰",
                        "Pagamentos",
                        "Registre e consulte pagamentos individuais.",
                        "#4D7C59",
                        () -> showDevelopment(
                                stage,
                                "Pagamentos",
                                "A tela de pagamentos será ligada ao fluxo semanal na próxima etapa."
                        )
                ),
                1,
                0
        );

        grid.add(
                createAction(
                        "📋",
                        "Listas semanais",
                        "Monte, confira e feche as listas de pagamento.",
                        "#B26A32",
                        () -> ListasPagamentoWindow.show(stage)
                ),
                0,
                1
        );

        grid.add(
                createAction(
                        "📊",
                        "Histórico",
                        "Consulte pagamentos por trabalhador e período.",
                        "#76558F",
                        () -> showDevelopment(
                                stage,
                                "Histórico",
                                "O histórico será implementado após o fluxo de listas."
                        )
                ),
                1,
                1
        );

        grid.add(
                createAction(
                        "⚙",
                        "Configurações",
                        "Configure o módulo de pagamentos.",
                        "#A27A32",
                        () -> showDevelopment(
                                stage,
                                "Configurações",
                                "As configurações do módulo serão implementadas nesta área."
                        )
                ),
                0,
                2
        );

        grid.add(
                createAction(
                        "💾",
                        "Backup",
                        "Gere e restaure backups do banco de pagamentos.",
                        "#2F7C82",
                        () -> showDevelopment(
                                stage,
                                "Backup",
                                "A rotina de backup e restauração será ligada à interface em uma etapa específica."
                        )
                ),
                1,
                2
        );

        return grid;
    }

    private static VBox createAction(
            String icon,
            String title,
            String description,
            String color,
            Runnable action) {

        var iconLabel = new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 30px;"
        );

        var titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + color + ";"
        );

        var descriptionLabel = new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + SECONDARY + ";"
        );

        var button = new Button("Abrir");

        button.setPrefWidth(110);
        button.setPrefHeight(34);
        button.setFocusTraversable(false);

        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18 8 18;"
        );

        button.setOnAction(event -> action.run());

        var card = new VBox(
                8,
                iconLabel,
                titleLabel,
                descriptionLabel,
                button
        );

        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(18));
        card.setPrefSize(390, 150);

        card.setStyle(
                "-fx-background-color: " + SURFACE + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;"
        );

        return card;
    }

    private static void showDevelopment(
            Stage owner,
            String title,
            String message) {

        var alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.initOwner(owner);
        alert.setTitle(
                "Sys-All - Pagamentos Avulsos"
        );
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}