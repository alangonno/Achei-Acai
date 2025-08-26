package br.com.acheiacai.controller;

import br.com.acheiacai.dao.ProdutoDAO;
import br.com.acheiacai.model.Produto;
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

@WebServlet("/produtos/*")
public class ProdutoServlet extends HttpServlet{

    ProdutoDAO prodDAO = new ProdutoDAO();
    ObjectMapper conversor = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

            try {
                Long id = extrairIdUrl(request);

                if (id == null) {
                    ArrayList<Produto> produtos = prodDAO.listarTodos();
                    String jsonProdutos = conversor.writeValueAsString(produtos);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().print(jsonProdutos);
                    return ;
                }

                Produto produto = prodDAO.buscarID(id);
                String jsonProduto= conversor.writeValueAsString(produto);
                response.setContentType("application/json");
                response.getWriter().print(jsonProduto);

                
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                String erroJson = "Falha ao processar a requisição de produtos. detalhe: " + e.getMessage();
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

            Produto dadosProduto = conversor.readValue(jsonString, Produto.class); // passa Json para o Model
            ArrayList<String> erros = new ArrayList<>();

            if (dadosProduto.nome() == null || dadosProduto.nome().isBlank()) {
                erros.add("nome esta vazio");
            }

            if (dadosProduto.tipo() == null || dadosProduto.tipo().isBlank()) {
                erros.add("tipo esta vazio");
            }

            if (dadosProduto.variacao() == null || dadosProduto.variacao().isBlank()) {
                erros.add("variacao esta vazio");
            }

            if (dadosProduto.tamanho() == null || dadosProduto.tamanho().isBlank()) {
                erros.add("tamanho esta vazio");
            }

            if (dadosProduto.preco() == null ||dadosProduto.preco().compareTo(BigDecimal.ZERO) <= 0) {
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

            Long id = prodDAO.criarProduto(dadosProduto);
            Produto novoProduto = new Produto(id, dadosProduto);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json"); // Definir o content type
            response.getWriter().print(conversor.writeValueAsString(novoProduto));

        } catch (JsonProcessingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("Erro: O JSON enviado é inválido. Detalhes: " + e.getMessage());
            e.printStackTrace();

        } catch (SQLException e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro: Falha ao salvar o produto no banco de dados.");
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
            response.getWriter().print("{\"erro\":\"ID de produto inválido ou não fornecido na URL.\"}");
            return;
        }

        try { // DAO retorna o produto que foi alterado

            Produto produtoExistente = prodDAO.buscarID(id);
            if (produtoExistente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("{\"erro\":\" ID inexistente.\"}");
                return;
            }

            String jsonString = request.
                    getReader().
                    lines().
                    collect(Collectors.joining(System.lineSeparator()));

            Produto dadosProduto = conversor.readValue(jsonString, Produto.class);

            Produto produtoAtualizado = new Produto( id, dadosProduto); //Passa o id da URL para saber qual produto

            prodDAO.atualizarProduto(produtoAtualizado);

            Produto produtoFinal = prodDAO.buscarID(id);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(conversor.writeValueAsString(produtoFinal));

        } catch (JsonProcessingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("Erro: O JSON enviado é inválido. Detalhes: " + e.getMessage());
            e.printStackTrace();

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro: Falha ao salvar o produto no banco de dados.");
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
            response.getWriter().print("{\"erro\":\"ID de produto inválido ou não fornecido na URL.\"}");
            return;
        }

        try { // DAO retorna o produto que foi alterado

            Produto produtoExistente = prodDAO.buscarID(id);
            if (produtoExistente == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("{\"erro\":\" ID inexistente.\"}");
                return;
            }

            prodDAO.deletarProduto(produtoExistente);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);


        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro: Falha ao salvar o produto no banco de dados.");
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
