package br.com.acheiacai.model.venda;

import java.math.BigDecimal;

public record ItemAdicional(
            Long id,
            String nome,
            BigDecimal preco,
            int quantidade
    ) {}

