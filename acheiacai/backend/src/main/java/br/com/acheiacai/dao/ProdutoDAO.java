package br.com.acheiacai.dao;

import java.sql.Connection;
import java.sql.SQLException;
import br.com.acheiacai.uteis.FabricaConexao;

public class ProdutoDAO{

    private Connection conexao;


    private Connection getConexao() {
        try { 
            if (conexao != null && !conexao.isClosed()) {
                return conexao;
            }
        } catch (SQLException e) {

        }

        conexao = FabricaConexao.getConexao();
        return conexao;
    }

    

    public void listarProdutos() {
        String sql = "SELECT * FROM produtos";
        
    }
}