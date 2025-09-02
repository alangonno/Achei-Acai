
import React, { useState, useEffect } from 'react';
import { buscarVendaPorId } from '../../services/vendaService';

import styles from './VendaDetalheModal.module.css';

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
    }, [vendaId]);

    return (
        <div className={styles.modalOverlay} onClick={onClose}>
            <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
                <button className={styles.modalCloseButton} onClick={onClose}>&times;</button>
                
                {loading && <p>A carregar detalhes...</p>}
                {error && <p>Erro: {error}</p>}
                
                {venda && (
                    <>
                        <h2>Detalhes da Venda #{venda.id}</h2>
                        <div className={styles.vendaInfo}>  
                            <p><strong>Data e Hora:</strong> {new Date(venda.dataVenda).toLocaleString('pt-BR')}</p>
                            <p><strong>Pagamento:</strong> {venda.formaPagamento}</p>
                            <p><strong>Total: R$ {venda.valorTotal.toFixed(2)}</strong></p>
                        </div>
                        
                        <h3>Itens Comprados:</h3>
                        <ul className={styles.listaItensDetalhe}>
                            {venda.itens.map(item => (
                                <li key={item.id} className={styles.itemDetalhe}>
                                    <div className={styles.itemProdutoInfo}>
                                        <strong>{item.quantidade}x {item.produto.nome} ({item.produto.variacao}) {item.produto.tamanho} </strong>
                                        <span>R$ {item.precoUnitario.toFixed(2)}</span>
                                    </div>
                                    
                                    {item.complementos && item.complementos.length > 0 && (
                                        <ul className={styles.listaAdicionaisDetalhe}>
                                            <span>Complementos:</span>
                                            {item.complementos.map(c => <li key={c.id}>- {c.nome} ({c.quantidade}x)</li>)}
                                        </ul>
                                    )}
                                    {item.coberturas && item.coberturas.length > 0 && (
                                         <ul className={styles.listaAdicionaisDetalhe}>
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