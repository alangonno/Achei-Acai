
function Tabela({ columns, data, onAlterar, onExcluir  }) {

  // Se não houver dados
  if (!data || data.length === 0) {
    return <p>Nenhum item encontrado.</p>;
  }

  return (
    <table className="data-table">
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
              <td key={column.accessor}>
                {row[column.accessor]}
              </td>
            ))}
            {(onAlterar || onExcluir) && (
              <td className="actions">
                {onAlterar && <button onClick={() => onAlterar(row)}>Alterar</button>}
                {onExcluir && <button className="delete-button" onClick={() => onExcluir(row.id)}>Excluir</button>}
              </td>
            )}
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default Tabela;