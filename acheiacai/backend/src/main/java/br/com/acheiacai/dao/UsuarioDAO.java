package br.com.acheiacai.dao;

import br.com.acheiacai.model.Usuario;
import br.com.acheiacai.uteis.FabricaConexao;


import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO {

    public ArrayList<Usuario> listarTodos() throws SQLException{

        String sql = "SELECT * FROM usuarios";
        ArrayList<Usuario> usuarios = new ArrayList<>();

        try (Connection conexao = FabricaConexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            ResultSet resultado = stmt.executeQuery();

            while (resultado.next()) {
                Long id = resultado.getLong("id");
                String nome = resultado.getString("nome_usuario");
                String funcao = resultado.getString("funcao");

                usuarios.add(new Usuario(id, nome, null,  funcao));

            }

            return usuarios;

        }

    }

    public Usuario criarUsuario(Usuario usuario) throws SQLException {

        String sql = "INSERT INTO usuarios(nome_usuario, senha_hash, funcao) VALUES(?, ?, ?::funcao_usuario)";

        try (Connection conexao = FabricaConexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.nomeUsuario());
            stmt.setString(2, usuario.senha());
            stmt.setString(3, usuario.funcao());
            stmt.executeUpdate();

            return  buscarUsuarioNome(usuario.nomeUsuario());

        }

    }

    public Usuario buscarUsuarioNome(String nome) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE nome_usuario = ?";

        try(Connection conexao = FabricaConexao.getConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                Long id = resultado.getLong("id");
                String senha = resultado.getString("senha_hash");
                String funcao = resultado.getString("funcao");

                return new Usuario(id, nome, senha, funcao);
            }
        }

        return null;
    }

    public void deletarUsuario(Long id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conexao = FabricaConexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }

    }

    public Usuario buscarId(Long id) throws SQLException{

        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try(Connection conexao = FabricaConexao.getConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet resultado = stmt.executeQuery();
            while(resultado.next()) {
                String nome = resultado.getString("nome_usuario");
                String funcao = resultado.getString("funcao");

                return new Usuario(id, nome, null, funcao);
            }

        }

        return null;

    }

}
