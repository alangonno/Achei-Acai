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

        System.out.println(">>> CORS Filter: Interceptando uma requisição...");

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = getHttpServletResponse((HttpServletResponse) servletResponse);

        // O navegador envia uma requisição "OPTIONS" antes de um PUT ou DELETE para verificar as permissões.
        // Se for um OPTIONS, nós apenas retornamos a resposta com os cabeçalhos acima e status OK.
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
        }

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
        System.out.println("****************************************");
        System.out.println("********** CORS FILTER INICIADO **********");
        System.out.println("****************************************");
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}
