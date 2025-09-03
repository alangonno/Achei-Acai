package br.com.acheiacai.controller;

import br.com.acheiacai.dao.UsuarioDAO;
import br.com.acheiacai.model.Usuario;
import br.com.acheiacai.uteis.JwtUtil;
import br.com.acheiacai.uteis.PasswordUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    UsuarioDAO usuarioDAO = new UsuarioDAO();

    ObjectMapper conversor =  new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String jsonString = request.
                getReader().
                lines().
                collect(Collectors.joining(System.lineSeparator()));

        if (jsonString == null || jsonString.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"erro\":\"Corpo da requisição está vazio.\"}");
        }

        try {

            Usuario usuarioRecebido = conversor.readValue(jsonString, Usuario.class);

            Usuario usuarioDB = usuarioDAO.buscarUsuarioNome(usuarioRecebido.nomeUsuario());

            if (usuarioDB == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print("{\"erro\":\"Utilizador inválido.\"}");
            }

            String senhaHash = usuarioDB.senha();
            String senhaBruta = usuarioRecebido.senha();

            boolean senhaCorreta = PasswordUtil.verificarSenha(senhaBruta, senhaHash);

            if(senhaCorreta) {

                String token = JwtUtil.gerarToken(usuarioDB.nomeUsuario());

                response.setStatus(HttpServletResponse.SC_OK);

                String respostaJson = "{\"token\": \"" + token + "\"}";
                response.getWriter().print(respostaJson);


            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print("{\"erro\":\"Senha inválida.\"}");
            }

        } catch (JsonProcessingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"erro\":\"JSON mal formatado.\"}");
            e.printStackTrace();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\":\"Ocorreu um erro no servidor ao aceder ao banco de dados.\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\":\"Ocorreu um erro inesperado no servidor.\"}");
            e.printStackTrace();
        }
    }
}
