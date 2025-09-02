

import React, { useState, useEffect } from 'react';
import { buscarTodasVendas, deletarVendaPorId } from '../services/vendaService.js';
import VendaDetalheModal from '../components/VendasComponents/VendaDetalheModal.jsx';

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
        <div className="historico-vendas-pagina">
            <h1>Histórico de Vendas</h1>
            <table className="data-table">
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
                            <td>{venda.id}</td>
                            <td>{new Date(venda.dataVenda).toLocaleString('pt-BR')}</td>
                            <td>{venda.formaPagamento}</td>
                            <td>{venda.valorTotal.toFixed(2)}</td>
                            <td>
                                <button className="botao-detalhes" onClick={() => setVendaSelecionadaId(venda.id)}>
                                    Detalhes
                                </button>
                                <button className="delete-button" onClick={() =>
                                handleExcluir(venda.id)}> 
                                    Deletar 
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            
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