package br.com.acheiacai.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public record VendaDetalhada(
        Long id,
        Date dataVenda,
        BigDecimal valorTotal,
        String formaPagamento,
        List<VendaItemDetalhado> itens
) {
}
