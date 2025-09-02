package br.com.acheiacai.model;

import java.math.BigDecimal;

public record ItemAdicional(
            Long id,
            String nome,
            BigDecimal preco,
            int quantidade
    ) {}

