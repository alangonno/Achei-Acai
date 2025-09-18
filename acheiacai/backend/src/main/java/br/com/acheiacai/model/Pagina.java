package br.com.acheiacai.model;

import java.util.List;

public record Pagina<T>(
        List<T> content,
        int currentPage,
        long totalPages,
        long totalElements
) {
}
