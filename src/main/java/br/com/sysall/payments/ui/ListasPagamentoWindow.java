package br.com.sysall.payments.ui;

import br.com.sysall.payments.application.PaymentsApplication;
import br.com.sysall.payments.domain.enums.StatusListaPagamento;
import br.com.sysall.payments.domain.model.ListaPagamento;
import br.com.sysall.payments.domain.model.Pagamento;
import br.com.sysall.payments.domain.model.Trabalhador;
import br.com.sysall.payments.infrastructure.printing.PaymentsPrintService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ListasPagamentoWindow {

    private static final String BACKGROUND = "#E9E1D5";
    private static final String SURFACE = "#F8F5EF";
    private static final String PRIMARY = "#173A5E";
    private static final String SECONDARY = "#6B6258";
    private static final String BORDER = "#D4C8B8";
    private static final String ORANGE = "#B26A32";
    private static final String GREEN = "#4D7C59";

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ListasPagamentoWindow() {
    }

    public static void show(Stage owner) {

        Stage stage = new Stage();

        stage.setTitle(
                "Sys-All - Listas Semanais"
        );

        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(
                    Modality.WINDOW_MODAL
            );
        }

        stage.setMinWidth(1000);
        stage.setMinHeight(650);

        BorderPane root =
                criarRoot(stage);

        stage.setScene(
                new Scene(
                        root,
                        1100,
                        720
                )
        );

        stage.centerOnScreen();
        stage.show();
    }

    private static BorderPane criarRoot(
            Stage stage) {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: "
                        + BACKGROUND
                        + ";"
        );

        Label titulo =
                new Label(
                        "Listas Semanais"
                );

        titulo.setStyle(
                "-fx-font-size: 24px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + PRIMARY
                        + ";"
        );

        Label subtitulo =
                new Label(
                        "Monte, confira e feche as listas de pagamentos avulsos."
                );

        subtitulo.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-text-fill: "
                        + SECONDARY
                        + ";"
        );

        VBox header =
                new VBox(
                        3,
                        titulo,
                        subtitulo
                );

        header.setPadding(
                new Insets(
                        14,
                        26,
                        14,
                        26
                )
        );

        header.setStyle(
                "-fx-background-color: "
                        + SURFACE
                        + ";"
                        + "-fx-border-color: transparent transparent "
                        + BORDER
                        + " transparent;"
                        + "-fx-border-width: 0 0 1 0;"
        );

        root.setTop(header);

        PaymentsApplication application =
                new PaymentsApplication();

        TableView<ListaPagamento> tabela =
                criarTabela(
                        application
                );

        HBox botoes =
                criarBotoes(
                        stage,
                        application,
                        tabela
                );

        VBox conteudo =
                new VBox(
                        16,
                        tabela,
                        botoes
                );

        conteudo.setPadding(
                new Insets(22)
        );

        VBox.setVgrow(
                tabela,
                javafx.scene.layout.Priority.ALWAYS
        );

        root.setCenter(conteudo);

        carregarListas(
                tabela,
                application
        );

        return root;
    }

    private static TableView<ListaPagamento> criarTabela(
            PaymentsApplication application) {

        TableView<ListaPagamento> tabela =
                new TableView<>();

        tabela.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        TableColumn<ListaPagamento, String> periodo =
                new TableColumn<>(
                        "Período"
                );

        periodo.setCellValueFactory(
                item ->
                        new SimpleStringProperty(
                                formatarPeriodo(
                                        item.getValue()
                                )
                        )
        );

        TableColumn<ListaPagamento, String> status =
                new TableColumn<>(
                        "Status"
                );

        status.setCellValueFactory(
                item ->
                        new SimpleStringProperty(
                                formatarStatus(
                                        item.getValue()
                                                .getStatus()
                                )
                        )
        );

        TableColumn<ListaPagamento, String> quantidade =
                new TableColumn<>(
                        "Trabalhadores"
                );

        quantidade.setCellValueFactory(
                item ->
                        new SimpleStringProperty(
                                String.valueOf(
                                        item.getValue()
                                                .getPagamentos()
                                                .size()
                                )
                        )
        );

        TableColumn<ListaPagamento, String> total =
                new TableColumn<>(
                        "Total"
                );

        total.setCellValueFactory(
                item ->
                        new SimpleStringProperty(
                                formatarMoeda(
                                        calcularTotal(
                                                item.getValue()
                                        )
                                )
                        )
        );

        tabela.getColumns().addAll(
                periodo,
                status,
                quantidade,
                total
        );

        tabela.setPlaceholder(
                new Label(
                        "Nenhuma lista de pagamento cadastrada."
                )
        );

        return tabela;
    }

    private static HBox criarBotoes(
            Stage stage,
            PaymentsApplication application,
            TableView<ListaPagamento> tabela) {

        Button nova =
                criarBotao(
                        "Nova Lista",
                        ORANGE
                );

        Button abrir =
                criarBotao(
                        "Abrir Lista",
                        PRIMARY
                );

        Button conferir =
                criarBotao(
                        "Conferir",
                        GREEN
                );

        Button fechar =
                criarBotao(
                        "Fechar Lista",
                        "#76558F"
                );

        nova.setOnAction(
                event ->
                        criarNovaLista(
                                stage,
                                application,
                                tabela
                        )
        );

        abrir.setOnAction(
                event -> {

                    ListaPagamento selecionada =
                            tabela.getSelectionModel()
                                    .getSelectedItem();

                    if (selecionada == null) {
                        mostrarAviso(
                                stage,
                                "Selecione uma lista para abrir."
                        );
                        return;
                    }

                    abrirLista(
                            stage,
                            application,
                            selecionada.getId(),
                            tabela
                    );
                }
        );

        conferir.setOnAction(
                event -> {

                    ListaPagamento selecionada =
                            tabela.getSelectionModel()
                                    .getSelectedItem();

                    if (selecionada == null) {
                        mostrarAviso(
                                stage,
                                "Selecione uma lista."
                        );
                        return;
                    }

                    try {

                        application.conferirLista(
                                selecionada.getId()
                        );

                        carregarListas(
                                tabela,
                                application
                        );

                        mostrarInfo(
                                stage,
                                "Lista conferida com sucesso."
                        );

                    } catch (Exception erro) {

                        mostrarErro(
                                stage,
                                erro.getMessage()
                        );
                    }
                }
        );

        fechar.setOnAction(
                event -> {

                    ListaPagamento selecionada =
                            tabela.getSelectionModel()
                                    .getSelectedItem();

                    if (selecionada == null) {
                        mostrarAviso(
                                stage,
                                "Selecione uma lista."
                        );
                        return;
                    }

                    try {

                        application.fecharLista(
                                selecionada.getId()
                        );

                        carregarListas(
                                tabela,
                                application
                        );

                        mostrarInfo(
                                stage,
                                "Lista fechada com sucesso."
                        );

                    } catch (Exception erro) {

                        mostrarErro(
                                stage,
                                erro.getMessage()
                        );
                    }
                }
        );

        HBox botoes =
                new HBox(
                        10,
                        nova,
                        abrir,
                        conferir,
                        fechar
                );

        botoes.setAlignment(
                Pos.CENTER_RIGHT
        );

        return botoes;
    }

    private static void criarNovaLista(
            Stage owner,
            PaymentsApplication application,
            TableView<ListaPagamento> tabela) {

        Stage stage =
                new Stage();

        stage.setTitle(
                "Nova Lista de Pagamento"
        );

        stage.initOwner(owner);
        stage.initModality(
                Modality.APPLICATION_MODAL
        );

        DatePicker inicio =
                new DatePicker();

        DatePicker fim =
                new DatePicker();

        inicio.setValue(
                LocalDate.now()
        );

        fim.setValue(
                LocalDate.now()
        );

        GridPane campos =
                new GridPane();

        campos.setHgap(10);
        campos.setVgap(10);

        campos.add(
                new Label("Data inicial:"),
                0,
                0
        );

        campos.add(
                inicio,
                1,
                0
        );

        campos.add(
                new Label("Data final:"),
                0,
                1
        );

        campos.add(
                fim,
                1,
                1
        );

        Button salvar =
                criarBotao(
                        "Criar Lista",
                        ORANGE
                );

        Button cancelar =
                new Button(
                        "Cancelar"
                );

        salvar.setOnAction(
                event -> {

                    try {

                        application.criarLista(
                                inicio.getValue(),
                                fim.getValue()
                        );

                        carregarListas(
                                tabela,
                                application
                        );

                        stage.close();

                    } catch (Exception erro) {

                        mostrarErro(
                                stage,
                                erro.getMessage()
                        );
                    }
                }
        );

        cancelar.setOnAction(
                event ->
                        stage.close()
        );

        HBox botoes =
                new HBox(
                        10,
                        cancelar,
                        salvar
                );

        botoes.setAlignment(
                Pos.CENTER_RIGHT
        );

        VBox root =
                new VBox(
                        18,
                        campos,
                        botoes
                );

        root.setPadding(
                new Insets(22)
        );

        stage.setScene(
                new Scene(
                        root,
                        430,
                        210
                )
        );

        stage.showAndWait();
    }

    private static void abrirLista(
            Stage owner,
            PaymentsApplication application,
            UUID listaId,
            TableView<ListaPagamento> tabelaPrincipal) {

        ListaPagamento lista =
                application.buscarLista(
                        listaId
                ).orElseThrow();

        List<Trabalhador> trabalhadores =
                application.listarTrabalhadores();

        Map<UUID, Trabalhador> mapa =
                new HashMap<>();

        for (Trabalhador trabalhador :
                trabalhadores) {

            mapa.put(
                    trabalhador.getId(),
                    trabalhador
            );
        }

        final ListaPagamento[] listaAtual =
                {lista};

        Stage stage =
                new Stage();

        stage.setTitle(
                "Sys-All - Lista de Pagamento"
        );

        stage.initOwner(owner);
        stage.initModality(
                Modality.WINDOW_MODAL
        );

        TableView<Pagamento> tabela =
                new TableView<>();

        tabela.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        TableColumn<Pagamento, String> trabalhador =
                new TableColumn<>(
                        "Trabalhador"
                );

        trabalhador.setCellValueFactory(
                item ->
                        new SimpleStringProperty(
                                nomeTrabalhador(
                                        item.getValue(),
                                        mapa
                                )
                        )
        );

        TableColumn<Pagamento, String> funcao =
                new TableColumn<>(
                        "Função"
                );

        funcao.setCellValueFactory(
                item ->
                        new SimpleStringProperty(
                                funcaoTrabalhador(
                                        item.getValue(),
                                        mapa
                                )
                        )
        );

        TableColumn<Pagamento, String> data =
                new TableColumn<>(
                        "Data"
                );

        data.setCellValueFactory(
                item ->
                        new SimpleStringProperty(
                                DATE.format(
                                        item.getValue()
                                                .getData()
                                )
                        )
        );

        TableColumn<Pagamento, String> valor =
                new TableColumn<>(
                        "Valor"
                );

        valor.setCellValueFactory(
                item ->
                        new SimpleStringProperty(
                                formatarMoeda(
                                        item.getValue()
                                                .getValor()
                                )
                        )
        );

        tabela.getColumns().addAll(
                trabalhador,
                funcao,
                data,
                valor
        );

        tabela.setItems(
                FXCollections.observableArrayList(
                        listaAtual[0].getPagamentos()
                )
        );

        Label titulo =
                new Label(
                        "Lista de Pagamento"
                );

        titulo.setStyle(
                "-fx-font-size: 22px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + PRIMARY
                        + ";"
        );

        Label periodo =
                new Label(
                        "Período: "
                                + formatarPeriodo(
                                listaAtual[0]
                        )
                );

        periodo.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-text-fill: "
                        + SECONDARY
                        + ";"
        );

        Label total =
                new Label();

        atualizarTotal(
                total,
                listaAtual[0]
        );

        Button adicionar =
                criarBotao(
                        "Adicionar Pagamento",
                        ORANGE
                );

        Button remover =
                criarBotao(
                        "Remover",
                        "#9A4A42"
                );

        Button imprimirRelatorio =
                criarBotao(
                        "Imprimir Relatório",
                        PRIMARY
                );

        Button imprimirFichas =
                criarBotao(
                        "Imprimir Fichas",
                        GREEN
                );

        boolean emEdicao =
                listaAtual[0].getStatus()
                        == StatusListaPagamento.EM_EDICAO;

        adicionar.setDisable(
                !emEdicao
        );

        remover.setDisable(
                !emEdicao
        );

        adicionar.setOnAction(
                event ->
                        adicionarPagamento(
                                owner,
                                application,
                                listaAtual[0],
                                mapa,
                                tabela,
                                total
                        )
        );

        remover.setOnAction(
                event -> {

                    Pagamento pagamento =
                            tabela.getSelectionModel()
                                    .getSelectedItem();

                    if (pagamento == null) {
                        mostrarAviso(
                                stage,
                                "Selecione um pagamento."
                        );
                        return;
                    }

                    try {

                        application.removerPagamento(
                                listaAtual[0].getId(),
                                pagamento.getId()
                        );

                        listaAtual[0] =
                                application.buscarLista(
                                        listaAtual[0].getId()
                                ).orElseThrow();

                        tabela.setItems(
                                FXCollections.observableArrayList(
                                        listaAtual[0].getPagamentos()
                                )
                        );

                        atualizarTotal(
                                total,
                                listaAtual[0]
                        );

                    } catch (Exception erro) {

                        mostrarErro(
                                stage,
                                erro.getMessage()
                        );
                    }
                }
        );

        imprimirRelatorio.setOnAction(
                event -> {

                    try {

                        ListaPagamento atualizada =
                                application.buscarLista(
                                        listaAtual[0].getId()
                                ).orElseThrow();

                        PaymentsPrintService
                                .imprimirRelatorioSemanal(
                                        atualizada,
                                        mapa
                                );

                    } catch (Exception erro) {

                        mostrarErro(
                                stage,
                                erro.getMessage()
                        );
                    }
                }
        );

        imprimirFichas.setOnAction(
                event -> {

                    try {

                        ListaPagamento atualizada =
                                application.buscarLista(
                                        listaAtual[0].getId()
                                ).orElseThrow();

                        PaymentsPrintService
                                .imprimirFichasAssinatura(
                                        atualizada,
                                        mapa
                                );

                    } catch (Exception erro) {

                        mostrarErro(
                                stage,
                                erro.getMessage()
                        );
                    }
                }
        );

        HBox botoes =
                new HBox(
                        10,
                        adicionar,
                        remover,
                        imprimirRelatorio,
                        imprimirFichas
                );

        botoes.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox root =
                new VBox(
                        14,
                        titulo,
                        periodo,
                        tabela,
                        total,
                        botoes
                );

        root.setPadding(
                new Insets(22)
        );

        VBox.setVgrow(
                tabela,
                javafx.scene.layout.Priority.ALWAYS
        );

        stage.setScene(
                new Scene(
                        root,
                        1000,
                        650
                )
        );

        stage.centerOnScreen();
        stage.show();
    }

    private static void adicionarPagamento(
            Stage owner,
            PaymentsApplication application,
            ListaPagamento lista,
            Map<UUID, Trabalhador> mapa,
            TableView<Pagamento> tabela,
            Label total) {

        Stage stage =
                new Stage();

        stage.setTitle(
                "Adicionar Pagamento"
        );

        stage.initOwner(owner);
        stage.initModality(
                Modality.APPLICATION_MODAL
        );

        ComboBox<Trabalhador> trabalhador =
                new ComboBox<>();

        trabalhador.getItems().addAll(
                mapa.values()
                        .stream()
                        .sorted(
                                (a, b) ->
                                        a.getNome()
                                                .compareToIgnoreCase(
                                                        b.getNome()
                                                )
                        )
                        .toList()
        );

        trabalhador.setCellFactory(
                combo ->
                        new javafx.scene.control.ListCell<>() {

                            @Override
                            protected void updateItem(
                                    Trabalhador item,
                                    boolean empty) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                setText(
                                        empty || item == null
                                                ? null
                                                : item.getNome()
                                );
                            }
                        }
        );

        trabalhador.setButtonCell(
                new javafx.scene.control.ListCell<>() {

                    @Override
                    protected void updateItem(
                            Trabalhador item,
                            boolean empty) {

                        super.updateItem(
                                item,
                                empty
                        );

                        setText(
                                empty || item == null
                                        ? null
                                        : item.getNome()
                        );
                    }
                }
        );

        DatePicker data =
                new DatePicker(
                        lista.getDataInicio()
                );

        TextField valor =
                new TextField();

        valor.setPromptText(
                "Ex.: 450,00"
        );

        TextField descricao =
                new TextField();

        descricao.setPromptText(
                "Serviço realizado"
        );

        GridPane campos =
                new GridPane();

        campos.setHgap(10);
        campos.setVgap(10);

        campos.add(
                new Label("Trabalhador:"),
                0,
                0
        );

        campos.add(
                trabalhador,
                1,
                0
        );

        campos.add(
                new Label("Data:"),
                0,
                1
        );

        campos.add(
                data,
                1,
                1
        );

        campos.add(
                new Label("Valor:"),
                0,
                2
        );

        campos.add(
                valor,
                1,
                2
        );

        campos.add(
                new Label("Descrição:"),
                0,
                3
        );

        campos.add(
                descricao,
                1,
                3
        );

        Button salvar =
                criarBotao(
                        "Salvar",
                        ORANGE
                );

        Button cancelar =
                new Button(
                        "Cancelar"
                );

        salvar.setOnAction(
                event -> {

                    try {

                        if (trabalhador.getValue()
                                == null) {

                            throw new IllegalArgumentException(
                                    "Selecione um trabalhador."
                            );
                        }

                        String valorTexto =
                                valor.getText()
                                        .trim()
                                        .replace(
                                                ".",
                                                ""
                                        )
                                        .replace(
                                                ",",
                                                "."
                                        );

                        BigDecimal valorPagamento =
                                new BigDecimal(
                                        valorTexto
                                );

                        Pagamento pagamento =
                                new Pagamento(
                                        trabalhador.getValue()
                                                .getId(),
                                        data.getValue(),
                                        valorPagamento,
                                        descricao.getText()
                                );

                        application.adicionarPagamento(
                                lista.getId(),
                                pagamento
                        );

                        ListaPagamento atualizada =
                                application.buscarLista(
                                        lista.getId()
                                ).orElseThrow();

                        tabela.setItems(
                                FXCollections.observableArrayList(
                                        atualizada.getPagamentos()
                                )
                        );

                        atualizarTotal(
                                total,
                                atualizada
                        );

                        stage.close();

                    } catch (Exception erro) {

                        mostrarErro(
                                stage,
                                erro.getMessage()
                        );
                    }
                }
        );

        cancelar.setOnAction(
                event ->
                        stage.close()
        );

        HBox botoes =
                new HBox(
                        10,
                        cancelar,
                        salvar
                );

        botoes.setAlignment(
                Pos.CENTER_RIGHT
        );

        VBox root =
                new VBox(
                        18,
                        campos,
                        botoes
                );

        root.setPadding(
                new Insets(22)
        );

        stage.setScene(
                new Scene(
                        root,
                        520,
                        300
                )
        );

        stage.showAndWait();
    }

    private static void carregarListas(
            TableView<ListaPagamento> tabela,
            PaymentsApplication application) {

        tabela.setItems(
                FXCollections.observableArrayList(
                        application.listarListas()
                )
        );
    }

    private static void atualizarTotal(
            Label label,
            ListaPagamento lista) {

        label.setText(
                "TOTAL: "
                        + formatarMoeda(
                        calcularTotal(lista)
                )
        );

        label.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + PRIMARY
                        + ";"
        );
    }

    private static BigDecimal calcularTotal(
            ListaPagamento lista) {

        return lista.getPagamentos()
                .stream()
                .map(Pagamento::getValor)
                .filter(valor -> valor != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private static String nomeTrabalhador(
            Pagamento pagamento,
            Map<UUID, Trabalhador> mapa) {

        Trabalhador trabalhador =
                mapa.get(
                        pagamento.getTrabalhadorId()
                );

        return trabalhador == null
                ? "Trabalhador não encontrado"
                : trabalhador.getNome();
    }

    private static String funcaoTrabalhador(
            Pagamento pagamento,
            Map<UUID, Trabalhador> mapa) {

        Trabalhador trabalhador =
                mapa.get(
                        pagamento.getTrabalhadorId()
                );

        return trabalhador == null
                ? "-"
                : trabalhador.getFuncao();
    }

    private static String formatarPeriodo(
            ListaPagamento lista) {

        return DATE.format(
                lista.getDataInicio()
        )
                + " a "
                + DATE.format(
                lista.getDataFim()
        );
    }

    private static String formatarStatus(
            StatusListaPagamento status) {

        return switch (status) {
            case EM_EDICAO -> "EM EDIÇÃO";
            case CONFERIDA -> "CONFERIDA";
            case FECHADA -> "FECHADA";
            case CANCELADA -> "CANCELADA";
        };
    }

    private static String formatarMoeda(
            BigDecimal valor) {

        return String.format(
                java.util.Locale.US,
                "R$ %,.2f",
                valor
        ).replace(
                ",",
                "#"
        ).replace(
                ".",
                ","
        ).replace(
                "#",
                "."
        );
    }

    private static Button criarBotao(
            String texto,
            String cor) {

        Button botao =
                new Button(texto);

        botao.setPrefHeight(34);
        botao.setFocusTraversable(false);

        botao.setStyle(
                "-fx-background-color: "
                        + cor
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 8;"
                        + "-fx-padding: 8 16 8 16;"
        );

        return botao;
    }

    private static void mostrarAviso(
            Stage owner,
            String mensagem) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.initOwner(owner);
        alert.setTitle(
                "Sys-All"
        );
        alert.setHeaderText(
                "Atenção"
        );
        alert.setContentText(
                mensagem
        );

        alert.showAndWait();
    }

    private static void mostrarErro(
            Stage owner,
            String mensagem) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.initOwner(owner);
        alert.setTitle(
                "Sys-All"
        );
        alert.setHeaderText(
                "Não foi possível realizar a operação"
        );
        alert.setContentText(
                mensagem == null
                        ? "Ocorreu um erro inesperado."
                        : mensagem
        );

        alert.showAndWait();
    }

    private static void mostrarInfo(
            Stage owner,
            String mensagem) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.initOwner(owner);
        alert.setTitle(
                "Sys-All"
        );
        alert.setHeaderText(
                null
        );
        alert.setContentText(
                mensagem
        );

        alert.showAndWait();
    }
}