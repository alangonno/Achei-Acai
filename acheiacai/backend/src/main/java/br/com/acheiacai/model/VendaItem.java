package br.com.acheiacai.model;

import java.math.BigDecimal;
import java.util.List;

public record VendaItem(
        Long id,
        Long produtoId,
        int quantidade,
        BigDecimal precoUnitario,
        List<Long> complementosIds,
        List<Long> coberturasIds
) {
}
