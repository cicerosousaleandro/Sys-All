package br.com.sysall.shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Centraliza o nome e a versão exibidos pela aplicação. */
public final class AppMetadata {
    private static final Properties PROPERTIES = load();

    private AppMetadata() {
    }

    public static String name() {
        return PROPERTIES.getProperty("app.name", "Sys-All");
    }

    public static String version() {
        return PROPERTIES.getProperty("app.version", "Desenvolvimento");
    }

    public static String releaseName() {
        return PROPERTIES.getProperty("app.releaseName", "");
    }

    private static Properties load() {
        var properties = new Properties();
        try (InputStream stream = AppMetadata.class.getResourceAsStream("/application.properties")) {
            if (stream != null) {
                properties.load(stream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível carregar os dados da aplicação.", exception);
        }
        return properties;
    }
}
