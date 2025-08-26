package br.com.acheiacai.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

import br.com.acheiacai.model.ComplementoCobertura;
import br.com.acheiacai.model.Produto;

import static br.com.acheiacai.uteis.FabricaConexao.getConexao;


public class ComplementosCoberturasDAO {

    public ArrayList<ComplementoCobertura> listarTodos(String tabela) throws Exception{

        String sql = "SELECT * FROM " + tabela;
        ArrayList<ComplementoCobertura> complementosCoberturas = new ArrayList<>();

        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while(resultado.next()) {

                Long id = resultado.getLong("id");
                String nome = resultado.getString("nome");
                BigDecimal preco = resultado.getBigDecimal("preco_adicional");

                ComplementoCobertura compleCober = new ComplementoCobertura(id, nome, preco);
                complementosCoberturas.add(compleCober);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos!");
            e.printStackTrace();
        }

        return complementosCoberturas;

    }

    public Long criar(ComplementoCobertura compCober, String tabela) throws SQLException {

        String sql = "INSERT INTO "+ tabela +" (nome, preco_adicional)  VALUES(?, ?)";

        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, compCober.nome());
            stmt.setBigDecimal(2, compCober.preco());
            stmt.executeUpdate();

            ResultSet resultado = stmt.getGeneratedKeys();
            if(resultado.next()) {
                Long novoId = resultado.getLong(1);
                return novoId;
            }
        }

        return -1L;
    }

    public ComplementoCobertura atualizar(ComplementoCobertura compCober, String tabela) throws IllegalArgumentException, SQLException, Exception {

        ArrayList<Object> parametros = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder("UPDATE "+ tabela +" SET ");

        //VERIFICAÇÃO DE CAMPOS

        //salva os parametros que vão ser alterados e adicionam no sql

        if (buscarID(compCober.id(), tabela) == null) {
            throw new IllegalArgumentException();
        }

        if(compCober.nome() != null && !compCober.nome().isBlank()) {
            sql.append("nome = ?, ");
            parametros.add(compCober.nome());

        }

        if(compCober.preco() != null && !(compCober.preco().compareTo(BigDecimal.ZERO) <= 0)) {
            sql.append("preco_adicional = ?, ");
            parametros.add(compCober.preco());
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

            stmt.setLong(parametros.size() + 1, compCober.id());

            stmt.executeUpdate();

            return buscarID(compCober.id(), tabela);

        }
    }


    public ComplementoCobertura buscarID(Long id, String tabela) throws SQLException, Exception{ //Traz o produto
        String sql = "SELECT * FROM "+ tabela +" WHERE id = ?";

        try (Connection conexao = getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet resultado = stmt.executeQuery();
            if(resultado.next()) {

                String nome = resultado.getString("nome");
                BigDecimal preco = resultado.getBigDecimal("preco_adicional");

                ComplementoCobertura compCober = new ComplementoCobertura(id, nome, preco);
                return compCober;
            }
            return null;

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

}
