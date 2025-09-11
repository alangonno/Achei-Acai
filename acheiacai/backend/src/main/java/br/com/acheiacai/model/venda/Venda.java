package br.com.acheiacai.model.venda;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public record Venda(
        Long id,
        Date dataVenda,
        BigDecimal valorTotal,
        String formaPagamento,
        BigDecimal desconto,   // <-- NOVO
        BigDecimal acrescimo,
        List<VendaItem> itens

) { public Venda(Long id, Date dataVenda, Venda venda) {
    this(id,
        dataVenda,
        venda.valorTotal,
        venda.formaPagamento,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        venda.itens);
    }

    public Venda(Long id, Date dataVenda, Venda venda, BigDecimal desconto, BigDecimal acrescimo) {
        this(id,
            dataVenda,
            venda.valorTotal,
            venda.formaPagamento,
            desconto,
            acrescimo,
            venda.itens);
    }


}
