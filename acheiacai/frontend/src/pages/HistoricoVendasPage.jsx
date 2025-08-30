

import React, { useState, useEffect } from 'react';
import { buscarTodasVendas } from '../services/vendaService.js';
import VendaDetalheModal from '../components/ProdutosComponents/VendaDetalheModal.jsx';

function HistoricoVendasPage() {
    const [vendas, setVendas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [vendaSelecionadaId, setVendaSelecionadaId] = useState(null);

    useEffect(() => {
        const carregarVendas = async () => {
            try {
                const dados = await buscarTodasVendas();
                setVendas(dados);
            } catch (err) {
                setError("Falha ao carregar o histórico de vendas.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
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
                                    Ver Detalhes
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