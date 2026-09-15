package br.com.sysall.payments.domain.repository;

import br.com.sysall.payments.domain.enums.StatusListaPagamento;
import br.com.sysall.payments.domain.model.ListaPagamento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListaPagamentoRepository {

    ListaPagamento salvar(ListaPagamento lista);

    Optional<ListaPagamento> buscarPorId(UUID id);

    List<ListaPagamento> listarTodos();

    List<ListaPagamento> buscarPorPeriodo(
            LocalDate inicio,
            LocalDate fim
    );

    void atualizar(ListaPagamento lista);

    void alterarStatus(
            UUID id,
            StatusListaPagamento status
    );

    void excluir(UUID id);
}