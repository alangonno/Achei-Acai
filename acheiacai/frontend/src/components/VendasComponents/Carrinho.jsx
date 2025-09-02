// frontend/src/components/Carrinho.jsx

import React, { useState } from 'react';
import { useCart } from '../../contexts/CartContext';
import { criarVenda } from '../../services/vendaService';

function Carrinho() {
    const { itens, itemSelecionadoId, dispatch, Acoes, valorTotalCarrinho, calcularTotalItem } = useCart();
    const [formaPagamento, setFormaPagamento] = useState('CREDITO');

    const handleFinalizarVenda = async () => {
        if (itens.length === 0) {
            alert("O carrinho está vazio!");
            return;
        }

        // --- CORREÇÃO APLICADA AQUI ---
        const dadosParaApi = {
            formaPagamento: formaPagamento,
            valorTotal: valorTotalCarrinho,
            itens: itens.map(item => ({
                produtoId: item.produto.id,
                quantidade: item.quantidade,
                precoUnitario: item.produto.preco,
                // O nome do campo agora é 'complementos', não 'complementosIds'.
                // E o conteúdo é uma lista de objetos {id, quantidade}.
                complementos: item.complementos.map(c => ({ id: c.id, quantidade: c.quantidade })),
                
                // O mesmo para 'coberturas'.
                coberturas: item.coberturas.map(c => ({ id: c.id, quantidade: 1 })), // Assumindo quantidade 1 para coberturas
            }))
        };

        try {
            const vendaCriada = await criarVenda(dadosParaApi);
            alert(`Venda #${vendaCriada.id} registada com sucesso!`);
            dispatch({ type: Acoes.LIMPAR_CARRINHO });
        } catch (error) {
            alert("Ocorreu um erro ao registar a venda. Verifique o console para mais detalhes.");
            console.error("Erro ao finalizar a venda:", error);
        }
    };

    if (itens.length === 0) {
        return (
            <div className="carrinho">
                <h2>Carrinho</h2>
                <p>O seu carrinho está vazio.</p>
            </div>
        );
    }

    return (
        <div className="carrinho">
            <h2>Carrinho</h2>
            <ul className="carrinho-lista-itens">
                {itens.map(item => (
                    <li
                        key={item.cartItemId}
                        className={`carrinho-item ${item.cartItemId === itemSelecionadoId ? 'selecionado' : ''}`}
                        onClick={() => dispatch({ type: Acoes.SELECIONAR_ITEM, payload: item.cartItemId })}
                    >
                        <div className="item-info">
                            <strong>{item.produto.nome} {item.produto.variacao} ({item.produto.tamanho})</strong>
                            <span> - {item.quantidade}x R$ {item.produto.preco.toFixed(2)}</span>
                        </div>

                        {item.complementos.length > 0 && (
                            <ul className="lista-adicionais">
                                {item.complementos.map(comp => <li key={`${item.cartItemId}-${comp.id}`}>+ {comp.nome} ({comp.quantidade}x)</li>)}
                            </ul>
                        )}
                        {item.coberturas.length > 0 && (
                            <ul className="lista-adicionais">
                                {item.coberturas.map(cob => <li key={`${item.cartItemId}-${cob.id}`}>+ {cob.nome}</li>)}
                            </ul>
                        )}

                        <div className="item-controles">
                            <span>Subtotal: R$ {calcularTotalItem(item).toFixed(2)}</span>
                            <button
                                className="botao-remover"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    dispatch({ type: Acoes.REMOVER_ITEM, payload: item.cartItemId });
                                }}
                            >
                                Remover
                            </button>
                        </div>
                    </li>
                ))}
            </ul>
            <div className="carrinho-total">
                <h3>Total: R$ {valorTotalCarrinho.toFixed(2)}</h3>
                <div className="forma-pagamento-seletor">
                    <label htmlFor="pagamento">Forma de Pagamento:</label>
                    <select 
                        id="pagamento"
                        value={formaPagamento}
                        onChange={(e) => setFormaPagamento(e.target.value)}
                    >
                        <option value="PIX">PIX</option>
                        <option value="CREDITO">Crédito</option>
                        <option value="DEBITO">Débito</option>
                        <option value="DINHEIRO">Dinheiro</option>
                    </select>
                </div>
                <button className="botao-finalizar" onClick={handleFinalizarVenda}>
                    Finalizar Venda
                </button>
            </div>
        </div>
    );
}

export default Carrinho;