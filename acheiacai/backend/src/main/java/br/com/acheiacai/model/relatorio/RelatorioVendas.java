package br.com.acheiacai.model.relatorio;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioVendas (
        List<ItemRelatorio> totalComplementos,
        List<ItemRelatorio> totalCoberturas,
        List<VolumeProduto> litrosProduto,
        VolumeTotal volumeTotal,
        List<ItemRelatorio> totalOutrosProdutos,
        List<TotalPorPagamento> totalPorPagamentos,
        BigDecimal totalGeralVendas
) {
}
