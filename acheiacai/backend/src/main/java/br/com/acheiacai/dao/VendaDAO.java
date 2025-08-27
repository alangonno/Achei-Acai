package br.com.acheiacai.dao;

import br.com.acheiacai.model.Venda;
import br.com.acheiacai.model.VendaItem;
import br.com.acheiacai.uteis.FabricaConexao;

import java.sql.*;

import java.util.Date;

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
}
