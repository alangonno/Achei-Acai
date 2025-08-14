package br.com.acheiacai.controller;

import br.com.acheiacai.dao.ProdutoDAO;
import br.com.acheiacai.model.Produto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/produtos")
public class ProdutoServlet extends HttpServlet{
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

            try {
                ProdutoDAO prodDAO = new ProdutoDAO();
                List<Produto> produtos = prodDAO.listarTodos();
                ObjectMapper conversor = new ObjectMapper();

                String jsonProdutos = conversor.writeValueAsString(produtos);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(jsonProdutos);
                response.getWriter().flush();
                
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                String erroJson = "Falha ao processar a requisição de produtos. detalhe: " + e.getMessage();
                response.getWriter().print(erroJson);

                e.printStackTrace();
            }

        }
}
