package br.com.acheiacai.dao;

import br.com.acheiacai.model.*;
import br.com.acheiacai.uteis.FabricaConexao;

import java.math.BigDecimal;
import java.sql.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class VendaDAO {

    public Venda salvar(Venda venda) throws SQLException{
        String sqlVenda = "INSERT INTO vendas(data_venda, valor_total, forma_pagamento) values (?, ?, ?)";

        String sqlVendaItem = "INSERT INTO venda_itens(venda_id, produto_id, quantidade, preco_unitario_da_venda) " +
                "values (?, ?, ?, ?)";

        String sqlVendaComplemento = "INSERT INTO venda_item_complementos(venda_item_id, complemento_id) " +
                "values (?, ?)";

        String sqlVendaCobertura = "INSERT INTO venda_item_coberturas(venda_item_id, cobertura_id) " +
                "values (?, ?)";

        Long idVenda = null;
        Connection conexao = null;
        Date dataDaTransacao = null;

        try {

            conexao = FabricaConexao.getConexao();
            conexao.setAutoCommit(false);


            try (PreparedStatement stmtVenda = conexao.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) { //cria Venda
                dataDaTransacao = new Date();

                stmtVenda.setObject(1, dataDaTransacao);
                stmtVenda.setBigDecimal(2, venda.valorTotal());
                stmtVenda.setString(3, venda.formaPagamento());
                stmtVenda.executeUpdate();

                try (ResultSet resultado = stmtVenda.getGeneratedKeys()) {
                    if (resultado.next()) {
                        idVenda = resultado.getLong(1);
                    } else {
                        throw new SQLException("Falha ao obter o ID da venda, nenhuma linha inserida.");
                    }
                }
            }

            try (PreparedStatement stmtVendaItem = conexao.prepareStatement(sqlVendaItem, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement stmtVendaComplemento = conexao.prepareStatement(sqlVendaComplemento);
                 PreparedStatement stmtVendaCobertura = conexao.prepareStatement(sqlVendaCobertura)) {

                for (VendaItem item : venda.itens()) {//cria VendaItem

                    stmtVendaItem.setLong(1, idVenda);
                    stmtVendaItem.setLong(2, item.produtoId());
                    stmtVendaItem.setInt(3, item.quantidade());
                    stmtVendaItem.setBigDecimal(4, item.precoUnitario());
                    stmtVendaItem.executeUpdate();


                    Long idVendaItem;
                    try(ResultSet resultado = stmtVendaItem.getGeneratedKeys()) {
                        if (resultado.next()) {
                            idVendaItem = resultado.getLong(1);
                        } else {
                            throw new SQLException("Falha ao obter o ID da venda, nenhuma linha inserida.");
                        }
                    }

                    for (Long idComplemento : item.complementosIds()) {//cria vendaItemComplementos
                        stmtVendaComplemento.setLong(1, idVendaItem);
                        stmtVendaComplemento.setLong(2, idComplemento);
                        stmtVendaComplemento.executeUpdate();
                    }
                    for (Long idCobertura : item.coberturasIds()) {//cria vendaItemCoberturas
                        stmtVendaCobertura.setLong(1, idVendaItem);
                        stmtVendaCobertura.setLong(2, idCobertura);
                        stmtVendaCobertura.executeUpdate();
                    }

                }
            }
                conexao.commit();

            } catch (SQLException e) {

            if (conexao != null) {
                System.err.println("Transação será desfeita!");
                conexao.rollback();
            }
            throw new SQLException("Erro ao salvar a venda. A transação foi desfeita.", e);
            } finally {
                if (conexao != null) {
                    conexao.close();
                }
            }

            return new Venda(idVenda, dataDaTransacao, venda);
        }

    public VendaDetalhada buscarPorIdVendaDetalhado(Long vendaId) throws Exception {
        ProdutoDAO produtoDAO = new ProdutoDAO();
        ComplementoCoberturaDAO compCobDAO = new ComplementoCoberturaDAO();

        // 1. Buscar a Venda base
        Venda vendaBase = buscarVendaBasePorId(vendaId);
        if (vendaBase == null) {
            return null; // Venda não encontrada
        }

        List<VendaItemDetalhado> itensDetalhados = new ArrayList<>();
        List<VendaItem> itensBase = buscarItensBasePorVendaId(vendaId);

        // 2. Para cada item, buscar os detalhes completos
        for (VendaItem itemBase : itensBase) {
            Produto produto = produtoDAO.buscarID(itemBase.produtoId());
            List<ComplementoCobertura> complementos = buscarAdicionaisPorItemId(itemBase.id(), "complementos");
            List<ComplementoCobertura> coberturas = buscarAdicionaisPorItemId(itemBase.id(), "coberturas");

            itensDetalhados.add(new VendaItemDetalhado(
                    itemBase.id(),
                    produto,
                    itemBase.quantidade(),
                    itemBase.precoUnitario(),
                    complementos,
                    coberturas
            ));
        }


        return new VendaDetalhada(
                vendaBase.id(),
                vendaBase.dataVenda(),
                vendaBase.valorTotal(),
                vendaBase.formaPagamento(),
                itensDetalhados
        );
    }

    public List<Venda> listarTodasAsVendasBase() throws SQLException {
        List<Venda> vendas = new ArrayList<>();
        String sql = "SELECT * FROM vendas ORDER BY data_venda DESC";

        try (Connection conexao = FabricaConexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                vendas.add(new Venda(
                        resultado.getLong("id"),
                        resultado.getObject("data_venda", Date.class),
                        resultado.getBigDecimal("valor_total"),
                        resultado.getString("forma_pagamento"),
                        null
                ));
            }
        }
        return vendas;
    }

    private Venda buscarVendaBasePorId(Long vendaId) throws SQLException, Exception {
        String sql = "SELECT * FROM vendas WHERE id = ?";

        try (Connection conexao = FabricaConexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, vendaId);
            try(ResultSet resultado = stmt.executeQuery()) {

                while (resultado.next()) {
                    Long id = resultado.getLong("id");
                    return new Venda(
                            id,
                            resultado.getDate("data_venda"),
                            resultado.getBigDecimal("valor_total"),
                            resultado.getString("forma_pagamento"),
                            buscarItensBasePorVendaId(id)
                    );
                }
            }catch (SQLException e) {
                System.err.println("Erro ao buscar VendaBase!");
                e.printStackTrace();

            } catch (Exception e) {
                throw e;
            }
        }
        return null;
    }

    private List<VendaItem> buscarItensBasePorVendaId(Long vendaId) throws SQLException, Exception {
        String sql = "SELECT * FROM venda_itens WHERE id = ?";
        List<VendaItem> itens = new ArrayList<>();

        try(Connection conexao = FabricaConexao.getConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, vendaId);

            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                Long idItem = resultado.getLong("id");

                List<ComplementoCobertura> complementos = buscarAdicionaisPorItemId(idItem, "complementos");
                List<Long> complementosIds = new ArrayList<>();
                complementos.stream().forEach(c -> complementosIds.add(c.id()));

                List<ComplementoCobertura> coberturas = buscarAdicionaisPorItemId(idItem, "complementos");
                List<Long> coberturasIds = new ArrayList<>();
                complementos.stream().forEach(c -> coberturasIds.add(c.id()));

                itens.add(new VendaItem(
                        idItem,
                        resultado.getLong("produto_id"),
                        resultado.getInt("quantidade"),
                        resultado.getBigDecimal("preco_unitario_da_venda"),
                        complementosIds,
                        coberturasIds
                        )
                );
            }

            return itens;
        }catch (SQLException e) {
            System.err.println("Erro ao buscar ItensBase!");
            e.printStackTrace();

        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    private List<ComplementoCobertura> buscarAdicionaisPorItemId(Long itemId, String tipo) throws SQLException, Exception {
        ComplementoCoberturaDAO dao = new ComplementoCoberturaDAO();
        List<ComplementoCobertura> lista = new ArrayList<>();
        String tabelaLigacao = tipo.equals("complementos") ? "venda_item_complementos" : "venda_item_coberturas";
        String colunaId = tipo.equals("complementos") ? "complemento_id" : "cobertura_id";

        String sql = "SELECT "+ colunaId +" FROM "+ tabelaLigacao +" WHERE venda_item_id = ?";

        try (Connection conexao = FabricaConexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, itemId);
            try (ResultSet resultado = stmt.executeQuery()) {
                while (resultado.next()) {
                    Long adicionalId = resultado.getLong(colunaId);
                    lista.add(dao.buscarID(adicionalId, tipo));
                }

            }catch (SQLException e) {
                System.err.println("Erro ao buscar Adicionais!");
                e.printStackTrace();

            } catch (Exception e) {
                throw e;
            }
        }
        return lista;
    }
}
