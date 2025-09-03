package br.com.acheiacai.dao;

import br.com.acheiacai.model.Usuario;
import br.com.acheiacai.uteis.FabricaConexao;
import br.com.acheiacai.uteis.PasswordUtil;

import java.sql.*;

public class UsuarioDAO {

    public Long criarUsuario(Usuario usuario) throws SQLException{

        String sql = "INSERT INTO usuarios(nome_usuario, senha_hash, funcao) VALUES(?, ?, ?)";

        String senhaHash = PasswordUtil.gerarHash(usuario.senha());

        try (Connection conexao = FabricaConexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.nomeUsuario());
            stmt.setString(2, senhaHash);
            stmt.setString(3, usuario.funcao());
            stmt.executeUpdate();

            ResultSet resultado = stmt.getGeneratedKeys();
            while (resultado.next()) {
                return resultado.getLong(1); //retorna id
            }

        }

        return 0L;

    }

    public Usuario buscarUsuarioNome(String nome) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE nome = ?";

        try(Connection conexao = FabricaConexao.getConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                Long id  = resultado.getLong("id");
                String senha = resultado.getString("senha_hash");
                String funcao = resultado.getString("funcao");

                return new Usuario(id, nome, senha, funcao);
            }
        }

        return null;
    }

}
