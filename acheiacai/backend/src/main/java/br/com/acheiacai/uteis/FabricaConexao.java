package br.com.acheiacai.uteis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class FabricaConexao {

    public static Connection getConexao() {

        try {
        Properties prop = getProperties();
        final String url = prop.getProperty("banco.url");
        final String usuario = prop.getProperty("banco.usuario");
        final String senha = prop.getProperty("banco.senha");

        return DriverManager.getConnection(url, usuario, senha);

        } catch (SQLException | IOException e) {
            throw new RuntimeException();
        }
    }

    private static Properties getProperties() throws IOException {
        Properties prop = new Properties();
        String path="/home/alan/conexao.properties";
        prop.load(FabricaConexao.class.getResourceAsStream(path));
        return prop;

    }

 
    
}
