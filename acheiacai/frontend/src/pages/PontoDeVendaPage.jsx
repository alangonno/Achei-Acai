

import React, { useState, useEffect } from 'react';
import Carrinho from '../components/VendasComponents/Carrinho';
import { useCart } from '../contexts/CartContext';

import * as produtoService from '../services/produtoService';
import * as complementoService from '../services/complementosService';
import * as coberturaService from '../services/coberturasService';

function PontoDeVendaPage() {
  const [produtos, setProdutos] = useState([]);
  const [complementos, setComplementos] = useState([]);
    const [coberturas, setCoberturas] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Acedemos à função 'dispatch' e às 'Acoes' do nosso contexto do carrinho
  const { dispatch, Acoes } = useCart();

  // Efeito para carregar todos os dados do cardápio quando a página monta
  useEffect(() => {
    async function carregarCardapio() {
      try {
        setLoading(true);
        // Carrega produtos e complementos em paralelo
        const [dadosProdutos, dadosComplementos, dadosCoberturas] = await Promise.all([
          produtoService.buscarTodos(),
          complementoService.buscarTodos(),
          coberturaService.buscarTodos(),
        ]);
        setProdutos(dadosProdutos);
        setComplementos(dadosComplementos);
        setCoberturas(dadosCoberturas);
      } catch (error) {
        console.error("Falha ao carregar o cardápio", error);
      } finally {
        setLoading(false);
      }
    }
    carregarCardapio();
  }, []);

  if (loading) {
    return <p>A carregar cardápio...</p>;
  }

  return (
    <div className="pdv-pagina">
      <div className="pdv-coluna-cardapio">
        <h2>Cardápio</h2>
        
        <section>
          <h3>Produtos</h3>
          <div className="lista-itens-cardapio">
            {produtos.map(p => (
              <div 
                key={p.id} 
                className="item-cardapio"
                onClick={() => dispatch({ type: Acoes.ADICIONAR_PRODUTO, payload: p })}
              >
                {p.nome} - {p.variacao} ({p.tamanho})
              </div>
            ))}
          </div>
        </section>

        <section>
          <h3>Complementos</h3>
          <div className="lista-itens-cardapio">
            {complementos.map(com => (
              <div 
                key={com.id} 
                className="item-cardapio"
                onClick={() => dispatch({ type: Acoes.ADICIONAR_COMPLEMENTO, payload: com })}
              >
                {com.nome}
              </div>
            ))}
          </div>
        </section>

        <section>
          <h3>Coberturas</h3>
          <div className="lista-itens-cardapio">
            {coberturas.map(cob => (
              <div 
                key={cob.id} 
                className="item-cardapio"
                onClick={() => dispatch({ type: Acoes.ADICIONAR_COBERTURA, payload: cob })}
              >
                {cob.nome}
              </div>
            ))}
          </div>
        </section>
        
      </div>

      <div className="pdv-coluna-carrinho">
        <Carrinho />
      </div>
    </div>
  );
}

export default PontoDeVendaPage;