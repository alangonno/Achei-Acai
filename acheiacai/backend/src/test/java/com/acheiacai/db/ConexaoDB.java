package com.acheiacai.db;

import br.com.acheiacai.dao.ProdutoDAO;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static br.com.acheiacai.uteis.FabricaConexao.getConexao;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConexaoDB {
    private Connection conexao;
    private ProdutoDAO prodDAO = new ProdutoDAO();


    @Test
    void testeConexao() throws SQLException {
        conexao = getConexao();
        boolean sucesso = !(conexao == null || conexao.isClosed());
        assertTrue(sucesso);
    }
}


//    @Test
//    void lerProdutos() {
//        ArrayList<Produto> produtos = prodDAO.listarTodos();
//        System.out.println(produtos.toString());
//        boolean listavazia = produtos.isEmpty();
//        assertFalse(listavazia);
//    }


