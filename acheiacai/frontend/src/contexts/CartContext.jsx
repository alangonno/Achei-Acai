import React, { createContext, useReducer, useContext } from 'react';
import { v4 as uuidv4 } from 'uuid';

// 1. Criar o Contexto
const CartContext = createContext();

// 2. Definir as Ações possíveis no nosso carrinho
const Acoes = {
  ADICIONAR_PRODUTO: 'ADICIONAR_PRODUTO',
  REMOVER_ITEM: 'REMOVER_ITEM',
  ADICIONAR_COMPLEMENTO: 'ADICIONAR_COMPLEMENTO',
  ADICIONAR_COBERTURA: 'ADICIONAR_COBERTURA',
  SET_DESCONTO: 'SET_DESCONTO',
  SET_ACRESCIMO: 'SET_ACRESCIMO',
  LIMPAR_CARRINHO: 'LIMPAR_CARRINHO',
  // Adicione outras ações como ADICIONAR_COBERTURA, LIMPAR_CARRINHO, etc.
};

const estadoInicial = {
    itens: [],
    itemSelecionadoId: null,
    desconto: 0,
    acrescimo: 0,
};

// 3. A Lógica do Carrinho (Reducer)
// Um reducer é ideal para gerir estados complexos como um carrinho.
function cartReducer(state, action) {
  switch (action.type) {
    case Acoes.ADICIONAR_PRODUTO: {
      const novoProduto = action.payload;
      const novoItem = {
        // Usamos um ID único para cada item no carrinho, mesmo que o produto seja o mesmo
        cartItemId: uuidv4(), 
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
            const complementoPayload = action.payload;
            return {
                ...state,
                itens: state.itens.map(item => {
                    if (item.cartItemId === state.itemSelecionadoId) {
                        const indexExistente = item.complementos.findIndex(c => c.id === complementoPayload.id);
                        const permiteMultiplos = complementoPayload.nome.toLowerCase().includes('fini');

                        if (indexExistente > -1) {
                            if (permiteMultiplos) {
                                const novosComplementos = [...item.complementos];
                                novosComplementos[indexExistente] = { ...novosComplementos[indexExistente], quantidade: novosComplementos[indexExistente].quantidade + 1 };
                                return { ...item, complementos: novosComplementos };
                            }
                            return item;
                        } else {
                            const novoComplemento = { ...complementoPayload, quantidade: 1 };
                            return { ...item, complementos: [...item.complementos, novoComplemento] };
                        }
                    }
                    return item;
                }),
            };
        }
        

        case Acoes.ADICIONAR_COBERTURA: {
            const coberturaPayload = action.payload;
            return {
                ...state,
                itens: state.itens.map(item => {
                    if (item.cartItemId === state.itemSelecionadoId) {
                    
                        const coberturaJaExiste = item.coberturas.some(c => c.id === coberturaPayload.id);
                        if (coberturaJaExiste) {
                            return item; 
                        }
                        return { ...item, coberturas: [...item.coberturas, coberturaPayload] };
                    }
                    return item;
                }),
            };
        }

    
    case Acoes.REMOVER_ITEM: {
        const cartItemIdParaRemover = action.payload;
        return {
            ...state,
            itens: state.itens.filter(item => item.cartItemId !== cartItemIdParaRemover),
        };
    }

    case Acoes.SET_DESCONTO:
            return { ...state, desconto: action.payload };

    case Acoes.SET_ACRESCIMO:
        return { ...state, acrescimo: action.payload };

    case Acoes.LIMPAR_CARRINHO: {
      return estadoInicial;
    }

    // Lógica para calcular o total do carrinho
    // Esta não é uma ação, mas uma função auxiliar que podemos adicionar ao nosso contexto
    default:
      return state;
  }
}

// 4. O Provedor do Contexto (Componente que "abraça" a aplicação)
export function CartProvider({ children }) {
  const [state, dispatch] = useReducer(cartReducer, estadoInicial);

  // Funções de ajuda para calcular o total
  const calcularTotalItem = (item) => {
    const precoBase = (item.produto.preco || 0) * item.quantidade;
    const precoComplementos = item.complementos.reduce((total, comp) =>  total + ((comp.preco || 0) * comp.quantidade), 0);
    const precoCoberturas = item.coberturas.reduce((total, cob) => total + (cob.preco || 0), 0);
    
    return precoBase + precoComplementos + precoCoberturas;
  };

  const subtotalItens = state.itens.reduce((total, item) => total + calcularTotalItem(item), 0);
  const valorTotalCarrinho = subtotalItens + state.acrescimo - state.desconto;

  const value = { ...state, dispatch, Acoes, valorTotalCarrinho, calcularTotalItem };


  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

// 5. Hook personalizado para facilitar o uso do contexto
export function useCart() {
  return useContext(CartContext);
}
