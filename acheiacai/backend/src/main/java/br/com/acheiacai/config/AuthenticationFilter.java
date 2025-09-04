package br.com.acheiacai.config;

import br.com.acheiacai.uteis.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    // A nossa "lista de convidados": URLs que não precisam de autenticação.
    private static final Set<String> URLS_PUBLICAS = Set.of("/login");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = httpRequest.getServletPath();

        // 1. Verifica se o URL está na lista de acesso público
        if (isUrlPublica(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Para URLs protegidos, extrai o token do cabeçalho
        String header = httpRequest.getHeader("Authorization");

        // Verifica se o cabeçalho existe e está no formato "Bearer <token>"
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            Claims claims = JwtUtil.extrairTodasAsClaims(token);

            if (claims != null) {
                // Se as claims são válidas, anexamos o nome de utilizador e a função à requisição
                httpRequest.setAttribute("nomeUsuario", claims.getSubject());
                httpRequest.setAttribute("funcaoUsuario", claims.get("funcao", String.class));

                chain.doFilter(request, response);
                return;
            }
        }

        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.getWriter().print("{\"erro\":\"Acesso não autorizado. Token inválido ou em falta.\"}");
    }

    private boolean isUrlPublica(String path) {
        return URLS_PUBLICAS.stream().anyMatch(path::startsWith);
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}