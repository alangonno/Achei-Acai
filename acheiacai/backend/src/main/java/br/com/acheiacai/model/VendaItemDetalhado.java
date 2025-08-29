package br.com.acheiacai.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public record VendaItemDetalhado(
        Long id,
        Produto produto,
        int quantidade,
        BigDecimal precoUnitario,
        List<ComplementoCobertura> complementos,
        List<ComplementoCobertura> coberturas
) {
}
