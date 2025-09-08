package br.com.acheiacai.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@WebFilter
public class CorsFilter implements Filter {

    private final Set<String> allowedOrigins = new HashSet<>();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String requestOrigin = httpRequest.getHeader("Origin");

        if (requestOrigin != null && allowedOrigins.contains(requestOrigin)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", requestOrigin);
        }

        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String allowedOriginsEnv = System.getenv("ALLOWED_ORIGINS");

        if (allowedOriginsEnv == null || allowedOriginsEnv.isBlank()) {
            allowedOrigins.add("http://localhost:5173");
        } else {
            allowedOrigins.addAll(Arrays.asList(allowedOriginsEnv.split(",")));
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}
