
import React, { useState, useEffect } from 'react';
import { buscarVendaPorId } from '../../services/vendaService';


function VendaDetalheModal({ vendaId, onClose }) {
    const [venda, setVenda] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        // Se não houver ID, não faz nada.
        if (!vendaId) return;

        const carregarDetalhes = async () => {
            try {
                setLoading(true);
                const dados = await buscarVendaPorId(vendaId);
                setVenda(dados);
            } catch (err) {
                setError("Falha ao carregar os detalhes da venda.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        carregarDetalhes();
    }, [vendaId]); // Este efeito roda sempre que o 'vendaId' mudar

    return (
        // O overlay que escurece o fundo
        <div className="modal-overlay" onClick={onClose}>
            {/* O conteúdo do modal */}
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close-button" onClick={onClose}>&times;</button>
                
                {loading && <p>A carregar detalhes...</p>}
                {error && <p>Erro: {error}</p>}
                
                {venda && (
                    <>
                        <h2>Detalhes da Venda #{venda.id}</h2>
                        <div className="venda-info">
                            <p><strong>Data:</strong> {new Date(venda.dataVenda).toLocaleString('pt-BR')}</p>
                            <p><strong>Pagamento:</strong> {venda.formaPagamento}</p>
                            <p><strong>Total: R$ {venda.valorTotal.toFixed(2)}</strong></p>
                        </div>
                        
                        <h3>Itens Comprados:</h3>
                        <ul className="lista-itens-detalhe">
                            {venda.itens.map(item => (
                                <li key={item.id} className="item-detalhe">
                                    <div className="item-produto-info">
                                        <strong>{item.quantidade}x {item.produto.nome} ({item.produto.variacao})</strong>
                                        <span>R$ {item.precoUnitarioNaVenda.toFixed(2)}</span>
                                    </div>
                                    
                                    {item.complementos && item.complementos.length > 0 && (
                                        <ul className="lista-adicionais-detalhe">
                                            <span>Complementos:</span>
                                            {item.complementos.map(c => <li key={c.id}>- {c.nome}</li>)}
                                        </ul>
                                    )}
                                    {item.coberturas && item.coberturas.length > 0 && (
                                         <ul className="lista-adicionais-detalhe">
                                            <span>Coberturas:</span>
                                            {item.coberturas.map(c => <li key={c.id}>- {c.nome}</li>)}
                                        </ul>
                                    )}
                                </li>
                            ))}
                        </ul>
                    </>
                )}
            </div>
        </div>
    );
}

export default VendaDetalheModal;