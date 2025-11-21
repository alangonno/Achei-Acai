import React, { useState, useEffect, useMemo } from 'react';
import styles from './Tabela.module.css';

function Tabela({ columns, data, onAlterar, onExcluir, nomeDaEntidade }) {
  const [filtroVariacao, setFiltroVariacao] = useState('TODAS');

  // useMemo para calcular variações apenas quando `data` muda
  const variacoesDisponiveis = useMemo(() => {
    if (nomeDaEntidade !== 'produtos' || !data) {
      return [];
    }
    return [...new Set(data.map(p => p.variacao))];
  }, [data, nomeDaEntidade]);

  // useMemo para filtrar os dados
  const dadosFiltrados = useMemo(() => {
    if (nomeDaEntidade !== 'produtos' || filtroVariacao === 'TODAS') {
      return data;
    }
    return data.filter(item => item.variacao === filtroVariacao);
  }, [data, filtroVariacao, nomeDaEntidade]);


  // Se não houver dados
  if (!data || data.length === 0) {
    return <p>Nenhum item encontrado.</p>;
  }

  return (
    <div className={styles.tableWrapper}>

      {nomeDaEntidade === 'produtos' && (
        <div className={styles.filtroContainer}>
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
      )}

      <table className={styles.dataTable}>
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.accessor}>{column.header}</th>
            ))}
            {/* 2. Adicione um cabeçalho fixo para a coluna de ações */}
            {(onAlterar || onExcluir) && <th>Ações</th>}
          </tr>
        </thead>
        <tbody>
          {dadosFiltrados.map((row) => (
            <tr key={row.id}>
              {columns.map((column) => (
                <td key={column.accessor} data-label={column.header}>
                  {column.accessor === 'preco' && !row[column.accessor]
                    ? 0
                    : row[column.accessor]}
                </td>
              ))}
              {(onAlterar || onExcluir) && (
                <td className={styles.actions}>
                  {onAlterar && 
                    <button className={styles.editButton} onClick={() => onAlterar(row)}>Alterar</button>}
                  {onExcluir && 
                    <button className={styles.deleteButton} onClick={() => onExcluir(row.id)}>Excluir</button>}
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Tabela;