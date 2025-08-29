
import React from 'react';
import { useCart } from '../contexts/CartContext';
import { criarVenda } from '../services/vendaService';

function Carrinho() {
  
  const { itens, dispatch, Acoes, valorTotalCarrinho, calcularTotalItem } = useCart();

  const handleFinalizarVenda = async () => {
        
        const dadosParaApi = {
            formaPagamento: 'PIX', 
            valorTotal: valorTotalCarrinho,
            itens: itens.map(item => ({
                produtoId: item.produto.id,
                quantidade: item.quantidade,
                precoUnitario: item.produto.preco,
                complementosIds: item.complementos.map(c => c.id),
                coberturasIds: item.coberturas.map(c => c.id),
            }))
        };

        try {
           
            const vendaCriada = await criarVenda(dadosParaApi);
            alert(`Venda #${vendaCriada.id} registada com sucesso!`);
            
            
            dispatch({ type: Acoes.LIMPAR_CARRINHO }); 

        } catch (error) {
            alert("Ocorreu um erro ao registar a venda. Tente novamente.");
            console.error("Erro ao finalizar a venda:", error);
        }
    };

  if (itens.length === 0) {
    return (
      <div className="carrinho-vazio">
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
          <li key={item.cartItemId} className="carrinho-item">
            <div className="item-info">
              <strong>{item.produto.nome} ({item.produto.variacao})</strong>
              <span> - {item.quantidade}x R$ {item.produto.preco.toFixed(2)}</span>
            </div>
            
            
            {item.complementos.length > 0 && (
              <ul className="lista-adicionais">
                {item.complementos.map(comp => <li key={comp.id}>+ {comp.nome}</li>)}
              </ul>
            )}
            
            {item.coberturas.length > 0 && (
              <ul className="lista-adicionais">
                {item.coberturas.map(cob => <li key={cob.id}>+ {cob.nome}</li>)}
              </ul>
            )}
            
            <div className="item-controles">
              <span>Subtotal: R$ {calcularTotalItem(item).toFixed(2)}</span>
              <button 
                className="botao-remover"
                onClick={() => dispatch({ type: Acoes.REMOVER_ITEM, payload: item.cartItemId })}
              >
                Remover
              </button>
            </div>
          </li>
        ))}
      </ul>
      <div className="carrinho-total">
        <h3>Total: R$ {valorTotalCarrinho.toFixed(2)}</h3>
        <button className="botao-finalizar" onClick={handleFinalizarVenda} disabled={itens.length === 0}>
                Finalizar Venda
            </button>
      </div>
    </div>
  );
}

export default Carrinho;
