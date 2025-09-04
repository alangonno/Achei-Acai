package br.com.acheiacai.uteis;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class FabricaConexao {

    private static final Properties prop = new Properties();

    static {

        try (InputStream input = FabricaConexao.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.err.println("Aviso: 'database.properties' não encontrado. A aplicação dependerá das variáveis de ambiente.");
            } else {
                prop.load(input);
            }
        } catch (IOException ex) {
            System.err.println("Erro ao carregar o ficheiro de propriedades. Continuando com variáveis de ambiente.");
            ex.printStackTrace();
        }
    }

    public static Connection getConexao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = getDbUrl();
            if (url == null || url.isBlank()) {
                throw new SQLException("URL do banco de dados não configurada.");
            }

            return DriverManager.getConnection(url, getDbUser(), getDbPassword());

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("!!! FALHA CRÍTICA AO OBTER CONEXÃO COM O BANCO DE DADOS !!!");
            e.printStackTrace();
            throw new RuntimeException("Não foi possível conectar ao banco de dados.", e);
        }

    }

    public static String getDbUrl() {
        return System.getenv("DB_URL") != null ? System.getenv("DB_URL") : prop.getProperty("banco.url");
    }

    public static String getDbUser() {
        return System.getenv("DB_USER") != null ? System.getenv("DB_USER") : prop.getProperty("banco.usuario");
    }

    public static String getDbPassword() {
        return System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : prop.getProperty("banco.senha");
    }


    
}
