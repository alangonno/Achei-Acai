
import { useState } from 'react';

function FormularioProduto({ onSave, onCancel }) {

  
    const [nome, setNome] = useState('');
    const [tipo, setTipo] = useState('ACAI');
    const [variacao, setVariacao] = useState('');
    const [tamanho, setTamanho] = useState('');
    const [preco, setPreco] = useState(0);

    const handleSubmit = (evento) => {
        // Previne o comportamento padrão do formulário, que é recarregar a página
        evento.preventDefault();

        const novoProduto = {
            nome,
            tipo,
            variacao,
            tamanho,
            preco: parseFloat(preco) 
        };

        onSave(novoProduto);
    };

    return (
        <form className="form-entidade" onSubmit={handleSubmit}>
            <h2>Adicionar Novo Produto</h2>

            <div className="form-group">
                <label htmlFor="nome">Nome Principal</label>
                <input
                    id="nome"
                    type="text"
                    value={nome}
                    onChange={(e) => setNome(e.target.value)}
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="tipo">Tipo</label>
                <select 
                    id="tipo" 
                    value={tipo} 
                    onChange={(e) => setTipo(e.target.value)}
                >
                    <option value="ACAI">Açaí</option>
                    <option value="SORVETE">Sorvete</option>
                    <option value="SANDUICHE">Sanduíche</option>
                    <option value="SUCO">Suco</option>
                    <option value="WHEY">Whey</option>
                    <option value="BEBIDA">Bebida</option>
                </select>
            </div>

            <div className="form-group">
                <label htmlFor="variacao">Variação</label>
                <input
                    id="variacao"
                    type="text"
                    value={variacao}
                    onChange={(e) => setVariacao(e.target.value)}
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="tamanho">Tamanho</label>
                <input
                    id="tamanho"
                    type="text"
                    value={tamanho}
                    onChange={(e) => setTamanho(e.target.value)}
                />
            </div>

            <div className="form-group">
                <label htmlFor="preco">Preço (R$)</label>
                <input
                    id="preco"
                    type="number"
                    step="0.01"
                    value={preco}
                    onChange={(e) => setPreco(e.target.value)}
                    required
                />
            </div>

            <div className="form-actions">
                <button type="submit" className="btn-salvar">Salvar</button>

                <button type="button" onClick={onCancel} className="btn-cancelar">Cancelar</button>
            </div>
        </form>
    );
}

export default FormularioProduto;