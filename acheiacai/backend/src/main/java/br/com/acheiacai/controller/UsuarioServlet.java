
package br.com.acheiacai.controller;

import br.com.acheiacai.dao.UsuarioDAO;
import br.com.acheiacai.model.Produto;
import br.com.acheiacai.model.Usuario;
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
import java.util.ArrayList;
import java.util.stream.Collectors;

@WebServlet("/usuarios/*")
public class UsuarioServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ObjectMapper conversor = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long id = extrairIdUrl(request);

            if (id == null) {
                ArrayList<Usuario> usuarios = usuarioDAO.listarTodos();
                String jsonUsuarios = conversor.writeValueAsString(usuarios);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(jsonUsuarios);
                return ;
            }

            Usuario usuario = usuarioDAO.buscarId(id);
            String jsonUsuario = conversor.writeValueAsString(usuario);
            response.setContentType("application/json");
            response.getWriter().print(jsonUsuario);


        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String erroJson = "Falha ao processar a requisição de produtos. detalhe: " + e.getMessage();
            response.getWriter().print(erroJson);

            e.printStackTrace();
        }



    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Pega a função que o AuthenticationFilter anexou à requisição.
        String funcaoDoRequisitante = (String) request.getAttribute("funcaoUsuario");

        if (!"ADMIN".equals(funcaoDoRequisitante)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print("{\"erro\":\"Acesso negado. Apenas administradores podem criar novos utilizadores.\"}");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonString = request.
                getReader().
                lines().
                collect(Collectors.joining(System.lineSeparator()));

        try {
            Usuario novoUsuario = conversor.readValue(jsonString, Usuario.class);

            if (novoUsuario.nomeUsuario() == null || novoUsuario.nomeUsuario().isBlank() ||
                    novoUsuario.senha() == null || novoUsuario.senha().isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"erro\":\"Nome de utilizador e senha são obrigatórios.\"}");
                return;
            }

            if (usuarioDAO.buscarUsuarioNome(novoUsuario.nomeUsuario()) != null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().print("{\"erro\":\"Este nome de utilizador já está em uso.\"}");
                return;
            }


            String senhaComHash = PasswordUtil.gerarHash(novoUsuario.senha());

            Usuario usuarioParaSalvar = new Usuario(null, novoUsuario.nomeUsuario(), senhaComHash, novoUsuario.funcao());

            Usuario usuarioSalvo = usuarioDAO.criarUsuario(usuarioParaSalvar);

            response.setStatus(HttpServletResponse.SC_CREATED);
            String respostaJson = "{\"id\":" + usuarioSalvo.id() + ",\"nomeUsuario\":\"" + usuarioSalvo.nomeUsuario() + "\",\"funcao\":\"" + usuarioSalvo.funcao() + "\"}";
            response.getWriter().print(respostaJson);

        } catch (JsonProcessingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"erro\":\"JSON mal formatado.\"}");
            e.printStackTrace();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\":\"Ocorreu um erro no servidor ao aceder ao banco de dados.\"}");
            e.printStackTrace();
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = extrairIdUrl(request);
        if (id == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"erro\":\"ID de usuario inválido ou não fornecido na URL.\"}");
            return;
        }

        try {

           Usuario usurioExistente = usuarioDAO.buscarId(id);
            if (usurioExistente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("{\"erro\":\" ID inexistente.\"}");
                return;
            }

            usuarioDAO.deletarUsuario(usurioExistente.id());
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);


        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro: Falha ao deletar user do banco de dados");
            e.printStackTrace();

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
            e.printStackTrace();
        }
    }

    private Long extrairIdUrl(HttpServletRequest request) throws NumberFormatException {

        String id_url = request.getPathInfo();

        if (id_url == null || id_url.equals("/")) {
            return null;
        }

        try {
            id_url = id_url.substring(1);
            return Long.parseLong(id_url);

        } catch (NumberFormatException e) {
            return null;
        }

    }
}
