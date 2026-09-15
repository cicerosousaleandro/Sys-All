package br.com.sysall.payments.domain.repository;

import br.com.sysall.payments.domain.model.Pagamento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository {

    Pagamento salvar(
            UUID listaId,
            Pagamento pagamento
    );

    Optional<Pagamento> buscarPorId(UUID id);

    List<Pagamento> listarPorLista(UUID listaId);

    void atualizar(Pagamento pagamento);

    void excluir(UUID id);

    void excluirPorLista(UUID listaId);
}