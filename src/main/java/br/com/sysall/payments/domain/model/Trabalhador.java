package br.com.sysall.payments.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Trabalhador {

    private UUID id;
    private String nome;
    private String funcao;
    private BigDecimal valorPadrao;

    public Trabalhador() {
        this.id = UUID.randomUUID();
    }

    public Trabalhador(String nome, String funcao, BigDecimal valorPadrao) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.funcao = funcao;
        this.valorPadrao = valorPadrao;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public BigDecimal getValorPadrao() {
        return valorPadrao;
    }

    public void setValorPadrao(BigDecimal valorPadrao) {
        this.valorPadrao = valorPadrao;
    }
}