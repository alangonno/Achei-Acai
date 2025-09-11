package br.com.acheiacai.model.venda;

import br.com.acheiacai.model.produtos.Produto;

import java.math.BigDecimal;
import java.util.List;

public record VendaItemDetalhado(
        Long id,
        Produto produto,
        int quantidade,
        BigDecimal precoUnitario,
        List<ItemAdicional> complementos,
        List<ItemAdicional> coberturas
) {
}
