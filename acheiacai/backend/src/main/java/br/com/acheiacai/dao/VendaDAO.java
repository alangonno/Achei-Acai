package br.com.acheiacai.dao;

import br.com.acheiacai.model.produtos.ComplementoCobertura;
import br.com.acheiacai.model.produtos.Produto;
import br.com.acheiacai.model.venda.*;
import br.com.acheiacai.uteis.FabricaConexao;

import java.math.BigDecimal;
import java.sql.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class VendaDAO {

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
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        null
                ));
            }
        }
        return vendas;
    }

    public Venda salvar(Venda venda) throws SQLException, Exception{
        String sqlVenda = "INSERT INTO vendas(data_venda, valor_total, forma_pagamento, desconto, acrescimo) values (?, ?, ?::forma_de_pagamento, ?, ?)";

        String sqlVendaItem = "INSERT INTO venda_itens(venda_id, produto_id, quantidade, preco_unitario_da_venda) " +
                "values (?, ?, ?, ?)";

        String sqlVendaComplemento = "INSERT INTO venda_item_complementos(venda_item_id, complemento_id, quantidade) " +
                "values (?, ?, ?)";

        String sqlVendaCobertura = "INSERT INTO venda_item_coberturas(venda_item_id, cobertura_id, quantidade) " +
                "values (?, ?, ?)";

        Long idVenda;
        Connection conexao = null;
        Date dataDaTransacao;
        BigDecimal valorTotalCalculado;

        try {

            conexao = FabricaConexao.getConexao();
            conexao.setAutoCommit(false);

            BigDecimal subtotalItens = BigDecimal.ZERO;

            ProdutoDAO produtoDAO = new ProdutoDAO();
            ComplementoCoberturaDAO adicionalDAO = new ComplementoCoberturaDAO();

            for (VendaItem item : venda.itens()) {

                Produto produtoDB = produtoDAO.buscarID(item.produtoId());
                if (produtoDB == null) throw new SQLException("Produto com ID " + item.produtoId() + " não encontrado.");

                BigDecimal precoItem = produtoDB.preco().multiply(new BigDecimal(item.quantidade()));

                BigDecimal precoAdicionais = BigDecimal.ZERO;
                for (ItemAdicional comp : item.complementos()) {
                    ComplementoCobertura compDB = adicionalDAO.buscarID(comp.id(), "complementos");
                    precoAdicionais = precoAdicionais.add(compDB.preco().multiply(new BigDecimal(comp.quantidade())));
                }

                for (ItemAdicional cob : item.coberturas()) {
                    ComplementoCobertura combDB = adicionalDAO.buscarID(cob.id(), "coberturas");
                    precoAdicionais = precoAdicionais.add(combDB.preco().multiply(new BigDecimal(cob.quantidade())));
                }

                subtotalItens = subtotalItens.add(precoItem).add(precoAdicionais);
            }

            valorTotalCalculado = subtotalItens.add(venda.acrescimo()).subtract(venda.desconto());

            if (venda.valorTotal().compareTo(valorTotalCalculado) != 0) {
                System.err.println("Aviso: O valor total enviado pelo front-end (" + venda.valorTotal() + ") é diferente do calculado no back-end (" + valorTotalCalculado + "). Usando o valor do back-end.");
            }

            try (PreparedStatement stmtVenda = conexao.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) { //cria Venda
                dataDaTransacao = new Date();
                Timestamp timestampFromDate = new Timestamp(dataDaTransacao.getTime());

                stmtVenda.setTimestamp(1, timestampFromDate);
                stmtVenda.setBigDecimal(2, valorTotalCalculado);
                stmtVenda.setString(3, venda.formaPagamento());
                stmtVenda.setBigDecimal(5, venda.desconto());
                stmtVenda.setBigDecimal(4, venda.acrescimo());
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

                    for (ItemAdicional complemento : item.complementos()) {//cria vendaItemComplementos
                        stmtVendaComplemento.setLong(1, idVendaItem);
                        stmtVendaComplemento.setLong(2, complemento.id());
                        stmtVendaComplemento.setInt(3, complemento.quantidade());
                        stmtVendaComplemento.executeUpdate();
                    }
                    for (ItemAdicional cobertura : item.coberturas()) {//cria vendaItemCoberturas
                        stmtVendaCobertura.setLong(1, idVendaItem);
                        stmtVendaCobertura.setLong(2, cobertura.id());
                        stmtVendaCobertura.setInt(3, cobertura.quantidade());
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

            return new Venda(idVenda, dataDaTransacao, venda, venda.desconto(), venda.acrescimo());
        }

    public void deletar(Long vendaId) throws SQLException, IllegalArgumentException, Exception{
        String sql = "DELETE FROM vendas WHERE id = ?";

        if(buscarVendaBasePorId(vendaId) == null) {
            throw new IllegalArgumentException();
        }

        try (Connection conexao = FabricaConexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, vendaId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }

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
            List<ItemAdicional> complementos = buscarAdicionaisPorItemId(itemBase.id(), "complementos");
            List<ItemAdicional> coberturas = buscarAdicionaisPorItemId(itemBase.id(), "coberturas");

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
                            resultado.getObject("data_venda", Date.class),
                            resultado.getBigDecimal("valor_total"),
                            resultado.getString("forma_pagamento"),
                            resultado.getBigDecimal("desconto"),
                            resultado.getBigDecimal("acrescimo"),
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
        String sql = "SELECT * FROM venda_itens WHERE venda_id = ?";
        List<VendaItem> itens = new ArrayList<>();

        try(Connection conexao = FabricaConexao.getConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, vendaId);

            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                Long idItem = resultado.getLong("id");

                List<ItemAdicional> complementos = buscarAdicionaisPorItemId(idItem, "complementos");
                List<ItemAdicional> coberturas = buscarAdicionaisPorItemId(idItem, "coberturas");

                itens.add(new VendaItem(
                        idItem,
                        resultado.getLong("produto_id"),
                        resultado.getInt("quantidade"),
                        resultado.getBigDecimal("preco_unitario_da_venda"),
                        complementos,
                        coberturas
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

    private List<ItemAdicional> buscarAdicionaisPorItemId(Long itemId, String tipo) throws SQLException, Exception {
        ComplementoCoberturaDAO dao = new ComplementoCoberturaDAO();
        List<ItemAdicional> lista = new ArrayList<>();
        String tabelaLigacao = tipo.equals("complementos") ? "venda_item_complementos" : "venda_item_coberturas";
        String colunaId = tipo.equals("complementos") ? "complemento_id" : "cobertura_id";

        String sql = "SELECT "+ colunaId +", quantidade FROM "+ tabelaLigacao +" WHERE venda_item_id = ?";

        try (Connection conexao = FabricaConexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, itemId);
            try (ResultSet resultado = stmt.executeQuery()) {
                while (resultado.next()) {
                    Long adicionalId = resultado.getLong(colunaId);
                    ComplementoCobertura compCober= dao.buscarID(adicionalId, tipo);
                    lista.add(new ItemAdicional(compCober.id(), compCober.nome(), compCober.preco(),  resultado.getInt("quantidade")));
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
