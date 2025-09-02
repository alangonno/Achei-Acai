import styles from './Tabela.module.css';

function Tabela({ columns, data, onAlterar, onExcluir }) {

  // Se não houver dados
  if (!data || data.length === 0) {
    return <p>Nenhum item encontrado.</p>;
  }

  return (
    <div className={styles.tableWrapper}>
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
          {data.map((row) => (
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