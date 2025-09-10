

import React, { useState, useEffect } from 'react';
import Carrinho from '../components/VendasComponents/Carrinho';
import { useCart } from '../contexts/CartContext';

import * as produtoService from '../services/produtoService';
import * as complementoService from '../services/complementosService';
import * as coberturaService from '../services/coberturasService';

import styles from './PontoDeVendaPage.module.css';

function PontoDeVendaPage() {
  const [produtos, setProdutos] = useState([]);
  const [complementos, setComplementos] = useState([]);
  const [coberturas, setCoberturas] = useState([]);
  const [loading, setLoading] = useState(true);

  const [filtroVariacao, setFiltroVariacao] = useState('TODAS');
  const [variacoesDisponiveis, setVariacoesDisponiveis] = useState([]);
  
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

        const todasVariacoes = [...new Set(dadosProdutos.map(p => p.variacao))];
        setVariacoesDisponiveis(todasVariacoes);
      } catch (error) {
        console.error("Falha ao carregar o cardápio", error);
      } finally {
        setLoading(false);
      }
    }
    carregarCardapio();
  }, []);

    const produtosFiltrados = produtos.filter(produto => {
        if (filtroVariacao === 'TODAS') {
            return true;
        }
        return produto.variacao === filtroVariacao;
    });


  if (loading) {
    return <p>A carregar cardápio...</p>;
  }

  return (
   <div className={styles.pdvPagina}>
      <div className={styles.pdvColunaCardapio}>
        <h2>Cardápio</h2>

         <div className={styles.filtroCardapio}>
              <label htmlFor="filtro-variacao">Filtrar por Variação:</label>
              <select
                  id="filtro-variacao"
                  value={filtroVariacao}
                  onChange={(e) => setFiltroVariacao(e.target.value)}
              >
                  <option value="TODAS">Todas as Variações</option>
                  {variacoesDisponiveis.map(variacao => (
                      <option key={variacao} value={variacao}>{variacao}</option>
                  ))}
              </select>
          </div>
        
        <section>
          <h3>Produtos</h3>
          <div className={styles.listaItensCardapio}>
            {produtosFiltrados.map(p => (
              <div 
                key={p.id} 
                className={styles.itemCardapio}
                onClick={() => dispatch({ type: Acoes.ADICIONAR_PRODUTO, payload: p })}
              >
                {p.nome} - {p.variacao} ({p.tamanho})
              </div>
            ))}
          </div>
        </section>

        <section>
          <h3>Complementos</h3>
          <div className={styles.listaItensCardapio}>
            {complementos.map(com => (
              <div 
                key={com.id} 
                className={styles.itemCardapio}
                onClick={() => dispatch({ type: Acoes.ADICIONAR_COMPLEMENTO, payload: com })}
              >
                {com.nome}
              </div>
            ))}
          </div>
        </section>

        <section>
          <h3>Coberturas</h3>
          <div className={styles.listaItensCardapio}>
            {coberturas.map(cob => (
              <div 
                key={cob.id} 
                className={styles.itemCardapio}
                onClick={() => dispatch({ type: Acoes.ADICIONAR_COBERTURA, payload: cob })}
              >
                {cob.nome}
              </div>
            ))}
          </div>
        </section>
        
      </div>

      <div className={styles.pdvColunaCarrinho}>
        <Carrinho />
      </div>
    </div>
  );
}

export default PontoDeVendaPage;