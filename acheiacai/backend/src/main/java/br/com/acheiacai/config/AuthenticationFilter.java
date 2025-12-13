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
    private static final Set<String> URLS_PUBLICAS = Set.of("/login", "/keep-alive");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (isUrlPublica(requestUri)) {
            chain.doFilter(request, response);
            return;
        }

        String header = httpRequest.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Claims claims = JwtUtil.extrairTodasAsClaims(token);

            if (claims != null) {
                httpRequest.setAttribute("nomeUsuario", claims.getSubject());
                httpRequest.setAttribute("funcaoUsuario", claims.get("funcao", String.class));
                chain.doFilter(request, response);
                return;
            }
        }

        System.err.println("!!! Acesso negado para: " + requestUri + ". Token inválido ou em falta.");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.getWriter().print("{\"erro\":\"Acesso não autorizado. Token inválido ou em falta.\"}");

    }

    private boolean isUrlPublica(String requestUri) {
        if (requestUri.isEmpty()) {
            return false;
        }
        return URLS_PUBLICAS.stream().anyMatch(publicPath -> requestUri.endsWith(publicPath));
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}