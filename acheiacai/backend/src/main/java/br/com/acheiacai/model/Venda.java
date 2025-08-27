package br.com.acheiacai.model;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Date;
import java.util.List;

public record Venda(
        Long id,
        Date dataVenda,
        BigDecimal valorTotal,
        String formaPagamento,
        List<VendaItem> itens

) { public Venda(Long id, Date dataVenda, Venda venda) {
    this(id,
        dataVenda,
        venda.valorTotal,
        venda.formaPagamento,
        venda.itens);
}
}
