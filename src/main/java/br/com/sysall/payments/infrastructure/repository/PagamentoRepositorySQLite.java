package br.com.sysall.payments.infrastructure.repository;

import br.com.sysall.payments.domain.model.Pagamento;
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

public class PagamentoRepositorySQLite
        implements PagamentoRepository {

    @Override
    public Pagamento salvar(
            UUID listaId,
            Pagamento pagamento) {

        String sql = """
                INSERT INTO pagamentos (
                    id,
                    lista_id,
                    trabalhador_id,
                    data,
                    valor,
                    descricao
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        PaymentsDatabase.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    pagamento.getId().toString()
            );

            statement.setString(
                    2,
                    listaId.toString()
            );

            statement.setString(
                    3,
                    pagamento.getTrabalhadorId().toString()
            );

            statement.setString(
                    4,
                    pagamento.getData().toString()
            );

            statement.setBigDecimal(
                    5,
                    pagamento.getValor()
            );

            statement.setString(
                    6,
                    pagamento.getDescricao()
            );

            statement.executeUpdate();

            return pagamento;

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível salvar o pagamento.",
                    erro
            );
        }
    }

    @Override
    public Optional<Pagamento> buscarPorId(UUID id) {

        String sql = """
                SELECT
                    id,
                    trabalhador_id,
                    data,
                    valor,
                    descricao
                FROM pagamentos
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

            try (ResultSet result =
                         statement.executeQuery()) {

                if (result.next()) {
                    return Optional.of(
                            mapear(result)
                    );
                }
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível buscar o pagamento.",
                    erro
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Pagamento> listarPorLista(
            UUID listaId) {

        String sql = """
                SELECT
                    id,
                    trabalhador_id,
                    data,
                    valor,
                    descricao
                FROM pagamentos
                WHERE lista_id = ?
                ORDER BY data, id
                """;

        List<Pagamento> pagamentos =
                new ArrayList<>();

        try (
                Connection connection =
                        PaymentsDatabase.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    listaId.toString()
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {
                    pagamentos.add(
                            mapear(result)
                    );
                }
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível carregar os pagamentos da lista.",
                    erro
            );
        }

        return pagamentos;
    }

    @Override
    public void atualizar(Pagamento pagamento) {

        String sql = """
                UPDATE pagamentos
                SET
                    trabalhador_id = ?,
                    data = ?,
                    valor = ?,
                    descricao = ?
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
                    pagamento.getTrabalhadorId().toString()
            );

            statement.setString(
                    2,
                    pagamento.getData().toString()
            );

            statement.setBigDecimal(
                    3,
                    pagamento.getValor()
            );

            statement.setString(
                    4,
                    pagamento.getDescricao()
            );

            statement.setString(
                    5,
                    pagamento.getId().toString()
            );

            int alterados =
                    statement.executeUpdate();

            if (alterados == 0) {
                throw new IllegalStateException(
                        "Pagamento não encontrado."
                );
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível atualizar o pagamento.",
                    erro
            );
        }
    }

    @Override
    public void excluir(UUID id) {

        String sql = """
                DELETE FROM pagamentos
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
                    "Não foi possível excluir o pagamento.",
                    erro
            );
        }
    }

    @Override
    public void excluirPorLista(UUID listaId) {

        String sql = """
                DELETE FROM pagamentos
                WHERE lista_id = ?
                """;

        try (
                Connection connection =
                        PaymentsDatabase.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    listaId.toString()
            );

            statement.executeUpdate();

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível excluir os pagamentos da lista.",
                    erro
            );
        }
    }

    private Pagamento mapear(
            ResultSet result) throws SQLException {

        Pagamento pagamento =
                new Pagamento();

        pagamento.setId(
                UUID.fromString(
                        result.getString("id")
                )
        );

        pagamento.setTrabalhadorId(
                UUID.fromString(
                        result.getString("trabalhador_id")
                )
        );

        pagamento.setData(
                LocalDate.parse(
                        result.getString("data")
                )
        );

        pagamento.setValor(
                result.getBigDecimal("valor")
        );

        pagamento.setDescricao(
                result.getString("descricao")
        );

        return pagamento;
    }
}