package br.com.acheiacai.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");


        // Passa a requisição e a resposta para o próximo filtro na cadeia (ou para o Servlet, se não houver mais filtros)
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private static HttpServletResponse getHttpServletResponse(HttpServletResponse servletResponse) {

        HttpServletResponse httpResponse = servletResponse;

        // Adiciona o cabeçalho que diz "qualquer origem (*) pode acessar esta API"
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");

        // Adiciona os cabeçalhos que dizem "quais métodos HTTP são permitidos"
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Adiciona os cabeçalhos que dizem "quais outros cabeçalhos podem ser enviados na requisição"
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return httpResponse;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}
