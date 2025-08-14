package br.com.acheiacai.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import br.com.acheiacai.model.Produto;
import br.com.acheiacai.uteis.FabricaConexao;

public class ProdutoDAO{

    private Connection conexao;

    public Connection getConexao() {
        try { 
            if (conexao != null && !conexao.isClosed()) {
                return conexao;
            }
        } catch (SQLException e) {

        }

        this.conexao = FabricaConexao.getConexao();
        return conexao;
    }

    

public ArrayList listarTodos() {

    String sql = "SELECT * FROM produtos";
    ArrayList<Produto> produtos = new ArrayList<>();

    try (Connection conexao = getConexao();
        PreparedStatement stmt = conexao.prepareStatement(sql);
        ResultSet resultado = stmt.executeQuery()) {

            while(resultado.next()) {
                Long id = resultado.getLong("id");
                String nome = resultado.getString("nome");
                String tipo = resultado.getString("tipo");
                String variacao = resultado.getString("variacao");
                String tamanho = resultado.getString("tamanho");
                BigDecimal preco = resultado.getBigDecimal("preco");
                
                Produto produto = new Produto(id, nome, tipo, variacao, tamanho, preco);
                produtos.add(produto);
                }

            } catch (SQLException e) {
                    System.err.println("Erro ao listar produtos!");
                    e.printStackTrace();
                }

        return produtos;
    
    }
}