package br.com.sysall.payments.infrastructure.printing;

import br.com.sysall.payments.domain.model.ListaPagamento;
import br.com.sysall.payments.domain.model.Pagamento;
import br.com.sysall.payments.domain.model.Trabalhador;

import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class PaymentsPrintService {

    private static final String COMPANY_NAME =
            "FIAÇÃO PATAMUTÉ LTDA";

    private static final String COMPANY_CNPJ =
            "CNPJ(MF): 070.112.925/0001-00";

    private static final String COMPANY_STATE_REGISTRATION =
            "INSCRIÇÃO ESTADUAL: 16.102.996-5";

    private static final String COMPANY_ADDRESS =
            "Via de Acesso BR 230, KM 496 B, Aeroporto - Cajazeiras - Paraíba";

    private static final String COMPANY_PHONE =
            "Fone: (83) 3531-4460";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final NumberFormat CURRENCY_FORMAT =
            NumberFormat.getCurrencyInstance(
                    new Locale("pt", "BR")
            );

    private static final double PAGE_WIDTH = 595;
    private static final double PAGE_HEIGHT = 842;

    private PaymentsPrintService() {
    }

    public static void imprimirRelatorioSemanal(
            ListaPagamento lista,
            Map<UUID, Trabalhador> trabalhadores) {

        validarLista(lista);

        List<PaymentPrintItem> itens =
                criarItens(lista, trabalhadores);

        if (itens.isEmpty()) {
            mostrarAviso(
                    "A lista de pagamento não possui pagamentos."
            );
            return;
        }

        PrinterJob printerJob =
                PrinterJob.createPrinterJob();

        if (printerJob == null) {
            mostrarErro(
                    "Nenhuma impressora disponível."
            );
            return;
        }

        Printer printer =
                printerJob.getPrinter();

        PageLayout pageLayout =
                printer.createPageLayout(
                        printer.getDefaultPageLayout().getPaper(),
                        PageOrientation.PORTRAIT,
                        printer.getDefaultPageLayout().getLeftMargin(),
                        printer.getDefaultPageLayout().getRightMargin(),
                        printer.getDefaultPageLayout().getTopMargin(),
                        printer.getDefaultPageLayout().getBottomMargin()
                );

        if (!printerJob.showPrintDialog(null)) {
            return;
        }

        List<List<PaymentPrintItem>> paginas =
                dividirEmPaginas(
                        itens,
                        18
                );

        for (int i = 0; i < paginas.size(); i++) {

            boolean sucesso =
                    printerJob.printPage(
                            pageLayout,
                            criarPaginaRelatorio(
                                    lista,
                                    paginas.get(i),
                                    i + 1,
                                    paginas.size()
                            )
                    );

            if (!sucesso) {
                printerJob.endJob();

                mostrarErro(
                        "A impressão foi interrompida."
                );

                return;
            }
        }

        printerJob.endJob();
    }

    public static void imprimirFichasAssinatura(
            ListaPagamento lista,
            Map<UUID, Trabalhador> trabalhadores) {

        validarLista(lista);

        List<PaymentPrintItem> itens =
                criarItens(lista, trabalhadores);

        if (itens.isEmpty()) {
            mostrarAviso(
                    "A lista de pagamento não possui pagamentos."
            );
            return;
        }

        PrinterJob printerJob =
                PrinterJob.createPrinterJob();

        if (printerJob == null) {
            mostrarErro(
                    "Nenhuma impressora disponível."
            );
            return;
        }

        Printer printer =
                printerJob.getPrinter();

        PageLayout pageLayout =
                printer.createPageLayout(
                        printer.getDefaultPageLayout().getPaper(),
                        PageOrientation.PORTRAIT,
                        printer.getDefaultPageLayout().getLeftMargin(),
                        printer.getDefaultPageLayout().getRightMargin(),
                        printer.getDefaultPageLayout().getTopMargin(),
                        printer.getDefaultPageLayout().getBottomMargin()
                );

        if (!printerJob.showPrintDialog(null)) {
            return;
        }

        List<List<PaymentPrintItem>> paginas =
                dividirEmPaginas(
                        itens,
                        2
                );

        for (List<PaymentPrintItem> pagina :
                paginas) {

            Canvas canvas =
                    new Canvas(
                            PAGE_WIDTH,
                            PAGE_HEIGHT
                    );

            GraphicsContext graphics =
                    canvas.getGraphicsContext2D();

            for (int i = 0;
                 i < pagina.size();
                 i++) {

                double yInicial =
                        i == 0
                                ? 25
                                : PAGE_HEIGHT / 2 + 10;

                desenharFicha(
                        graphics,
                        pagina.get(i),
                        lista,
                        yInicial
                );
            }

            if (pagina.size() == 2) {
                desenharLinhaCorte(
                        graphics,
                        PAGE_HEIGHT / 2
                );
            }

            boolean sucesso =
                    printerJob.printPage(
                            pageLayout,
                            canvas
                    );

            if (!sucesso) {
                printerJob.endJob();

                mostrarErro(
                        "A impressão das fichas foi interrompida."
                );

                return;
            }
        }

        printerJob.endJob();
    }

    private static Canvas criarPaginaRelatorio(
            ListaPagamento lista,
            List<PaymentPrintItem> itens,
            int numeroPagina,
            int totalPaginas) {

        Canvas canvas =
                new Canvas(
                        PAGE_WIDTH,
                        PAGE_HEIGHT
                );

        GraphicsContext graphics =
                canvas.getGraphicsContext2D();

        desenharCabecalho(
                graphics,
                "PAGAMENTOS AVULSOS"
        );

        double y = 175;

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        10
                )
        );

        graphics.fillText(
                "Período: "
                        + formatarData(
                        lista.getDataInicio()
                )
                        + " a "
                        + formatarData(
                        lista.getDataFim()
                ),
                40,
                y
        );

        graphics.fillText(
                "Emissão: "
                        + formatarData(
                        LocalDate.now()
                ),
                390,
                y
        );

        y += 30;

        desenharCabecalhoTabela(
                graphics,
                y
        );

        y += 27;

        BigDecimal total =
                BigDecimal.ZERO;

        for (int i = 0;
             i < itens.size();
             i++) {

            PaymentPrintItem item =
                    itens.get(i);

            desenharLinhaTabela(
                    graphics,
                    item,
                    y,
                    i + 1
            );

            total =
                    total.add(
                            item.valor()
                    );

            y += 28;
        }

        y += 20;

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        graphics.fillText(
                "TOTAL:",
                40,
                y
        );

        graphics.fillText(
                formatarMoeda(total),
                430,
                y
        );

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        9
                )
        );

        graphics.fillText(
                "Quantidade de pagamentos: "
                        + itens.size(),
                40,
                y + 22
        );

        graphics.fillText(
                "Página "
                        + numeroPagina
                        + " de "
                        + totalPaginas,
                465,
                PAGE_HEIGHT - 30
        );

        desenharRodape(
                graphics
        );

        return canvas;
    }

    private static void desenharCabecalho(
            GraphicsContext graphics,
            String titulo) {

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        15
                )
        );

        graphics.fillText(
                COMPANY_NAME,
                40,
                42
        );

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        9
                )
        );

        graphics.fillText(
                COMPANY_CNPJ,
                40,
                60
        );

        graphics.fillText(
                COMPANY_STATE_REGISTRATION,
                40,
                76
        );

        graphics.fillText(
                COMPANY_ADDRESS,
                40,
                92
        );

        graphics.fillText(
                COMPANY_PHONE,
                40,
                108
        );

        graphics.strokeLine(
                40,
                120,
                PAGE_WIDTH - 40,
                120
        );

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        14
                )
        );

        graphics.fillText(
                titulo,
                40,
                150
        );
    }

    private static void desenharCabecalhoTabela(
            GraphicsContext graphics,
            double y) {

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        graphics.strokeLine(
                40,
                y - 10,
                PAGE_WIDTH - 40,
                y - 10
        );

        graphics.fillText(
                "Nº",
                45,
                y + 8
        );

        graphics.fillText(
                "TRABALHADOR",
                80,
                y + 8
        );

        graphics.fillText(
                "SERVIÇO",
                350,
                y + 8
        );

        graphics.fillText(
                "VALOR",
                490,
                y + 8
        );

        graphics.strokeLine(
                40,
                y + 17,
                PAGE_WIDTH - 40,
                y + 17
        );
    }

    private static void desenharLinhaTabela(
            GraphicsContext graphics,
            PaymentPrintItem item,
            double y,
            int numero) {

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        9
                )
        );

        graphics.fillText(
                String.valueOf(numero),
                45,
                y
        );

        graphics.fillText(
                limitarTexto(
                        item.trabalhador().getNome(),
                        36
                ),
                80,
                y
        );

        graphics.fillText(
                limitarTexto(
                        item.trabalhador().getFuncao(),
                        20
                ),
                350,
                y
        );

        graphics.fillText(
                formatarMoeda(
                        item.valor()
                ),
                490,
                y
        );

        graphics.strokeLine(
                40,
                y + 10,
                PAGE_WIDTH - 40,
                y + 10
        );
    }

    private static void desenharRodape(
            GraphicsContext graphics) {

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        8
                )
        );

        graphics.fillText(
                COMPANY_NAME,
                40,
                PAGE_HEIGHT - 30
        );
    }

    private static void desenharFicha(
            GraphicsContext graphics,
            PaymentPrintItem item,
            ListaPagamento lista,
            double y) {

        double altura = 370;

        graphics.strokeRect(
                40,
                y,
                PAGE_WIDTH - 80,
                altura
        );

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        13
                )
        );

        graphics.fillText(
                COMPANY_NAME,
                55,
                y + 28
        );

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        8
                )
        );

        graphics.fillText(
                COMPANY_CNPJ,
                55,
                y + 43
        );

        graphics.fillText(
                COMPANY_STATE_REGISTRATION,
                55,
                y + 56
        );

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        12
                )
        );

        graphics.fillText(
                "RECIBO DE PAGAMENTO AVULSO",
                55,
                y + 82
        );

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        10
                )
        );

        double linha =
                y + 115;

        graphics.fillText(
                "Trabalhador:",
                55,
                linha
        );

        graphics.fillText(
                item.trabalhador().getNome(),
                145,
                linha
        );

        linha += 28;

        graphics.fillText(
                "Serviço/Função:",
                55,
                linha
        );

        graphics.fillText(
                item.trabalhador().getFuncao(),
                145,
                linha
        );

        linha += 28;

        graphics.fillText(
                "Período:",
                55,
                linha
        );

        graphics.fillText(
                formatarData(
                        lista.getDataInicio()
                )
                        + " a "
                        + formatarData(
                        lista.getDataFim()
                ),
                145,
                linha
        );

        linha += 28;

        graphics.fillText(
                "Valor pago:",
                55,
                linha
        );

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );

        graphics.fillText(
                formatarMoeda(
                        item.valor()
                ),
                145,
                linha
        );

        graphics.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        9
                )
        );

        linha += 45;

        graphics.fillText(
                "Declaro ter recebido o valor acima referente",
                55,
                linha
        );

        graphics.fillText(
                "ao serviço realizado no período informado.",
                55,
                linha + 15
        );

        linha += 55;

        graphics.fillText(
                "Data: ____/____/________",
                55,
                linha
        );

        linha += 55;

        graphics.strokeLine(
                55,
                linha,
                260,
                linha
        );

        graphics.fillText(
                "Assinatura do trabalhador",
                85,
                linha + 15
        );

        graphics.strokeLine(
                315,
                linha,
                540,
                linha
        );

        graphics.fillText(
                "Nome legível",
                385,
                linha + 15
        );

        linha += 50;

        graphics.fillText(
                "RG/CPF: ______________________________________________",
                55,
                linha
        );
    }

    private static void desenharLinhaCorte(
            GraphicsContext graphics,
            double y) {

        graphics.setLineDashes(
                8,
                5
        );

        graphics.strokeLine(
                25,
                y,
                PAGE_WIDTH - 25,
                y
        );

        graphics.setLineDashes();
    }

    private static List<PaymentPrintItem> criarItens(
            ListaPagamento lista,
            Map<UUID, Trabalhador> trabalhadores) {

        List<PaymentPrintItem> itens =
                new ArrayList<>();

        for (Pagamento pagamento :
                lista.getPagamentos()) {

            Trabalhador trabalhador =
                    trabalhadores.get(
                            pagamento.getTrabalhadorId()
                    );

            if (trabalhador == null) {
                throw new IllegalStateException(
                        "Trabalhador do pagamento não encontrado."
                );
            }

            if (pagamento.getValor() == null) {
                throw new IllegalStateException(
                        "Pagamento sem valor encontrado."
                );
            }

            itens.add(
                    new PaymentPrintItem(
                            trabalhador,
                            pagamento.getValor()
                    )
            );
        }

        return itens;
    }

    private static List<List<PaymentPrintItem>> dividirEmPaginas(
            List<PaymentPrintItem> itens,
            int tamanhoPagina) {

        List<List<PaymentPrintItem>> paginas =
                new ArrayList<>();

        for (int inicio = 0;
             inicio < itens.size();
             inicio += tamanhoPagina) {

            int fim =
                    Math.min(
                            inicio + tamanhoPagina,
                            itens.size()
                    );

            paginas.add(
                    new ArrayList<>(
                            itens.subList(
                                    inicio,
                                    fim
                            )
                    )
            );
        }

        return paginas;
    }

    private static void validarLista(
            ListaPagamento lista) {

        if (lista == null) {
            throw new IllegalArgumentException(
                    "Lista de pagamento inválida."
            );
        }

        if (lista.getDataInicio() == null
                || lista.getDataFim() == null) {

            throw new IllegalArgumentException(
                    "A lista de pagamento precisa possuir período."
            );
        }

        if (lista.getDataFim()
                .isBefore(
                        lista.getDataInicio()
                )) {

            throw new IllegalArgumentException(
                    "A data final não pode ser anterior à data inicial."
            );
        }
    }

    private static String formatarData(
            LocalDate data) {

        return data.format(
                DATE_FORMATTER
        );
    }

    private static String formatarMoeda(
            BigDecimal valor) {

        return CURRENCY_FORMAT.format(
                valor
        );
    }

    private static String limitarTexto(
            String texto,
            int tamanho) {

        if (texto == null) {
            return "";
        }

        if (texto.length() <= tamanho) {
            return texto;
        }

        return texto.substring(
                0,
                tamanho - 3
        ) + "...";
    }

    private static void mostrarAviso(
            String mensagem) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING,
                        mensagem,
                        ButtonType.OK
                );

        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private static void mostrarErro(
            String mensagem) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR,
                        mensagem,
                        ButtonType.OK
                );

        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private record PaymentPrintItem(
            Trabalhador trabalhador,
            BigDecimal valor) {
    }
}