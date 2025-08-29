import React, { createContext, useReducer, useContext } from 'react';

// 1. Criar o Contexto
const CartContext = createContext();

// 2. Definir as Ações possíveis no nosso carrinho
const Acoes = {
  ADICIONAR_PRODUTO: 'ADICIONAR_PRODUTO',
  REMOVER_ITEM: 'REMOVER_ITEM',
  ADICIONAR_COMPLEMENTO: 'ADICIONAR_COMPLEMENTO',
  ADICIONAR_COBERTURA: 'ADICIONAR_COBERTURA',
  LIMPAR_CARRINHO: 'LIMPAR_CARRINHO',
  // Adicione outras ações como ADICIONAR_COBERTURA, LIMPAR_CARRINHO, etc.
};

// 3. A Lógica do Carrinho (Reducer)
// Um reducer é ideal para gerir estados complexos como um carrinho.
function cartReducer(state, action) {
  switch (action.type) {
    case Acoes.ADICIONAR_PRODUTO: {
      const novoProduto = action.payload;
      const novoItem = {
        // Usamos um ID único para cada item no carrinho, mesmo que o produto seja o mesmo
        cartItemId: Date.now(), 
        produto: novoProduto,
        quantidade: 1,
        complementos: [],
        coberturas: [],
      };
      return {
        ...state,
        itens: [...state.itens, novoItem],
        itemSelecionadoId: novoItem.cartItemId, // O novo item torna-se o selecionado
      };
    }
    
    case Acoes.ADICIONAR_COMPLEMENTO: {
        const complemento = action.payload;
        return {
            ...state,
            itens: state.itens.map(item => 
                // Adiciona o complemento apenas ao item que está atualmente selecionado
                item.cartItemId === state.itemSelecionadoId
                ? { ...item, complementos: [...item.complementos, complemento] }
                : item
            )
        };
    }

    case Acoes.ADICIONAR_COBERTURA: {
        const cobertura = action.payload;
        return {
            ...state,
            itens: state.itens.map(item => 
                // Adiciona o complemento apenas ao item que está atualmente selecionado
                item.cartItemId === state.itemSelecionadoId
                ? { ...item, coberturas: [...item.coberturas, cobertura] }
                : item
            )
        };
    }
    
    case Acoes.REMOVER_ITEM: {
        const cartItemIdParaRemover = action.payload;
        return {
            ...state,
            itens: state.itens.filter(item => item.cartItemId !== cartItemIdParaRemover),
        };
    }

    case Acoes.LIMPAR_CARRINHO: {
      return {
        ...state,
        itens: [],
        itemSelecionadoId: null,
      };
    }

    // Lógica para calcular o total do carrinho
    // Esta não é uma ação, mas uma função auxiliar que podemos adicionar ao nosso contexto
    default:
      return state;
  }
}

// 4. O Provedor do Contexto (Componente que "abraça" a aplicação)
export function CartProvider({ children }) {
  const [state, dispatch] = useReducer(cartReducer, {
    itens: [],
    itemSelecionadoId: null, // Para saber a qual item adicionar complementos/coberturas
  });

  // Funções de ajuda para calcular o total
  const calcularTotalItem = (item) => {
    // --- ALTERAÇÃO APLICADA AQUI ---
    // Garante que o preço base seja 0 se não estiver definido
    const precoBase = (item.produto.preco || 0) * item.quantidade;
    
    // Garante que o preço do complemento seja 0 se não estiver definido
    const precoComplementos = item.complementos.reduce((total, comp) => total + (comp.preco || 0), 0);
    
    // Garante que o preço da cobertura seja 0 se não estiver definido
    const precoCoberturas = item.coberturas.reduce((total, cob) => total + (cob.preco || 0), 0);
    
    return precoBase + precoComplementos + precoCoberturas;
  };

  const valorTotalCarrinho = state.itens.reduce((total, item) => total + calcularTotalItem(item), 0);

  const value = {
    ...state,
    dispatch,
    Acoes,
    valorTotalCarrinho,
    calcularTotalItem
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

// 5. Hook personalizado para facilitar o uso do contexto
export function useCart() {
  return useContext(CartContext);
}
