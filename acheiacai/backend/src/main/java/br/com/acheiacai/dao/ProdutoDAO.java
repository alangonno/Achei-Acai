package br.com.acheiacai.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Objects;

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

    public ArrayList<Produto> listarTodos() {

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

    public void criarProduto(Produto produto) throws SQLException {

        String sql = "INSERT INTO produtos (nome, tipo, variacao, tamanho, preco)  VALUES(?, ?, ?, ?, ?)";

        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, produto.nome());
            stmt.setString(2, produto.tipo());
            stmt.setString(3, produto.variacao());
            stmt.setString(4, produto.tamanho());
            stmt.setBigDecimal(5, produto.preco());
            stmt.executeUpdate();

        }
    }

    public ArrayList<String> atualizarProduto(Produto produto) throws IllegalArgumentException, SQLException, Exception {

        ArrayList<String> linhasAlteradas = new ArrayList<String>();
        ArrayList<Object> parametros = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("UPDATE produtos SET ");

        //VERIFICAÇÃO DE CAMPOS

        //salva os parametros que vão ser alterados e adicionam no sql

        if (produto.id() == null) {
            throw new IllegalArgumentException();
        }

        if(produto.nome() != null && !produto.nome().isBlank()) {
            sql.append("nome = ?, ");
            parametros.add(produto.nome());
            linhasAlteradas.add("nome alterado para" + produto.nome());

        }

        if(produto.tipo() != null && !produto.tipo().isBlank()) {
            sql.append("tipo = ?, ");
            parametros.add(produto.tipo());
            linhasAlteradas.add("tipo alterado para " + produto.tipo());
        }

        if(produto.tamanho() != null && !produto.tamanho().isBlank()) {
            sql.append("tamanho = ?, ");
            parametros.add(produto.tamanho());
            linhasAlteradas.add("tamanho alterado para" + produto.tamanho());
        }

        if(produto.variacao() != null && !produto.variacao().isBlank()) {
            sql.append("variacao = ?, ");
            parametros.add(produto.variacao());
            linhasAlteradas.add("variacao alterada para" + produto.variacao());
        }

        if(produto.preco() != null && !(produto.preco().compareTo(BigDecimal.ZERO) <= 0)) {
            sql.append("preco = ?, ");
            parametros.add(produto.preco());
            linhasAlteradas.add("preco alterado para" + produto.preco());
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

            int idExistente = stmt.executeUpdate();

            if (idExistente == 0) { // Se 0 linhas forem afetadas não existe o ID
                throw new IllegalArgumentException();
            }

            return linhasAlteradas;

        }
    }

    public void deletarProduto (Produto produto) throws SQLException, IllegalArgumentException {

        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, produto.id());
            int linhasalteradas = stmt.executeUpdate();

            if (linhasalteradas == 0) {
                throw new IllegalArgumentException("ID não encontrado");
            }

        }

    }
}