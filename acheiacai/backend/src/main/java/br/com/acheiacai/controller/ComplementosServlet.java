package br.com.acheiacai.controller;

import br.com.acheiacai.dao.ComplementoCoberturaDAO;
import br.com.acheiacai.model.produtos.ComplementoCobertura;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@WebServlet("/complementos/*")
public class ComplementosServlet extends HttpServlet{

    ComplementoCoberturaDAO complementosDAO = new ComplementoCoberturaDAO();
    ObjectMapper conversor = new ObjectMapper();
    String tabela = "complementos";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long id = extrairIdUrl(request);

            if (id == null) {
                ArrayList<ComplementoCobertura> complementos = complementosDAO.listarTodos(tabela);
                String jsonComplementos = conversor.writeValueAsString(complementos);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(jsonComplementos);
                return ;
            }

            ComplementoCobertura complemento = complementosDAO.buscarID(id, tabela);
            String jsonComplemento= conversor.writeValueAsString(complemento);
            response.setContentType("application/json");
            response.getWriter().print(jsonComplemento);


        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String erroJson = "Falha ao processar a requisição de complementos. detalhe: " + e.getMessage();
            response.getWriter().print(erroJson);

            e.printStackTrace();
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String jsonString = request.
                    getReader().
                    lines().
                    collect(Collectors.joining(System.lineSeparator()));

            if (jsonString == null || jsonString.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Erro: Corpo da requisição está vazio.");
            }

            ComplementoCobertura dadosComplemento = conversor.readValue(jsonString, ComplementoCobertura.class); // passa Json para o Model
            ArrayList<String> erros = new ArrayList<>();

            if (dadosComplemento.nome() == null || dadosComplemento.nome().isBlank()) {
                erros.add("nome esta vazio");
            }

            if (dadosComplemento.preco() == null || dadosComplemento.preco().compareTo(BigDecimal.ZERO) <= 0) {
                erros.add("preço é 0 ou negativo");
            }

            if (!erros.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String jsonErros = conversor.writeValueAsString(erros);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(jsonErros);
                return;
            }

            Long id = complementosDAO.criar(dadosComplemento, tabela);
            ComplementoCobertura novoComplemento = new ComplementoCobertura(id, dadosComplemento);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.getWriter().print(conversor.writeValueAsString(novoComplemento));

        } catch (JsonProcessingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("Erro: O JSON enviado é inválido. Detalhes: " + e.getMessage());
            e.printStackTrace();

        } catch (SQLException e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro: Falha ao salvar o complemento no banco de dados.");
            e.printStackTrace();

        } catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro inesperado no servidor.");
            e.printStackTrace();
        }

    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = extrairIdUrl(request);
        if (id == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"erro\":\"ID de complemento inválido ou não fornecido na URL.\"}");
            return;
        }

        try { // DAO retorna o complemento que foi alterado

            ComplementoCobertura complementoExistente = complementosDAO.buscarID(id, tabela);
            if (complementoExistente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("{\"erro\":\" ID inexistente.\"}");
                return;
            }

            String jsonString = request.
                    getReader().
                    lines().
                    collect(Collectors.joining(System.lineSeparator()));

            ComplementoCobertura dadosComplemento = conversor.readValue(jsonString, ComplementoCobertura.class);

            ComplementoCobertura complementoAtualizado = new ComplementoCobertura( id, dadosComplemento); //Passa o id da URL para saber qual complemento

            complementosDAO.atualizar(complementoAtualizado, tabela);

            ComplementoCobertura complementoFinal = complementosDAO.buscarID(id, tabela);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(conversor.writeValueAsString(complementoFinal));

        } catch (JsonProcessingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("Erro: O JSON enviado é inválido. Detalhes: " + e.getMessage());
            e.printStackTrace();

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro: Falha ao salvar o complemento no banco de dados.");
            e.printStackTrace();

        } catch (IllegalArgumentException e){
            response.setStatus((HttpServletResponse.SC_NO_CONTENT));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
            e.printStackTrace();
        }

    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = extrairIdUrl(request);
        if (id == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"erro\":\"ID de complemento inválido ou não fornecido na URL.\"}");
            return;
        }

        try { // DAO retorna o complemento que foi alterado

            ComplementoCobertura complementoExistente = complementosDAO.buscarID(id, tabela);
            if (complementoExistente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("{\"erro\":\" ID inexistente.\"}");
                return;
            }

            complementosDAO.deletar(complementoExistente, tabela);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);


        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro: Falha ao salvar o complemento no banco de dados.");
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

