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

        Connection conexao = DriverManager.getConnection(url, usuario, senha);

        return conexao;

        //CRIAR EXEÇÃO ESPECIFICA SE FICAR VAZIO } catch (ClassNotFoundException e) {
        //     System.err.println("!!! DRIVER MYSQL NÃO ENCONTRADO !!!");
        //     e.printStackTrace(); // <--- ADICIONE ISTO
        //     throw new RuntimeException(e);
        } catch (SQLException e) {
            System.err.println("!!! FALHA AO CONECTAR AO BANCO DE DADOS !!!");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch(IOException e) {
            System.err.println("!!! Falha ao acessar properties");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        
    }

    private static Properties getProperties() throws IOException {
        Properties prop = new Properties();
        String path="/home/alan/conexao.properties";
        prop.load(FabricaConexao.class.getResourceAsStream(path));
        return prop;

    }

 
    
}
