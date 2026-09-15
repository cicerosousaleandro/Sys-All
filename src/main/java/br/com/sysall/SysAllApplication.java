package br.com.sysall;

import br.com.sysall.payments.ui.PaymentsWindow;
import br.com.sysall.shared.AppMetadata;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public final class SysAllApplication extends javafx.application.Application {

    private static final String BACKGROUND = "#E9E1D5";
    private static final String SURFACE = "#F8F5EF";
    private static final String PRIMARY = "#173A5E";
    private static final String SECONDARY = "#6B6258";
    private static final String BORDER = "#D4C8B8";

    private static final String CADASTROS_COLOR = "#2E6F95";
    private static final String USUARIOS_COLOR = "#4D7C59";
    private static final String FERRAMENTAS_COLOR = "#B26A32";
    private static final String MONITORAMENTO_COLOR = "#2F7C82";
    private static final String RELATORIOS_COLOR = "#76558F";
    private static final String CONFIGURACOES_COLOR = "#A27A32";

    @Override
    public void start(Stage stage) {
        stage.setTitle(AppMetadata.name());
        stage.setMinWidth(1000);
        stage.setMinHeight(620);
        stage.setScene(new Scene(createHome(), 1200, 760));
        stage.setMaximized(true);
        stage.show();
    }

    private BorderPane createHome() {
        var root = new BorderPane();

        root.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );

        root.setTop(createToolbar());
        root.setCenter(createWorkspace());

        return root;
    }

    private HBox createToolbar() {
        var toolbar = new HBox(8);

        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(
                new Insets(4, 16, 4, 16)
        );

        toolbar.setStyle(
                "-fx-background-color: " + SURFACE + ";" +
                        "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        toolbar.getChildren().addAll(
                createIconButton(
                        createClipboardIcon(),
                        "Cadastros",
                        CADASTROS_COLOR
                ),
                createIconButton(
                        createUsersIcon(),
                        "Usuários",
                        USUARIOS_COLOR
                ),
                createToolsButton(),
                createIconButton(
                        createMonitorIcon(),
                        "Monitoramento",
                        MONITORAMENTO_COLOR
                ),
                createIconButton(
                        createReportIcon(),
                        "Relatórios",
                        RELATORIOS_COLOR
                ),
                createIconButton(
                        createSettingsIcon(),
                        "Configurações",
                        CONFIGURACOES_COLOR
                )
        );

        return toolbar;
    }

    private Button createIconButton(
            SVGPath icon,
            String tooltipText,
            String iconColor) {

        var button = new Button();

        icon.setFill(Color.web(iconColor));

        button.setGraphic(icon);
        button.setMinSize(52, 52);
        button.setPrefSize(52, 52);
        button.setMaxSize(52, 52);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip(tooltipText));

        applyIconStyle(button, false);

        button.setOnMouseEntered(event ->
                applyIconStyle(button, true)
        );

        button.setOnMouseExited(event ->
                applyIconStyle(button, false)
        );

        return button;
    }

    private Button createToolsButton() {
        var button = createIconButton(
                createWrenchIcon(),
                "Ferramentas",
                FERRAMENTAS_COLOR
        );

        var menu = new ContextMenu();

        var paymentsItem =
                new MenuItem("Pagamentos Avulsos");

        paymentsItem.setOnAction(event -> {
            var window = button.getScene().getWindow();

            PaymentsWindow.show(
                    window instanceof Stage stage
                            ? stage
                            : null
            );
        });

        menu.getItems().add(paymentsItem);

        button.setOnAction(event ->
                menu.show(
                        button,
                        javafx.geometry.Side.BOTTOM,
                        0,
                        0
                )
        );

        return button;
    }

    private void applyIconStyle(
            Button button,
            boolean hovered) {

        button.setStyle(
                "-fx-background-color: " +
                        (hovered ? "#DED2C2" : "transparent") + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-padding: 8;"
        );
    }

    private StackPane createWorkspace() {
        var workspace = new StackPane();

        workspace.setPadding(
                new Insets(28)
        );

        var logoPanel = createLogoPanel();

        StackPane.setAlignment(
                logoPanel,
                Pos.TOP_RIGHT
        );

        workspace.getChildren().add(
                logoPanel
        );

        return workspace;
    }

    private VBox createLogoPanel() {
        var name = new Label(
                AppMetadata.name()
        );

        name.setStyle(
                "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        var separator = new Separator();

        separator.setPrefWidth(210);

        var release = new Label(
                "v" + AppMetadata.version()
                        .replace("-SNAPSHOT", "")
        );

        release.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + SECONDARY + ";"
        );

        var panel = new VBox(
                18,
                name,
                separator,
                release
        );

        panel.setAlignment(Pos.CENTER);
        panel.setPadding(
                new Insets(34)
        );

        panel.setPrefSize(
                300,
                240
        );

        panel.setMaxSize(
                300,
                240
        );

        panel.setStyle(
                "-fx-background-color: " + SURFACE + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;"
        );

        return panel;
    }

    private SVGPath createClipboardIcon() {
        return createIcon(
                "M16 2H8C6.9 2 6 2.9 6 4H5C3.9 4 3 4.9 3 6V20C3 21.1 3.9 22 5 22H19C20.1 22 21 21.1 21 20V6C21 4.9 20.1 4 19 4H18C18 2.9 17.1 2 16 2ZM8 4H16V6H8V4ZM19 20H5V6H7V8H17V6H19V20ZM8 11H16V13H8V11ZM8 15H16V17H8V15Z"
        );
    }

    private SVGPath createUsersIcon() {
        return createIcon(
                "M16 11C18.21 11 20 9.21 20 7C20 4.79 18.21 3 16 3C15.15 3 14.37 3.27 13.75 3.73C14.54 4.62 15 5.77 15 7C15 8.23 14.54 9.38 13.75 10.27C14.37 10.73 15.15 11 16 11ZM8 12C10.76 12 13 9.76 13 7C13 4.24 10.76 2 8 2C5.24 2 3 4.24 3 7C3 9.76 5.24 12 8 12ZM16 13C14.67 13 13.42 13.3 12.35 13.82C13.97 14.91 15 16.43 15 18V21H22V18C22 15.24 19.31 13 16 13ZM8 14C3.58 14 0 16.24 0 19V22H16V19C16 16.24 12.42 14 8 14Z"
        );
    }

    private SVGPath createWrenchIcon() {
        return createIcon(
                "M22 19.6L14.4 12C15.1 10.9 15.4 9.6 15.2 8.3C14.9 6.1 13.1 4.3 10.9 4C9.6 3.8 8.3 4.1 7.2 4.8L10.8 8.4L8.4 10.8L4.8 7.2C4.1 8.3 3.8 9.6 4 10.9C4.3 13.1 6.1 14.9 8.3 15.2C9.6 15.4 10.9 15.1 12 14.4L19.6 22L22 19.6ZM10.6 13.2C9.3 13.5 7.9 12.9 7.1 11.8C6.5 10.9 6.4 9.8 6.7 8.8L9 11.1L12.1 8L9.8 5.7C10.8 5.4 11.9 5.5 12.8 6.1C13.9 6.9 14.5 8.3 14.2 9.6L13.9 10.8L21 17.9L19.9 19L12.8 11.9L10.6 13.2Z"
        );
    }

    private SVGPath createMonitorIcon() {
        return createIcon(
                "M3 4C3 2.9 3.9 2 5 2H19C20.1 2 21 2.9 21 4V15C21 16.1 20.1 17 19 17H14V19H17V21H7V19H10V17H5C3.9 17 3 16.1 3 15V4ZM5 4V14H19V4H5Z"
        );
    }

    private SVGPath createReportIcon() {
        return createIcon(
                "M5 19V21H19V19H5ZM5 15H8V17H5V15ZM10 11H13V17H10V11ZM15 7H18V17H15V7ZM4 3H20C21.1 3 22 3.9 22 5V18C22 19.1 21.1 20 20 20H4C2.9 20 2 19.1 2 18V5C2 3.9 2.9 3 4 3ZM4 5V18H20V5H4Z"
        );
    }

    private SVGPath createSettingsIcon() {
        return createIcon(
                "M19.43 12.98C19.47 12.66 19.5 12.34 19.43 11.02L21.54 9.37L19.54 5.91L17.05 6.91C16.53 6.51 15.95 6.17 15.32 5.92L14.95 3.25H10.95L10.58 5.92C9.95 6.17 9.37 6.51 8.85 6.91L6.36 5.91L4.36 9.37L6.47 11.02C6.43 11.34 6.4 11.66 6.47 11.02L4.36 14.63L6.36 18.09L8.85 17.09C9.37 17.49 9.95 17.83 10.58 18.08L10.95 20.75H14.95L15.32 18.08C15.95 17.83 16.53 17.49 17.05 17.09L19.54 18.09L21.54 14.63L19.43 12.98ZM12.95 15.5C11.02 15.5 9.45 13.93 9.45 12C9.45 10.07 11.02 8.5 12.95 8.5C14.88 8.5 16.45 10.07 16.45 12C16.45 13.93 14.88 15.5 12.95 15.5Z"
        );
    }

    private SVGPath createIcon(String content) {
        var icon = new SVGPath();

        icon.setContent(content);
        icon.setScaleX(1.35);
        icon.setScaleY(1.35);

        return icon;
    }
}