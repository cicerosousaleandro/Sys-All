package br.com.sysall.payments.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Pagamento {

    private UUID id;
    private UUID trabalhadorId;
    private LocalDate data;
    private BigDecimal valor;
    private String descricao;

    public Pagamento() {
        this.id = UUID.randomUUID();
    }

    public Pagamento(
            UUID trabalhadorId,
            LocalDate data,
            BigDecimal valor,
            String descricao) {

        this.id = UUID.randomUUID();
        this.trabalhadorId = trabalhadorId;
        this.data = data;
        this.valor = valor;
        this.descricao = descricao;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTrabalhadorId() {
        return trabalhadorId;
    }

    public void setTrabalhadorId(UUID trabalhadorId) {
        this.trabalhadorId = trabalhadorId;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}