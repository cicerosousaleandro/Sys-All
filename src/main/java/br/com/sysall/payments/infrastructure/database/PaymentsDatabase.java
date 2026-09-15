package br.com.sysall.payments.infrastructure.database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class PaymentsDatabase {

    private static final Path DATABASE_DIRECTORY =
            Paths.get("data", "pagamentos");

    private static final Path DATABASE_PATH =
            DATABASE_DIRECTORY.resolve("pagamentos.db");

    private PaymentsDatabase() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Files.createDirectories(DATABASE_DIRECTORY);
        } catch (Exception erro) {
            throw new SQLException(
                    "Não foi possível criar o diretório do banco de pagamentos.",
                    erro
            );
        }

        Connection connection =
                DriverManager.getConnection(
                        "jdbc:sqlite:" + DATABASE_PATH
                );

        configurar(connection);

        return connection;
    }

    public static void inicializar() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS trabalhadores (
                        id TEXT PRIMARY KEY,
                        nome TEXT NOT NULL,
                        funcao TEXT NOT NULL,
                        valor_padrao NUMERIC NOT NULL
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS listas_pagamento (
                        id TEXT PRIMARY KEY,
                        data_inicio TEXT NOT NULL,
                        data_fim TEXT NOT NULL,
                        status TEXT NOT NULL
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS pagamentos (
                        id TEXT PRIMARY KEY,
                        lista_id TEXT NOT NULL,
                        trabalhador_id TEXT NOT NULL,
                        data TEXT NOT NULL,
                        valor NUMERIC NOT NULL,
                        descricao TEXT,
                        FOREIGN KEY (lista_id)
                            REFERENCES listas_pagamento(id)
                            ON DELETE CASCADE,
                        FOREIGN KEY (trabalhador_id)
                            REFERENCES trabalhadores(id)
                    )
                    """);

            statement.execute("""
                    CREATE INDEX IF NOT EXISTS idx_pagamentos_lista
                    ON pagamentos(lista_id)
                    """);

            statement.execute("""
                    CREATE INDEX IF NOT EXISTS idx_pagamentos_trabalhador
                    ON pagamentos(trabalhador_id)
                    """);

        } catch (SQLException erro) {
            throw new IllegalStateException(
                    "Não foi possível inicializar o banco de pagamentos.",
                    erro
            );
        }
    }

    private static void configurar(Connection connection)
            throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
    }

    public static Path getDatabasePath() {
        return DATABASE_PATH;
    }
}