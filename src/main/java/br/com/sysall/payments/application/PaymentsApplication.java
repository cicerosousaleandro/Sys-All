package br.com.sysall.payments.application;

import br.com.sysall.payments.domain.enums.StatusListaPagamento;
import br.com.sysall.payments.domain.model.ListaPagamento;
import br.com.sysall.payments.domain.model.Pagamento;
import br.com.sysall.payments.domain.model.Trabalhador;
import br.com.sysall.payments.domain.repository.ListaPagamentoRepository;
import br.com.sysall.payments.domain.repository.PagamentoRepository;
import br.com.sysall.payments.domain.repository.TrabalhadorRepository;
import br.com.sysall.payments.infrastructure.database.PaymentsDatabase;
import br.com.sysall.payments.infrastructure.repository.ListaPagamentoRepositorySQLite;
import br.com.sysall.payments.infrastructure.repository.PagamentoRepositorySQLite;
import br.com.sysall.payments.infrastructure.repository.TrabalhadorRepositorySQLite;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PaymentsApplication {

    private final TrabalhadorRepository trabalhadorRepository;
    private final ListaPagamentoRepository listaPagamentoRepository;
    private final PagamentoRepository pagamentoRepository;

    public PaymentsApplication() {
        this(
                new TrabalhadorRepositorySQLite(),
                new ListaPagamentoRepositorySQLite(),
                new PagamentoRepositorySQLite()
        );
    }

    public PaymentsApplication(
            TrabalhadorRepository trabalhadorRepository,
            ListaPagamentoRepository listaPagamentoRepository,
            PagamentoRepository pagamentoRepository) {

        PaymentsDatabase.inicializar();

        this.trabalhadorRepository =
                trabalhadorRepository;

        this.listaPagamentoRepository =
                listaPagamentoRepository;

        this.pagamentoRepository =
                pagamentoRepository;
    }

    public Trabalhador cadastrarTrabalhador(
            Trabalhador trabalhador) {

        return trabalhadorRepository.salvar(
                trabalhador
        );
    }

    public Optional<Trabalhador> buscarTrabalhador(
            UUID id) {

        return trabalhadorRepository.buscarPorId(
                id
        );
    }

    public List<Trabalhador> listarTrabalhadores() {

        return trabalhadorRepository.listarTodos();
    }

    public void atualizarTrabalhador(
            Trabalhador trabalhador) {

        trabalhadorRepository.atualizar(
                trabalhador
        );
    }

    public void excluirTrabalhador(
            UUID id) {

        trabalhadorRepository.excluir(
                id
        );
    }

    public ListaPagamento criarLista(
            LocalDate dataInicio,
            LocalDate dataFim) {

        validarPeriodo(
                dataInicio,
                dataFim
        );

        ListaPagamento lista =
                new ListaPagamento(
                        dataInicio,
                        dataFim
                );

        return listaPagamentoRepository.salvar(
                lista
        );
    }

    public List<ListaPagamento> listarListas() {

        return listaPagamentoRepository.listarTodos();
    }

    public Optional<ListaPagamento> buscarLista(
            UUID id) {

        return listaPagamentoRepository.buscarPorId(
                id
        );
    }

    public void adicionarPagamento(
            UUID listaId,
            Pagamento pagamento) {

        if (pagamento == null) {
            throw new IllegalArgumentException(
                    "O pagamento é obrigatório."
            );
        }

        ListaPagamento lista =
                listaPagamentoRepository
                        .buscarPorId(listaId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Lista de pagamento não encontrada."
                                )
                        );

        if (lista.getStatus()
                != StatusListaPagamento.EM_EDICAO) {

            throw new IllegalStateException(
                    "Não é possível alterar uma lista que não está em edição."
            );
        }

        if (pagamento.getTrabalhadorId() == null) {
            throw new IllegalArgumentException(
                    "O trabalhador é obrigatório."
            );
        }

        if (pagamento.getData() == null) {
            throw new IllegalArgumentException(
                    "A data do pagamento é obrigatória."
            );
        }

        if (pagamento.getData()
                .isBefore(lista.getDataInicio())
                || pagamento.getData()
                .isAfter(lista.getDataFim())) {

            throw new IllegalArgumentException(
                    "A data do pagamento deve estar dentro do período da lista."
            );
        }

        if (pagamento.getValor() == null
                || pagamento.getValor()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "O valor do pagamento deve ser maior que zero."
            );
        }

        trabalhadorRepository
                .buscarPorId(
                        pagamento.getTrabalhadorId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "O trabalhador informado não existe."
                        )
                );

        pagamentoRepository.salvar(
                listaId,
                pagamento
        );
    }

    public void removerPagamento(
            UUID listaId,
            UUID pagamentoId) {

        ListaPagamento lista =
                listaPagamentoRepository
                        .buscarPorId(listaId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Lista de pagamento não encontrada."
                                )
                        );

        if (lista.getStatus()
                != StatusListaPagamento.EM_EDICAO) {

            throw new IllegalStateException(
                    "Não é possível alterar uma lista que não está em edição."
            );
        }

        pagamentoRepository.excluir(
                pagamentoId
        );
    }

    public void conferirLista(
            UUID listaId) {

        ListaPagamento lista =
                carregarLista(listaId);

        if (lista.getStatus()
                != StatusListaPagamento.EM_EDICAO) {

            throw new IllegalStateException(
                    "Somente listas em edição podem ser conferidas."
            );
        }

        if (lista.getPagamentos().isEmpty()) {
            throw new IllegalStateException(
                    "A lista precisa possuir pelo menos um pagamento."
            );
        }

        lista.setStatus(
                StatusListaPagamento.CONFERIDA
        );

        listaPagamentoRepository.atualizar(
                lista
        );
    }

    public void fecharLista(
            UUID listaId) {

        ListaPagamento lista =
                carregarLista(listaId);

        if (lista.getStatus()
                != StatusListaPagamento.CONFERIDA) {

            throw new IllegalStateException(
                    "A lista precisa ser conferida antes de ser fechada."
            );
        }

        lista.setStatus(
                StatusListaPagamento.FECHADA
        );

        listaPagamentoRepository.atualizar(
                lista
        );
    }

    public void cancelarLista(
            UUID listaId) {

        ListaPagamento lista =
                carregarLista(listaId);

        if (lista.getStatus()
                == StatusListaPagamento.FECHADA) {

            throw new IllegalStateException(
                    "Uma lista fechada não pode ser cancelada."
            );
        }

        lista.setStatus(
                StatusListaPagamento.CANCELADA
        );

        listaPagamentoRepository.atualizar(
                lista
        );
    }

    private ListaPagamento carregarLista(
            UUID listaId) {

        return listaPagamentoRepository
                .buscarPorId(listaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lista de pagamento não encontrada."
                        )
                );
    }

    private void validarPeriodo(
            LocalDate dataInicio,
            LocalDate dataFim) {

        if (dataInicio == null
                || dataFim == null) {

            throw new IllegalArgumentException(
                    "As datas inicial e final são obrigatórias."
            );
        }

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException(
                    "A data final não pode ser anterior à data inicial."
            );
        }
    }
}