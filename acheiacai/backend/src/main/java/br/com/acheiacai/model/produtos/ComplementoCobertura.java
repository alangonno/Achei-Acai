package br.com.acheiacai.model.produtos;

import java.math.BigDecimal;

public record ComplementoCobertura(
        Long id,
        String nome,
        BigDecimal preco
) {
    public ComplementoCobertura(Long id, ComplementoCobertura compCober) {
        this(id, compCober.nome, compCober.preco);
    }
}
