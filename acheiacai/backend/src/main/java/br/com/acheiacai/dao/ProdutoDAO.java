package br.com.acheiacai.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

import br.com.acheiacai.model.produtos.Produto;
import static br.com.acheiacai.uteis.FabricaConexao.getConexao;

public class ProdutoDAO{

    public ArrayList<Produto> listarTodos() throws Exception{

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

    public Produto criarProduto(Produto produto) throws SQLException {

        String sql = "INSERT INTO produtos (nome, tipo, variacao, tamanho, preco)  VALUES(?, ?::tipo_produto, ?, ?, ?)";

        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, produto.nome());
            stmt.setString(2, produto.tipo());
            stmt.setString(3, produto.variacao());
            stmt.setString(4, produto.tamanho());
            stmt.setBigDecimal(5, produto.preco());
            stmt.executeUpdate();

            ResultSet resultado = stmt.getGeneratedKeys();
            if(resultado.next()) {
                Long novoId = resultado.getLong(1);
                return new Produto(novoId, produto);
                }
        }

        return null;
    }

    public Produto atualizarProduto(Produto produto) throws IllegalArgumentException, SQLException, Exception {

        ArrayList<Object> parametros = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("UPDATE produtos SET ");

        //VERIFICAÇÃO DE CAMPOS

        //salva os parametros que vão ser alterados e adicionam no sql

        if (buscarID(produto.id()) == null) {
            throw new IllegalArgumentException();
        }

        if(produto.nome() != null && !produto.nome().isBlank()) {
            sql.append("nome = ?, ");
            parametros.add(produto.nome());

        }

        if(produto.tipo() != null && !produto.tipo().isBlank()) {
            sql.append("tipo = ?, ");
            parametros.add(produto.tipo());
        }

        if(produto.tamanho() != null && !produto.tamanho().isBlank()) {
            sql.append("tamanho = ?, ");
            parametros.add(produto.tamanho());
        }

        if(produto.variacao() != null && !produto.variacao().isBlank()) {
            sql.append("variacao = ?, ");
            parametros.add(produto.variacao());
        }

        if(produto.preco() != null && !(produto.preco().compareTo(BigDecimal.ZERO) <= 0)) {
            sql.append("preco = ?, ");
            parametros.add(produto.preco());
        }

        if (parametros.isEmpty()) {
            throw new Exception("Falta dados para atualização");
        }

        sql.delete(sql.length() - 2, sql.length());

        sql.append(" WHERE id = ?");

        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql.toString())) {


            for(int i = 0; i < parametros.size(); i++) { //Adiciona os parametros no Statement
                stmt.setObject(i + 1, parametros.get(i));
            }

            stmt.setLong(parametros.size() + 1, produto.id());

            stmt.executeUpdate();

            Long id = buscarID(produto.id()).id();

            return new Produto(id, produto);

        }
    }

    public void deletarProduto (Produto produto) throws SQLException, IllegalArgumentException, Exception {

        String sql = "DELETE FROM produtos WHERE id = ?";

        if (buscarID(produto.id()) == null ) {
            throw new IllegalArgumentException();
        }

        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, produto.id());
            stmt.executeUpdate();

        }

    }

    public Produto buscarID(Long id) throws SQLException, Exception{ //Traz o produto
        String sql = "SELECT * FROM produtos WHERE id = ?";

        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet resultado = stmt.executeQuery();
            if(resultado.next()) {

                String nome = resultado.getString("nome");
                String tipo = resultado.getString("tipo");
                String variacao = resultado.getString("variacao");
                String tamanho = resultado.getString("tamanho");
                BigDecimal preco = resultado.getBigDecimal("preco");

                Produto produto = new Produto(id, nome, tipo, variacao, tamanho, preco);
                return produto;
            }
            return null;

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

}