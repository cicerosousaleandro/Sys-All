package br.com.sysall.payments.infrastructure.repository;

import br.com.sysall.payments.domain.enums.StatusListaPagamento;
import br.com.sysall.payments.domain.model.ListaPagamento;
import br.com.sysall.payments.domain.model.Pagamento;
import br.com.sysall.payments.domain.repository.ListaPagamentoRepository;
import br.com.sysall.payments.domain.repository.PagamentoRepository;
import br.com.sysall.payments.infrastructure.database.PaymentsDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ListaPagamentoRepositorySQLite
        implements ListaPagamentoRepository {

    private final PagamentoRepository pagamentoRepository;

    public ListaPagamentoRepositorySQLite() {
        this(
                new PagamentoRepositorySQLite()
        );
    }

    public ListaPagamentoRepositorySQLite(
            PagamentoRepository pagamentoRepository) {

        this.pagamentoRepository =
                pagamentoRepository;
    }

    @Override
    public ListaPagamento salvar(
            ListaPagamento lista) {

        String sql = """
                INSERT INTO listas_pagamento (
                    id,
                    data_inicio,
                    data_fim,
                    status
                )
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        PaymentsDatabase.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    lista.getId().toString()
            );

            statement.setString(
                    2,
                    lista.getDataInicio().toString()
            );

            statement.setString(
                    3,
                    lista.getDataFim().toString()
            );

            statement.setString(
                    4,
                    lista.getStatus().name()
            );

            statement.executeUpdate();

            for (Pagamento pagamento :
                    lista.getPagamentos()) {

                pagamentoRepository.salvar(
                        lista.getId(),
                        pagamento
                );
            }

            return lista;

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível salvar a lista de pagamento.",
                    erro
            );
        }
    }

    @Override
    public Optional<ListaPagamento> buscarPorId(
            UUID id) {

        String sql = """
                SELECT
                    id,
                    data_inicio,
                    data_fim,
                    status
                FROM listas_pagamento
                WHERE id = ?
                """;

        try (
                Connection connection =
                        PaymentsDatabase.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    id.toString()
            );

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {

                    ListaPagamento lista =
                            mapear(result);

                    lista.getPagamentos().addAll(
                            pagamentoRepository.listarPorLista(
                                    id
                            )
                    );

                    return Optional.of(lista);
                }
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível buscar a lista de pagamento.",
                    erro
            );
        }

        return Optional.empty();
    }

    @Override
    public List<ListaPagamento> listarTodos() {

        String sql = """
                SELECT
                    id,
                    data_inicio,
                    data_fim,
                    status
                FROM listas_pagamento
                ORDER BY data_inicio DESC
                """;

        List<ListaPagamento> listas =
                new ArrayList<>();

        try (
                Connection connection =
                        PaymentsDatabase.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                ListaPagamento lista =
                        mapear(result);

                lista.getPagamentos().addAll(
                        pagamentoRepository.listarPorLista(
                                lista.getId()
                        )
                );

                listas.add(lista);
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível carregar as listas de pagamento.",
                    erro
            );
        }

        return listas;
    }

    @Override
    public List<ListaPagamento> buscarPorPeriodo(
            LocalDate dataInicio,
            LocalDate dataFim) {

        return listarTodos()
                .stream()
                .filter(lista ->
                        !lista.getDataFim()
                                .isBefore(dataInicio)
                                && !lista.getDataInicio()
                                .isAfter(dataFim)
                )
                .toList();
    }

    @Override
    public void atualizar(
            ListaPagamento lista) {

        String sql = """
                UPDATE listas_pagamento
                SET
                    data_inicio = ?,
                    data_fim = ?,
                    status = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        PaymentsDatabase.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    lista.getDataInicio().toString()
            );

            statement.setString(
                    2,
                    lista.getDataFim().toString()
            );

            statement.setString(
                    3,
                    lista.getStatus().name()
            );

            statement.setString(
                    4,
                    lista.getId().toString()
            );

            int alterados =
                    statement.executeUpdate();

            if (alterados == 0) {
                throw new IllegalStateException(
                        "Lista de pagamento não encontrada."
                );
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível atualizar a lista de pagamento.",
                    erro
            );
        }
    }

    @Override
    public void alterarStatus(
            UUID id,
            StatusListaPagamento status) {

        String sql = """
                UPDATE listas_pagamento
                SET status = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        PaymentsDatabase.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    status.name()
            );

            statement.setString(
                    2,
                    id.toString()
            );

            int alterados =
                    statement.executeUpdate();

            if (alterados == 0) {
                throw new IllegalStateException(
                        "Lista de pagamento não encontrada."
                );
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível alterar o status da lista de pagamento.",
                    erro
            );
        }
    }

    @Override
    public void excluir(UUID id) {

        String sql = """
                DELETE FROM listas_pagamento
                WHERE id = ?
                """;

        try (
                Connection connection =
                        PaymentsDatabase.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    id.toString()
            );

            statement.executeUpdate();

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível excluir a lista de pagamento.",
                    erro
            );
        }
    }

    private ListaPagamento mapear(
            ResultSet result) throws SQLException {

        ListaPagamento lista =
                new ListaPagamento(
                        LocalDate.parse(
                                result.getString("data_inicio")
                        ),
                        LocalDate.parse(
                                result.getString("data_fim")
                        )
                );

        lista.setId(
                UUID.fromString(
                        result.getString("id")
                )
        );

        lista.setStatus(
                StatusListaPagamento.valueOf(
                        result.getString("status")
                )
        );

        return lista;
    }
}