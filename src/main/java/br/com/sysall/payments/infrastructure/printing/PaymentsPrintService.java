package br.com.sysall.payments.infrastructure.printing;

import br.com.sysall.payments.domain.model.ListaPagamento;
import br.com.sysall.payments.domain.model.Pagamento;
import br.com.sysall.payments.domain.model.Trabalhador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PaymentsPrintService {

    private static final String COMPANY_NAME = "FIAÇÃO PATAMUTÉ LTDA";
    private static final String COMPANY_CNPJ = "CNPJ(MF) 070.112.925/0001-00";
    private static final String COMPANY_STATE_REGISTRATION = "INSCRIÇÃO ESTADUAL: 16.102.996-5";
    private static final String COMPANY_ADDRESS = "Via de Acesso da BR 230, KM 496 B, Aeroporto - Cajazeiras - Paraíba";
    private static final String COMPANY_PHONE = "Fone: (83) 3531-4460";

    private static final String FONT = "Arial";
    private static final String FONT_SERIF = "Georgia";
    private static final String BLACK = "#111111";
    private static final String GRAY = "#E9EAEB";
    private static final String LIGHT_GRAY = "#F4F4F4";
    private static final String BORDER = "#B8B8B8";

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private PaymentsPrintService() {
    }

    public static boolean imprimirRelatorioSemanal(
            ListaPagamento lista,
            Map<UUID, Trabalhador> trabalhadores) {

        Objects.requireNonNull(lista, "A lista de pagamento é obrigatória.");
        Objects.requireNonNull(trabalhadores, "Os trabalhadores são obrigatórios.");

        validarLista(lista);

        List<PaymentPrintItem> itens = criarItens(lista, trabalhadores);

        PrinterJob job = PrinterJob.createPrinterJob();

        if (job == null) {
            throw new IllegalStateException(
                    "Não foi possível inicializar o serviço de impressão."
            );
        }

        try {
            if (!job.showPrintDialog(null)) {
                return false;
            }

            PageLayout layout = criarLayout(job);
            List<Node> paginas = criarPaginasRelatorio(lista, itens, layout);

            if (!job.printPage(layout, paginas.getFirst())) {
                throw new IllegalStateException(
                        "Não foi possível imprimir o relatório semanal."
                );
            }

            for (int i = 1; i < paginas.size(); i++) {
                if (!job.printPage(layout, paginas.get(i))) {
                    throw new IllegalStateException(
                            "Não foi possível imprimir uma das páginas do relatório semanal."
                    );
                }
            }

            if (!job.endJob()) {
                throw new IllegalStateException(
                        "A impressora não confirmou a conclusão do trabalho."
                );
            }

            return true;

        } catch (RuntimeException erro) {
            job.cancelJob();
            throw erro;
        }
    }

    public static boolean imprimirFichasAssinatura(
            ListaPagamento lista,
            Map<UUID, Trabalhador> trabalhadores) {

        Objects.requireNonNull(lista, "A lista de pagamento é obrigatória.");
        Objects.requireNonNull(trabalhadores, "Os trabalhadores são obrigatórios.");

        validarLista(lista);

        List<PaymentPrintItem> itens = criarItens(lista, trabalhadores);

        PrinterJob job = PrinterJob.createPrinterJob();

        if (job == null) {
            throw new IllegalStateException(
                    "Não foi possível inicializar o serviço de impressão."
            );
        }

        try {
            if (!job.showPrintDialog(null)) {
                return false;
            }

            PageLayout layout = criarLayout(job);
            List<Node> paginas = criarPaginasFichas(lista, itens, layout);

            for (Node pagina : paginas) {
                if (!job.printPage(layout, pagina)) {
                    throw new IllegalStateException(
                            "Não foi possível imprimir as fichas de assinatura."
                    );
                }
            }

            if (!job.endJob()) {
                throw new IllegalStateException(
                        "A impressora não confirmou a conclusão do trabalho."
                );
            }

            return true;

        } catch (RuntimeException erro) {
            job.cancelJob();
            throw erro;
        }
    }

    public static void mostrarPreviewRelatorio(
            Stage owner,
            ListaPagamento lista,
            Map<UUID, Trabalhador> trabalhadores) {

        mostrarPreview(
                owner,
                lista,
                trabalhadores,
                false
        );
    }

    public static void mostrarPreviewFichas(
            Stage owner,
            ListaPagamento lista,
            Map<UUID, Trabalhador> trabalhadores) {

        mostrarPreview(
                owner,
                lista,
                trabalhadores,
                true
        );
    }

    private static void mostrarPreview(
            Stage owner,
            ListaPagamento lista,
            Map<UUID, Trabalhador> trabalhadores,
            boolean fichas) {

        Objects.requireNonNull(lista, "A lista de pagamento é obrigatória.");
        Objects.requireNonNull(trabalhadores, "Os trabalhadores são obrigatórios.");

        validarLista(lista);

        List<PaymentPrintItem> itens = criarItens(lista, trabalhadores);
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job == null) {
            throw new IllegalStateException(
                    "Não foi possível inicializar o serviço de impressão. Verifique se há uma impressora instalada no Windows."
            );
        }

        PageLayout layout = criarLayout(job);
        List<Node> paginas = fichas
                ? criarPaginasFichas(lista, itens, layout)
                : criarPaginasRelatorio(lista, itens, layout);

        Stage preview = new Stage();
        preview.setTitle(
                fichas
                        ? "Sys-All - Pré-visualização das Fichas"
                        : "Sys-All - Pré-visualização do Relatório"
        );

        if (owner != null) {
            preview.initOwner(owner);
            preview.initModality(Modality.WINDOW_MODAL);
        }

        VBox paginasBox = new VBox(18);
        paginasBox.setAlignment(Pos.TOP_CENTER);
        paginasBox.setPadding(new Insets(22));
        paginasBox.setStyle("-fx-background-color: #D8D8D8;");

        double escala = 1.50;

        for (Node pagina : paginas) {
            Group grupo = new Group(pagina);
            grupo.setScaleX(escala);
            grupo.setScaleY(escala);

            StackPane moldura = new StackPane(grupo);
            moldura.setPrefSize(
                    layout.getPrintableWidth() * escala,
                    layout.getPrintableHeight() * escala
            );
            moldura.setMinSize(
                    layout.getPrintableWidth() * escala,
                    layout.getPrintableHeight() * escala
            );
            moldura.setMaxSize(
                    layout.getPrintableWidth() * escala,
                    layout.getPrintableHeight() * escala
            );
            moldura.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.22), 12, 0.2, 0, 3);"
            );

            paginasBox.getChildren().add(moldura);
        }

        ScrollPane scroll = new ScrollPane(paginasBox);
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.setStyle("-fx-background: #D8D8D8; -fx-background-color: #D8D8D8;");

        Label informacao = new Label(
                paginas.size() == 1
                        ? "1 página"
                        : paginas.size() + " páginas"
        );
        informacao.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #555555;"
        );

        Button cancelar = new Button("Cancelar");
        Button imprimir = new Button("Imprimir");

        cancelar.setPrefHeight(34);
        imprimir.setPrefHeight(34);
        imprimir.setDefaultButton(true);

        cancelar.setOnAction(event -> {
            job.cancelJob();
            preview.close();
        });

        imprimir.setOnAction(event -> {
            try {
                if (!job.showPrintDialog(preview)) {
                    return;
                }

                for (Node pagina : paginas) {
                    if (!job.printPage(layout, pagina)) {
                        throw new IllegalStateException(
                                fichas
                                        ? "Não foi possível imprimir as fichas de assinatura."
                                        : "Não foi possível imprimir o relatório semanal."
                        );
                    }
                }

                if (!job.endJob()) {
                    throw new IllegalStateException(
                            "A impressora não confirmou a conclusão do trabalho."
                    );
                }

                preview.close();

            } catch (RuntimeException erro) {
                job.cancelJob();

                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        erro.getMessage() == null
                                ? "Não foi possível concluir a impressão."
                                : erro.getMessage()
                );
                alert.setTitle("Sys-All");
                alert.setHeaderText("Erro de impressão");
                alert.initOwner(preview);
                alert.showAndWait();
            }
        });

        HBox botoes = new HBox(10, informacao, cancelar, imprimir);
        botoes.setAlignment(Pos.CENTER_RIGHT);
        botoes.setPadding(new Insets(10, 16, 10, 16));
        botoes.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #CCCCCC transparent transparent transparent;" +
                        "-fx-border-width: 1 0 0 0;"
        );

        HBox.setHgrow(informacao, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setCenter(scroll);
        root.setBottom(botoes);
        root.setStyle("-fx-background-color: #D8D8D8;");

        preview.setScene(new javafx.scene.Scene(root, 1450, 950));
        preview.setOnCloseRequest(event -> job.cancelJob());
        preview.setMinWidth(1200);
        preview.setMinHeight(820);
        preview.centerOnScreen();
        preview.show();
    }

    private static PageLayout criarLayout(PrinterJob job) {
        return job.getPrinter().createPageLayout(
                Paper.A4,
                PageOrientation.PORTRAIT,
                javafx.print.Printer.MarginType.DEFAULT
        );
    }

    private static List<Node> criarPaginasRelatorio(
            ListaPagamento lista,
            List<PaymentPrintItem> itens,
            PageLayout layout) {

        double width = layout.getPrintableWidth();
        double height = layout.getPrintableHeight();

        int itensPorPagina = 18;
        List<Node> paginas = new ArrayList<>();

        if (itens.isEmpty()) {
            paginas.add(
                    criarRelatorioPagina(
                            lista,
                            List.of(),
                            1,
                            1,
                            width,
                            height
                    )
            );
            return paginas;
        }

        int totalPaginas =
                (itens.size() + itensPorPagina - 1) / itensPorPagina;

        for (int inicio = 0; inicio < itens.size(); inicio += itensPorPagina) {
            int fim = Math.min(
                    inicio + itensPorPagina,
                    itens.size()
            );

            paginas.add(
                    criarRelatorioPagina(
                            lista,
                            itens.subList(inicio, fim),
                            paginas.size() + 1,
                            totalPaginas,
                            width,
                            height
                    )
            );
        }

        return paginas;
    }

    private static Node criarRelatorioPagina(
            ListaPagamento lista,
            List<PaymentPrintItem> itens,
            int pagina,
            int totalPaginas,
            double width,
            double height) {

        VBox root = new VBox(10);
        root.setPrefSize(width, height);
        root.setMinSize(width, height);
        root.setMaxSize(width, height);
        root.setPadding(new Insets(8));
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-font-family: '" + FONT + "';" +
                        "-fx-text-fill: " + BLACK + ";"
        );

        root.getChildren().add(criarCabecalhoRelatorio(lista, width));
        root.getChildren().add(criarTituloRelatorio(width));
        root.getChildren().add(criarMetadadosRelatorio(lista, width));

        VBox tabela = criarTabela(itens, width);
        VBox.setVgrow(tabela, Priority.ALWAYS);
        root.getChildren().add(tabela);

        if (pagina == totalPaginas) {
            root.getChildren().add(criarResumoRelatorio(itens, lista, width));
        }

        root.getChildren().add(
                criarRodape(
                        "Página " + pagina + " de " + totalPaginas,
                        width
                )
        );

        return root;
    }

    private static VBox criarCabecalhoRelatorio(
            ListaPagamento lista,
            double width) {

        Label nome = texto(
                COMPANY_NAME,
                20,
                true,
                FONT_SERIF
        );

        Label cadastro = texto(
                COMPANY_CNPJ + "    " + COMPANY_STATE_REGISTRATION,
                9,
                false,
                FONT
        );

        Label endereco = texto(
                COMPANY_ADDRESS,
                9,
                false,
                FONT
        );

        Label telefone = texto(
                COMPANY_PHONE,
                9,
                false,
                FONT
        );

        VBox cabecalho = new VBox(
                2,
                nome,
                cadastro,
                endereco,
                telefone
        );

        cabecalho.setAlignment(Pos.CENTER);
        cabecalho.setPrefWidth(width);
        cabecalho.setPadding(new Insets(2, 0, 8, 0));
        cabecalho.setStyle(
                "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        return cabecalho;
    }

    private static VBox criarTituloRelatorio(double width) {
        Label titulo = texto(
                "PAGAMENTOS AVULSOS",
                20,
                true,
                FONT_SERIF
        );

        Label subtitulo = texto(
                "R E L A T Ó R I O   S E M A N A L",
                10,
                true,
                FONT_SERIF
        );

        VBox box = new VBox(1, titulo, subtitulo);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(width);
        box.setPadding(new Insets(8, 6, 8, 6));
        box.setStyle(
                "-fx-background-color: " + GRAY + ";"
        );

        return box;
    }

    private static GridPane criarMetadadosRelatorio(
            ListaPagamento lista,
            double width) {

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(4);
        grid.setPrefWidth(width);
        grid.setPadding(new Insets(2, 4, 4, 4));

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(76);

        ColumnConstraints valueColumn = new ColumnConstraints();
        valueColumn.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(
                labelColumn,
                valueColumn
        );

        adicionarLinhaMetadado(
                grid,
                0,
                "Período:",
                formatarPeriodo(lista.getDataInicio(), lista.getDataFim())
        );

        adicionarLinhaMetadado(
                grid,
                1,
                "Emissão:",
                DATE_TIME.format(LocalDateTime.now())
        );

        adicionarLinhaMetadado(
                grid,
                2,
                "Observações:",
                ""
        );

        return grid;
    }

    private static void adicionarLinhaMetadado(
            GridPane grid,
            int linha,
            String rotulo,
            String valor) {

        Label label = texto(
                rotulo,
                9,
                true,
                FONT
        );

        Label value = texto(
                valor,
                9,
                false,
                FONT
        );

        grid.add(label, 0, linha);
        grid.add(value, 1, linha);
    }

    private static VBox criarTabela(
            List<PaymentPrintItem> itens,
            double width) {

        VBox tabela = new VBox();
        tabela.setPrefWidth(width);
        tabela.setStyle(
                "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-width: 1;"
        );

        GridPane header = criarLinhaTabela(
                "Nº",
                "Trabalhador",
                "Função / Serviço",
                "Valor (R$)",
                true,
                width
        );

        tabela.getChildren().add(header);

        int numero = 1;

        for (PaymentPrintItem item : itens) {
            tabela.getChildren().add(
                    criarLinhaTabela(
                            String.format("%02d", numero++),
                            item.trabalhador().getNome(),
                            item.trabalhador().getFuncao(),
                            formatarMoeda(item.pagamento().getValor()),
                            false,
                            width
                    )
            );
        }

        return tabela;
    }

    private static GridPane criarLinhaTabela(
            String numero,
            String trabalhador,
            String funcao,
            String valor,
            boolean cabecalho,
            double width) {

        GridPane linha = new GridPane();
        linha.setPrefWidth(width);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(10);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(38);

        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(32);

        ColumnConstraints c4 = new ColumnConstraints();
        c4.setPercentWidth(20);

        linha.getColumnConstraints().addAll(c1, c2, c3, c4);

        linha.add(celula(numero, cabecalho, Pos.CENTER), 0, 0);
        linha.add(celula(trabalhador, cabecalho, Pos.CENTER_LEFT), 1, 0);
        linha.add(celula(funcao, cabecalho, Pos.CENTER_LEFT), 2, 0);
        linha.add(celula(valor, cabecalho, Pos.CENTER_RIGHT), 3, 0);

        return linha;
    }

    private static Label celula(
            String valor,
            boolean cabecalho,
            Pos alinhamento) {

        Label label = texto(
                valor,
                cabecalho ? 8 : 8,
                cabecalho,
                FONT_SERIF
        );

        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(alinhamento);
        label.setPadding(new Insets(4, 5, 4, 5));
        label.setStyle(
                label.getStyle() +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-width: 0 1 1 0;" +
                        (cabecalho
                                ? "-fx-background-color: " + GRAY + ";"
                                : "")
        );

        return label;
    }

    private static HBox criarResumoRelatorio(
            List<PaymentPrintItem> itens,
            ListaPagamento lista,
            double width) {

        BigDecimal total = itens.stream()
                .map(item -> item.pagamento().getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Label quantidadeLabel = texto(
                "Quantidade de trabalhadores:\n" + itens.size(),
                10,
                false,
                FONT
        );

        Label totalLabel = texto(
                "Total da semana:\n" + formatarMoeda(total),
                13,
                true,
                FONT_SERIF
        );

        HBox resumo = new HBox(
                quantidadeLabel,
                totalLabel
        );

        HBox.setHgrow(
                quantidadeLabel,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                totalLabel,
                Priority.ALWAYS
        );

        quantidadeLabel.setPadding(new Insets(7, 10, 7, 10));
        totalLabel.setPadding(new Insets(7, 10, 7, 10));
        quantidadeLabel.setMaxWidth(Double.MAX_VALUE);
        totalLabel.setMaxWidth(Double.MAX_VALUE);
        quantidadeLabel.setAlignment(Pos.CENTER_LEFT);
        totalLabel.setAlignment(Pos.CENTER_RIGHT);

        resumo.setPrefWidth(width);
        resumo.setStyle(
                "-fx-background-color: " + LIGHT_GRAY + ";"
        );

        return resumo;
    }

    private static List<Node> criarPaginasFichas(
            ListaPagamento lista,
            List<PaymentPrintItem> itens,
            PageLayout layout) {

        double width = layout.getPrintableWidth();
        double height = layout.getPrintableHeight();
        double fichaHeight = (height - 18) / 2;

        List<Node> paginas = new ArrayList<>();

        for (int i = 0; i < itens.size(); i += 2) {
            VBox pagina = new VBox(8);
            pagina.setPrefSize(width, height);
            pagina.setMinSize(width, height);
            pagina.setMaxSize(width, height);
            pagina.setPadding(new Insets(4));
            pagina.setStyle("-fx-background-color: white;");

            double fichaWidth = width - 8;

            pagina.getChildren().add(
                    criarFicha(
                            lista,
                            itens.get(i),
                            fichaWidth,
                            fichaHeight
                    )
            );

            if (i + 1 < itens.size()) {
                pagina.getChildren().add(
                        criarLinhaCorte(fichaWidth)
                );
                pagina.getChildren().add(
                        criarFicha(
                                lista,
                                itens.get(i + 1),
                                fichaWidth,
                                fichaHeight
                        )
                );
            }

            paginas.add(pagina);
        }

        if (paginas.isEmpty()) {
            VBox pagina = new VBox();
            pagina.setPrefSize(width, height);
            pagina.getChildren().add(
                    criarFichaVazia(width - 8, fichaHeight)
            );
            paginas.add(pagina);
        }

        return paginas;
    }

    private static VBox criarFicha(
            ListaPagamento lista,
            PaymentPrintItem item,
            double width,
            double height) {

        VBox ficha = new VBox(4);
        ficha.setPrefSize(width, height);
        ficha.setMinSize(width, height);
        ficha.setMaxSize(width, height);
        ficha.setPadding(new Insets(10));
        ficha.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-font-family: '" + FONT + "';"
        );

        VBox cabecalho = new VBox(
                1,
                texto(COMPANY_NAME, 14, true, FONT_SERIF),
                texto(COMPANY_CNPJ + "    " + COMPANY_STATE_REGISTRATION, 7, false, FONT),
                texto(COMPANY_ADDRESS, 7, false, FONT),
                texto(COMPANY_PHONE, 7, false, FONT)
        );
        cabecalho.setAlignment(Pos.CENTER);
        cabecalho.setPadding(new Insets(0, 0, 5, 0));
        cabecalho.setStyle(
                "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        VBox titulo = new VBox(
                0,
                texto("COMPROVANTE DE PAGAMENTO", 13, true, FONT_SERIF),
                texto("TRABALHADOR AVULSO", 8, true, FONT_SERIF)
        );
        titulo.setAlignment(Pos.CENTER);
        titulo.setPadding(new Insets(5));
        titulo.setStyle(
                "-fx-background-color: " + GRAY + ";"
        );

        GridPane dados = new GridPane();
        dados.setHgap(8);
        dados.setVgap(4);
        dados.setPadding(new Insets(5));
        dados.setStyle(
                "-fx-background-color: " + LIGHT_GRAY + ";"
        );

        ColumnConstraints label = new ColumnConstraints();
        label.setPercentWidth(22);

        ColumnConstraints value = new ColumnConstraints();
        value.setPercentWidth(78);

        dados.getColumnConstraints().addAll(label, value);

        adicionarDadoFicha(dados, 0, "Trabalhador:", item.trabalhador().getNome());
        adicionarDadoFicha(dados, 1, "Função / Serviço:", item.trabalhador().getFuncao());
        adicionarDadoFicha(dados, 2, "Período:", formatarPeriodo(lista.getDataInicio(), lista.getDataFim()));

        HBox valor = new HBox(
                texto("Valor pago:", 11, true, FONT_SERIF),
                texto(formatarMoeda(item.pagamento().getValor()), 16, true, FONT_SERIF)
        );
        valor.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(valor.getChildren().get(1), Priority.ALWAYS);
        ((Label) valor.getChildren().get(1)).setAlignment(Pos.CENTER_RIGHT);
        ((Label) valor.getChildren().get(1)).setMaxWidth(Double.MAX_VALUE);
        valor.setPadding(new Insets(6));
        valor.setStyle(
                "-fx-background-color: " + GRAY + ";"
        );

        Label declaracao = texto(
                "Declaro ter recebido o valor acima referente ao serviço realizado no período informado, dando plena e total quitação, nada mais tendo a receber até a presente data.",
                8,
                false,
                FONT_SERIF
        );
        declaracao.setWrapText(true);
        declaracao.setPadding(new Insets(5, 8, 3, 8));

        Label data = texto(
                "Cajazeiras/PB, ____ de __________________ de ______.",
                8,
                false,
                FONT_SERIF
        );
        data.setPadding(new Insets(3, 8, 3, 8));

        Region assinaturaLinha = new Region();
        assinaturaLinha.setPrefHeight(18);
        assinaturaLinha.setStyle(
                "-fx-border-color: transparent transparent " + BLACK + " transparent;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        Label assinatura = texto(
                "Assinatura do trabalhador",
                7,
                false,
                FONT
        );
        assinatura.setAlignment(Pos.CENTER);

        HBox assinaturaBox = new HBox(12);
        assinaturaBox.setAlignment(Pos.BOTTOM_CENTER);

        VBox assinaturaColuna = new VBox(
                2,
                assinaturaLinha,
                assinatura
        );
        assinaturaColuna.setAlignment(Pos.CENTER);
        assinaturaColuna.setPrefWidth(width * 0.52);

        VBox identificacao = new VBox(
                3,
                texto("Nome legível: ______________________________", 7, false, FONT),
                texto("Documento (RG/CPF): __________________", 7, false, FONT)
        );
        identificacao.setPrefWidth(width * 0.40);

        assinaturaBox.getChildren().addAll(
                assinaturaColuna,
                identificacao
        );

        ficha.getChildren().addAll(
                cabecalho,
                titulo,
                dados,
                valor,
                declaracao,
                data,
                assinaturaBox
        );

        return ficha;
    }

    private static VBox criarFichaVazia(
            double width,
            double height) {

        VBox ficha = new VBox();
        ficha.setPrefSize(width, height);
        ficha.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-width: 1;"
        );

        return ficha;
    }

    private static void adicionarDadoFicha(
            GridPane grid,
            int linha,
            String rotulo,
            String valor) {

        grid.add(
                texto(rotulo, 7, true, FONT_SERIF),
                0,
                linha
        );

        grid.add(
                texto(valor, 8, false, FONT_SERIF),
                1,
                linha
        );
    }

    private static Region criarLinhaCorte(double width) {
        Region linha = new Region();
        linha.setPrefWidth(width);
        linha.setMinHeight(8);
        linha.setMaxHeight(8);
        linha.setStyle(
                "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
                        "-fx-border-style: dashed;" +
                        "-fx-border-width: 0 0 1 0;"
        );
        return linha;
    }

    private static HBox criarRodape(
            String pagina,
            double width) {

        Label paginaLabel = texto(
                pagina,
                7,
                false,
                FONT
        );

        HBox rodape = new HBox(
                paginaLabel
        );

        rodape.setAlignment(Pos.CENTER_RIGHT);
        rodape.setPrefWidth(width);
        rodape.setPadding(new Insets(3, 0, 0, 0));

        return rodape;
    }

    private static Label texto(
            String valor,
            double tamanho,
            boolean negrito,
            String familia) {

        Label label = new Label(valor);

        label.setStyle(
                "-fx-font-family: '" + familia + "';" +
                        "-fx-font-size: " + tamanho + "px;" +
                        "-fx-font-weight: " + (negrito ? "bold" : "normal") + ";" +
                        "-fx-text-fill: " + BLACK + ";"
        );

        return label;
    }

    private static List<PaymentPrintItem> criarItens(
            ListaPagamento lista,
            Map<UUID, Trabalhador> trabalhadores) {

        List<PaymentPrintItem> itens = new ArrayList<>();

        for (Pagamento pagamento : lista.getPagamentos()) {
            Trabalhador trabalhador = trabalhadores.get(
                    pagamento.getTrabalhadorId()
            );

            if (trabalhador == null) {
                throw new IllegalStateException(
                        "O trabalhador do pagamento não foi encontrado: "
                                + pagamento.getTrabalhadorId()
                );
            }

            if (pagamento.getValor() == null) {
                throw new IllegalStateException(
                        "Existe um pagamento sem valor definido."
                );
            }

            itens.add(
                    new PaymentPrintItem(
                            pagamento,
                            trabalhador
                    )
            );
        }

        return itens;
    }

    private static void validarLista(ListaPagamento lista) {
        if (lista.getDataInicio() == null || lista.getDataFim() == null) {
            throw new IllegalArgumentException(
                    "A lista precisa possuir data inicial e final."
            );
        }

        if (lista.getDataFim().isBefore(lista.getDataInicio())) {
            throw new IllegalArgumentException(
                    "A data final não pode ser anterior à data inicial."
            );
        }
    }

    private static String formatarPeriodo(
            LocalDate inicio,
            LocalDate fim) {

        return DATE.format(inicio)
                + " a "
                + DATE.format(fim);
    }

    private static String formatarMoeda(BigDecimal valor) {
        return CURRENCY.format(valor);
    }

    private record PaymentPrintItem(
            Pagamento pagamento,
            Trabalhador trabalhador) {
    }
}
