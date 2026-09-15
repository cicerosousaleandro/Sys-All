package br.com.sysall.payments.infrastructure.repository;

import br.com.sysall.payments.domain.model.Trabalhador;
import br.com.sysall.payments.domain.repository.TrabalhadorRepository;
import br.com.sysall.payments.infrastructure.database.PaymentsDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TrabalhadorRepositorySQLite implements TrabalhadorRepository {

    @Override
    public Trabalhador salvar(Trabalhador trabalhador) {
        String sql = """
                INSERT INTO trabalhadores (
                    id,
                    nome,
                    funcao,
                    valor_padrao
                )
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = PaymentsDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, trabalhador.getId().toString());
            statement.setString(2, trabalhador.getNome());
            statement.setString(3, trabalhador.getFuncao());
            statement.setBigDecimal(4, trabalhador.getValorPadrao());

            statement.executeUpdate();

            return trabalhador;

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível salvar o trabalhador.",
                    erro
            );
        }
    }

    @Override
    public Optional<Trabalhador> buscarPorId(UUID id) {
        String sql = """
                SELECT
                    id,
                    nome,
                    funcao,
                    valor_padrao
                FROM trabalhadores
                WHERE id = ?
                """;

        try (Connection connection = PaymentsDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id.toString());

            try (ResultSet resultado = statement.executeQuery()) {

                if (!resultado.next()) {
                    return Optional.empty();
                }

                return Optional.of(mapear(resultado));
            }

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível buscar o trabalhador.",
                    erro
            );
        }
    }

    @Override
    public List<Trabalhador> listarTodos() {
        String sql = """
                SELECT
                    id,
                    nome,
                    funcao,
                    valor_padrao
                FROM trabalhadores
                ORDER BY nome
                """;

        List<Trabalhador> trabalhadores = new ArrayList<>();

        try (Connection connection = PaymentsDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {

            while (resultado.next()) {
                trabalhadores.add(mapear(resultado));
            }

            return trabalhadores;

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível listar os trabalhadores.",
                    erro
            );
        }
    }

    @Override
    public void atualizar(Trabalhador trabalhador) {
        String sql = """
                UPDATE trabalhadores
                SET
                    nome = ?,
                    funcao = ?,
                    valor_padrao = ?
                WHERE id = ?
                """;

        try (Connection connection = PaymentsDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, trabalhador.getNome());
            statement.setString(2, trabalhador.getFuncao());
            statement.setBigDecimal(3, trabalhador.getValorPadrao());
            statement.setString(4, trabalhador.getId().toString());

            statement.executeUpdate();

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível atualizar o trabalhador.",
                    erro
            );
        }
    }

    @Override
    public void excluir(UUID id) {
        String sql = """
                DELETE FROM trabalhadores
                WHERE id = ?
                """;

        try (Connection connection = PaymentsDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id.toString());

            statement.executeUpdate();

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível excluir o trabalhador.",
                    erro
            );
        }
    }

    private Trabalhador mapear(ResultSet resultado)
            throws SQLException {

        Trabalhador trabalhador = new Trabalhador();

        trabalhador.setId(
                UUID.fromString(
                        resultado.getString("id")
                )
        );

        trabalhador.setNome(
                resultado.getString("nome")
        );

        trabalhador.setFuncao(
                resultado.getString("funcao")
        );

        trabalhador.setValorPadrao(
                resultado.getBigDecimal("valor_padrao")
        );

        return trabalhador;
    }
}