package br.com.sysall.payments.application;

import br.com.sysall.payments.domain.model.Trabalhador;

import java.math.BigDecimal;

public class PaymentsApplicationTest {

    public static void main(String[] args) {

        PaymentsApplication application =
                new PaymentsApplication();

        Trabalhador trabalhador =
                new Trabalhador(
                        "João da Silva",
                        "Pedreiro",
                        new BigDecimal("450.00")
                );

        application.cadastrarTrabalhador(
                trabalhador
        );

        application.listarTrabalhadores()
                .forEach(item ->
                        System.out.println(
                                item.getNome()
                                        + " - "
                                        + item.getFuncao()
                                        + " - R$ "
                                        + item.getValorPadrao()
                        )
                );
    }
}