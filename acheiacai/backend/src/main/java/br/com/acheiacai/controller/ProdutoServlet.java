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

@WebServlet("/produtos")
public class ProdutoServlet extends HttpServlet{

    ProdutoDAO prodDAO = new ProdutoDAO();
    ObjectMapper conversor = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

            try {
                ArrayList<Produto> produtos = prodDAO.listarTodos();
                String jsonProdutos = conversor.writeValueAsString(produtos);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(jsonProdutos);
                
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

            Produto novoProduto = conversor.readValue(jsonString, Produto.class);

            ArrayList<String> erros = new ArrayList<>();

            if (novoProduto.nome() == null || novoProduto.nome().isBlank()) {
                erros.add("nome esta vazio");
            }

            if (novoProduto.tipo() == null || novoProduto.tipo().isBlank()) {
                erros.add("tipo esta vazio");
            }

            if (novoProduto.variacao() == null || novoProduto.variacao().isBlank()) {
                erros.add("variacao esta vazio");
            }

            if (novoProduto.tamanho() == null || novoProduto.tamanho().isBlank()) {
                erros.add("tamanho esta vazio");
            }

            if (novoProduto.preco() == null || novoProduto.preco().compareTo(BigDecimal.ZERO) <= 0) {
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

            prodDAO.criarProduto(novoProduto);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("Produto criado com sucesso!");


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

    protected void doPatch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String jsonString = request.
                getReader().
                lines().
                collect(Collectors.joining(System.lineSeparator()));

        if (jsonString == null || jsonString.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Erro: Corpo da requisição está vazio.");
        }

        Produto produtoAtualizado = conversor.readValue(jsonString, Produto.class);
        try {
            String alteracaoJson = conversor.writeValueAsString(prodDAO.atualizarProduto(produtoAtualizado));
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Produto alterado com sucesso!");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(alteracaoJson);

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
            throws ServletException, IOException { }
}
