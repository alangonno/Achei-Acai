package com.acheiacai.db;

import br.com.acheiacai.dao.ProdutoDAO;
import br.com.acheiacai.model.Produto;
import br.com.acheiacai.uteis.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

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


