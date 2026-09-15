package br.com.sysall.payments.domain.model;

import br.com.sysall.payments.domain.enums.StatusListaPagamento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListaPagamento {

    private UUID id;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private StatusListaPagamento status;
    private List<Pagamento> pagamentos;

    public ListaPagamento() {
        this.id = UUID.randomUUID();
        this.status = StatusListaPagamento.EM_EDICAO;
        this.pagamentos = new ArrayList<>();
    }

    public ListaPagamento(
            LocalDate dataInicio,
            LocalDate dataFim) {

        this.id = UUID.randomUUID();
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusListaPagamento.EM_EDICAO;
        this.pagamentos = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public StatusListaPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusListaPagamento status) {
        this.status = status;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void adicionarPagamento(Pagamento pagamento) {
        pagamentos.add(pagamento);
    }
}