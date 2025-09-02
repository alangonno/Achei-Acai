

import React, { useState, useEffect } from 'react';
import { buscarTodasVendas, deletarVendaPorId } from '../services/vendaService.js';
import VendaDetalheModal from '../components/VendasComponents/VendaDetalheModal.jsx';

import pageStyles from './HistoricoVendasPage.module.css';
import tableStyles from '../components/ProdutosComponents/Tabela.module.css';

function HistoricoVendasPage() {
    const [vendas, setVendas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [vendaSelecionadaId, setVendaSelecionadaId] = useState(null);

    const handleExcluir = async (itemId) => {
        if (window.confirm(`Tem a certeza de que deseja excluir a venda com ID ${itemId}?`)) {
            try {
                await deletarVendaPorId(itemId);
                carregarVendas();
            } catch (err) {
                setError(`Falha ao excluir o item: ${err.message}`);
            }
        }
    };
    
    const carregarVendas = async () => {
        try {
            setLoading(true);
            setError(null);
            const dados = await buscarTodasVendas();
            setVendas(dados);
        } catch (err) {
            setError("Falha ao carregar o histórico de vendas.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };


    useEffect(() => {
        carregarVendas();
    }, []); 

    if (loading) return <p>A carregar histórico...</p>;
    if (error) return <p>Erro: {error}</p>;

    return (
        <div className={pageStyles.historicoVendasPagina}>
            <h1>Histórico de Vendas</h1>
            <div className={tableStyles.tableWrapper}>
                <table className={tableStyles.dataTable}>
                    <thead>
                        <tr>
                            <th>ID da Venda</th>
                            <th>Data e Hora</th>
                            <th>Forma de Pagamento</th>
                            <th>Valor Total (R$)</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        {vendas.map(venda => (
                            <tr key={venda.id}>
                                <td data-label="ID da Venda">{venda.id}</td>
                                <td data-label="Data e Hora">{new Date(venda.dataVenda).toLocaleString('pt-BR')}</td>
                                <td data-label="Forma de Pagamento">{venda.formaPagamento}</td>
                                <td data-label="Valor Total (R$)">{venda.valorTotal.toFixed(2)}</td>
                                <td data-label="Ações" className={tableStyles.actions}>
                                     <button className={tableStyles.detailsButton} onClick={() => setVendaSelecionadaId(venda.id)}>
                                        Detalhes
                                    </button>
                                    <button className={tableStyles.deleteButton} onClick={() => handleExcluir(venda.id)}> 
                                        Deletar 
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            
            {vendaSelecionadaId && (
                <VendaDetalheModal 
                    vendaId={vendaSelecionadaId} 
                    onClose={() => setVendaSelecionadaId(null)} 
                />
            )}
        </div>
    );
}

export default HistoricoVendasPage;