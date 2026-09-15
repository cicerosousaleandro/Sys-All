package br.com.sysall.payments.ui;

import br.com.sysall.payments.application.PaymentsApplication;
import br.com.sysall.payments.domain.model.Trabalhador;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public final class TrabalhadoresWindow {

    private static final String BACKGROUND = "#E9E1D5";
    private static final String SURFACE = "#F8F5EF";
    private static final String PRIMARY = "#173A5E";
    private static final String SECONDARY = "#6B6258";
    private static final String BORDER = "#D4C8B8";
    private static final String ACCENT = "#2E6F95";

    private TrabalhadoresWindow() {
    }

    public static void show(Stage owner) {
        var stage = new Stage();
        stage.setTitle("Sys-All - Trabalhadores");
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setMinWidth(950);
        stage.setMinHeight(620);
        stage.setScene(new Scene(createRoot(stage), 1050, 700));
        stage.show();
    }

    private static BorderPane createRoot(Stage stage) {
        var application = new PaymentsApplication();
        var selectedId = new SelectedWorker();

        var root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");
        root.setTop(createHeader());

        var table = createTable();
        var nameField = createField("Nome do trabalhador");
        var functionField = createField("Função / serviço");
        var valueField = createField("Valor padrão");

        var form = createForm(
                application,
                selectedId,
                table,
                nameField,
                functionField,
                valueField
        );

        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        selectedId.clear();
                        return;
                    }

                    selectedId.set(newValue.getId());
                    nameField.setText(newValue.getNome());
                    functionField.setText(newValue.getFuncao());
                    valueField.setText(formatarValor(newValue.getValorPadrao()));
                }
        );

        refresh(table, application);

        var center = new VBox(18, table, form);
        center.setPadding(new Insets(24, 30, 30, 30));
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);

        root.setCenter(center);
        return root;
    }

    private static VBox createHeader() {
        var title = new Label("Trabalhadores");
        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY + ";"
        );

        var subtitle = new Label(
                "Cadastro dos profissionais que recebem pagamentos avulsos"
        );
        subtitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + SECONDARY + ";"
        );

        var header = new VBox(6, title, subtitle);
        header.setPadding(new Insets(24, 30, 22, 30));
        header.setStyle(
                "-fx-background-color: " + SURFACE + ";" +
                "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                "-fx-border-width: 0 0 1 0;"
        );

        return header;
    }

    private static TableView<Trabalhador> createTable() {
        var table = new TableView<Trabalhador>();
        table.setPlaceholder(new Label("Nenhum trabalhador cadastrado."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(360);

        var nameColumn = new TableColumn<Trabalhador, String>("Nome");
        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNome())
        );

        var functionColumn = new TableColumn<Trabalhador, String>("Função / serviço");
        functionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFuncao())
        );

        var valueColumn = new TableColumn<Trabalhador, BigDecimal>("Valor padrão");
        valueColumn.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getValorPadrao())
        );
        valueColumn.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : formatarValor(value));
            }
        });

        table.getColumns().addAll(
                nameColumn,
                functionColumn,
                valueColumn
        );

        return table;
    }

    private static GridPane createForm(
            PaymentsApplication application,
            SelectedWorker selectedId,
            TableView<Trabalhador> table,
            TextField nameField,
            TextField functionField,
            TextField valueField) {

        var form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new Insets(18));
        form.setStyle(
                "-fx-background-color: " + SURFACE + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;"
        );

        var nameLabel = createFieldLabel("Nome");
        var functionLabel = createFieldLabel("Função / serviço");
        var valueLabel = createFieldLabel("Valor padrão");

        var saveButton = createButton("Salvar", ACCENT);
        var newButton = createButton("Novo", "#6B6258");
        var deleteButton = createButton("Excluir", "#9B4D3C");

        saveButton.setOnAction(event -> {
            try {
                var nome = nameField.getText().trim();
                var funcao = functionField.getText().trim();
                var valor = parseValor(valueField.getText());

                if (selectedId.get() == null) {
                    application.cadastrarTrabalhador(
                            new Trabalhador(nome, funcao, valor)
                    );
                } else {
                    var trabalhador = application
                            .buscarTrabalhador(selectedId.get())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "O trabalhador selecionado não foi encontrado."
                            ));

                    trabalhador.setNome(nome);
                    trabalhador.setFuncao(funcao);
                    trabalhador.setValorPadrao(valor);
                    application.atualizarTrabalhador(trabalhador);
                }

                clearForm(selectedId, table, nameField, functionField, valueField);
                refresh(table, application);

            } catch (Exception erro) {
                showError(erro.getMessage());
            }
        });

        newButton.setOnAction(event ->
                clearForm(selectedId, table, nameField, functionField, valueField)
        );

        deleteButton.setOnAction(event -> {
            var id = selectedId.get();

            if (id == null) {
                showError("Selecione um trabalhador para excluir.");
                return;
            }

            var confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Sys-All");
            confirmation.setHeaderText("Excluir trabalhador");
            confirmation.setContentText("Deseja realmente excluir o trabalhador selecionado?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        application.excluirTrabalhador(id);
                        clearForm(selectedId, table, nameField, functionField, valueField);
                        refresh(table, application);
                    } catch (Exception erro) {
                        showError(erro.getMessage());
                    }
                }
            });
        });

        var buttons = new HBox(10, saveButton, newButton, deleteButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        form.add(nameLabel, 0, 0);
        form.add(nameField, 1, 0);
        form.add(functionLabel, 2, 0);
        form.add(functionField, 3, 0);
        form.add(valueLabel, 4, 0);
        form.add(valueField, 5, 0);
        form.add(buttons, 6, 0);

        GridPane.setHgrow(nameField, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(functionField, javafx.scene.layout.Priority.ALWAYS);

        return form;
    }

    private static TextField createField(String prompt) {
        var field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(36);
        return field;
    }

    private static Label createFieldLabel(String text) {
        var label = new Label(text);
        label.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY + ";"
        );
        return label;
    }

    private static Button createButton(String text, String color) {
        var button = new Button(text);
        button.setPrefHeight(36);
        button.setFocusTraversable(false);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;"
        );
        return button;
    }

    private static void refresh(
            TableView<Trabalhador> table,
            PaymentsApplication application) {

        table.setItems(
                FXCollections.observableArrayList(
                        application.listarTrabalhadores()
                )
        );
    }

    private static void clearForm(
            SelectedWorker selectedId,
            TableView<Trabalhador> table,
            TextField nameField,
            TextField functionField,
            TextField valueField) {

        selectedId.clear();
        table.getSelectionModel().clearSelection();
        nameField.clear();
        functionField.clear();
        valueField.clear();
        nameField.requestFocus();
    }

    private static BigDecimal parseValor(String text) {
        var normalized = text == null ? "" : text.trim();

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o valor padrão."
            );
        }

        normalized = normalized.replace("R$", "").trim();
        normalized = normalized.replace(".", "").replace(",", ".");

        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException(
                    "Informe um valor válido. Exemplo: 450,00."
            );
        }
    }

    private static String formatarValor(BigDecimal valor) {
        return NumberFormat
                .getCurrencyInstance(new Locale("pt", "BR"))
                .format(valor);
    }

    private static void showError(String message) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sys-All");
        alert.setHeaderText("Não foi possível concluir a operação");
        alert.setContentText(
                message == null || message.isBlank()
                        ? "Ocorreu um erro inesperado."
                        : message
        );
        alert.showAndWait();
    }

    private static final class SelectedWorker {

        private UUID id;

        UUID get() {
            return id;
        }

        void set(UUID id) {
            this.id = id;
        }

        void clear() {
            this.id = null;
        }
    }
}
