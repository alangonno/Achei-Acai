package br.com.acheiacai.dao;

import br.com.acheiacai.model.relatorio.ItemRelatorio;
import br.com.acheiacai.model.relatorio.TotalPorPagamento;
import br.com.acheiacai.model.relatorio.VolumeProduto;
import br.com.acheiacai.uteis.FabricaConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioDAO {

    public List<ItemRelatorio> calcularTotalAdicionais(LocalDate dataInicio, LocalDate dataFim, String tipo) throws SQLException {
        List<ItemRelatorio> resultado = new ArrayList<>();
        String tabelaPrincipal = tipo;
        String tabelaLigacao = "venda_item_" + tipo;
        String colunaId = tipo.equals("complementos") ? "complemento_id" : "cobertura_id";

        String sql = "SELECT t.nome, SUM(tl.quantidade) AS quantidade_total " +
                "FROM " + tabelaLigacao + " tl " +
                "JOIN " + tabelaPrincipal + " t ON tl." + colunaId + " = t.id " +
                "JOIN venda_itens vi ON tl.venda_item_id = vi.id " +
                "JOIN vendas v ON vi.venda_id = v.id " +
                "WHERE v.data_venda >= ? AND v.data_venda < ? " +
                "GROUP BY t.nome " +
                "ORDER BY quantidade_total DESC";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, dataInicio.atStartOfDay());
            stmt.setObject(2, dataFim.plusDays(1).atStartOfDay());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultado.add(new ItemRelatorio(rs.getString("nome"), rs.getLong("quantidade_total")));
                }
            }
        }
        return resultado;
    }

    public List<VolumeProduto> calcularVolumePorVariacao(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        Map<String, Double> agregador = new HashMap<>();
        String sql = "SELECT p.tipo, p.variacao, p.tamanho, SUM(vi.quantidade) as quantidade_total " +
                "FROM venda_itens vi " +
                "JOIN produtos p ON vi.produto_id = p.id " +
                "JOIN vendas v ON vi.venda_id = v.id " +
                "WHERE v.data_venda >= ? AND v.data_venda < ? AND (p.tipo = 'ACAI' OR p.tipo = 'SORVETE') " +
                "GROUP BY p.tipo, p.variacao, p.tamanho";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, dataInicio.atStartOfDay());
            stmt.setObject(2, dataFim.plusDays(1).atStartOfDay());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String tipo = rs.getString("tipo");
                    String variacao = rs.getString("variacao");
                    String tamanho = rs.getString("tamanho");
                    long quantidade = rs.getLong("quantidade_total");
                    double litros = converterTamanhoParaLitros(tamanho);
                    String chave = tipo + "::" + variacao;
                    agregador.put(chave, agregador.getOrDefault(chave, 0.0) + (litros * quantidade));
                }
            }
        }

        List<VolumeProduto> resultado = new ArrayList<>();
        for (Map.Entry<String, Double> entry : agregador.entrySet()) {
            String[] partes = entry.getKey().split("::");
            resultado.add(new VolumeProduto(partes[0], partes[1], entry.getValue()));
        }
        return resultado;
    }

    public List<TotalPorPagamento> calcularTotaisPorPagamento(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        List<TotalPorPagamento> resultado = new ArrayList<>();
        String sql = "SELECT forma_pagamento, SUM(valor_total) as total_faturado " +
                    "FROM vendas " +
                    "WHERE data_venda >= ? AND data_venda < ? " +
                    "GROUP BY forma_pagamento " +
                    "ORDER BY total_faturado DESC";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, dataInicio.atStartOfDay());
            stmt.setObject(2, dataFim.plusDays(1).atStartOfDay());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultado.add(new TotalPorPagamento(rs.getString("forma_pagamento"), rs.getBigDecimal("total_faturado")));
                }
            }
        }
        return resultado;
    }

    private double converterTamanhoParaLitros(String tamanho) {
        if (tamanho == null || tamanho.isBlank()) return 0.0;
        String lowerTamanho = tamanho.toLowerCase().trim();
        try {
            if (lowerTamanho.endsWith("ml")) {
                return Double.parseDouble(lowerTamanho.replace("ml", "")) / 1000.0;
            }
            if (lowerTamanho.endsWith("l")) {
                return Double.parseDouble(lowerTamanho.replace("l", ""));
            }
        } catch (NumberFormatException e) {
            return 0.0;
        }
        return 0.0;
    }
}
