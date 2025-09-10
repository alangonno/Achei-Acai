package br.com.acheiacai.model.relatorio;

import java.math.BigDecimal;

public record TotalPorPagamento(
        String formaPagamento,
        BigDecimal total
) {}
