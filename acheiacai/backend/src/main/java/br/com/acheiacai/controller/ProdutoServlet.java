package br.com.acheiacai.controller;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
@WebServlet("/produtos")
public class ProdutoServlet extends HttpServlet{
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
            if (request.equalsIgnoreCase("get"))
        }
}
