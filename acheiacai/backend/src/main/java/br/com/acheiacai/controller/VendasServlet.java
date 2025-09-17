package br.com.acheiacai.controller;

import br.com.acheiacai.dao.VendaDAO;
import br.com.acheiacai.model.Pagina;
import br.com.acheiacai.model.venda.Venda;
import br.com.acheiacai.model.venda.VendaDetalhada;
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
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/vendas/*")
public class VendasServlet extends HttpServlet {

    VendaDAO vendaDAO = new VendaDAO();
    ObjectMapper conversor = new ObjectMapper();

    protected void doGet (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long id = extrairIdUrl(request);

            if (id == null) {

                int tamanhoPagina = 10;
                int pagina = 0;

                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isBlank()) {
                    pagina = Integer.parseInt(pageParam);
                }

                String sizeParam = request.getParameter("size");
                if (sizeParam != null && !sizeParam.isBlank()) {
                    tamanhoPagina = Integer.parseInt(sizeParam);
                }

                if(pagina == 0){
                    pagina = vendaDAO.ultimaPagina(tamanhoPagina);
                }

                Pagina<Venda> vendasPaginado = vendaDAO.listarTodosPaginado(pagina, tamanhoPagina);
                String jsonPaginaVenda = conversor.writeValueAsString(vendasPaginado);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(jsonPaginaVenda);
                return;
            }

            VendaDetalhada vendaDetalhada = vendaDAO.buscarPorIdVendaDetalhado(id);
            String jsonVendaDetalhada = conversor.writeValueAsString(vendaDetalhada);
            response.setContentType("application/json");

            if (vendaDetalhada != null) {
                response.getWriter().print(jsonVendaDetalhada);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("{\"erro\":\"Venda não encontrada.\"}");
            }

        } catch(NumberFormatException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"erro\":\"Parâmetros de página ou tamanho inválidos.\"}");

        } catch(Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"erro\":\"Falha ao processar a requisição de vendas.\"}");
            e.printStackTrace();
        }

    }

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

    protected void doDelete (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long vendaId = extrairIdUrl(request);
        if (vendaId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"erro\":\"ID de venda inválido ou não fornecido na URL.\"}");
            return;
        }

        try {

            vendaDAO.deletar(vendaId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);


        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().print(e.getMessage());

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(e.getMessage());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(e.getMessage());
        }

    }

    private Long extrairIdUrl(HttpServletRequest request) throws NumberFormatException {

        String id_url = request.getPathInfo();

        if (id_url == null || id_url.equals("/") ) {
            return null;
        }

        try {
            id_url = id_url.substring(1);
            return Long.parseLong(id_url);

        } catch (NumberFormatException e) {
            return null;
        }

    }

    private List<Integer> extrairPaginasUrl(HttpServletRequest request) {
        List<Integer> paginas = new ArrayList<>();

        try {
            paginas.add(Integer.valueOf(request.getParameter("page_size")));
            paginas.add(Integer.valueOf(request.getParameter("page")));
            return paginas;
        } catch (Exception e) {
            return null;
        }

    }

}
