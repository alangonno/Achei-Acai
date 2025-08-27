package br.com.acheiacai.controller;

import br.com.acheiacai.dao.VendaDAO;
import br.com.acheiacai.model.Produto;
import br.com.acheiacai.model.Venda;
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

@WebServlet("/vendas/*")
public class VendasServlet extends HttpServlet {

    VendaDAO vendaDAO = new VendaDAO();
    ObjectMapper conversor = new ObjectMapper();

    @Override
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

            Venda dadosVenda = conversor.readValue(jsonString, Venda.class);
            Venda novaVenda = vendaDAO.salvar(dadosVenda);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.getWriter().print(conversor.writeValueAsString(novaVenda));



        } catch (JsonProcessingException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("Erro: O JSON enviado é inválido. Detalhes: " + e.getMessage());
            e.printStackTrace();

        }catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro: Falha ao salvar o produto no banco de dados.");
            e.printStackTrace();

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro:" + e.getMessage());
            e.printStackTrace();
        }

        }



}
