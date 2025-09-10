package br.com.acheiacai.model.produtos;

import java.math.BigDecimal;

public record Produto  (
    Long id,
    String nome,
    String tipo, 
    String variacao, 
    String tamanho, 
    BigDecimal preco
) {

    public Produto(Long id, Produto produto) {
        this(id,
        produto.nome,
        produto.tipo,
        produto.variacao,
        produto.tamanho,
        produto.preco);

    }
}
