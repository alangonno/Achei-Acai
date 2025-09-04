import styles from './FormularioGenerico.module.css';
import React, { useState, useEffect } from 'react';

// - dadosIniciais: um objeto com os dados do item a ser editado, ou null se for para criar um novo.
// - colunas: a configuração das colunas/campos que o formulário deve exibir.
// - onSave: a função a ser chamada quando o utilizador clica em "Salvar".
// - onCancel: a função a ser chamada quando o utilizador clica em "Cancelar".
// - nomeDaEntidade: uma string como "Produto" para usar nos títulos.
function FormularioGenerico({ dadosIniciais, colunas, onSave, onCancel, nomeDaEntidade }) {
    
    // Cria um estado inicial vazio a partir da definição das colunas
    const criarEstadoInicialVazio = () => {
        return colunas.reduce((acc, coluna) => {
            if (coluna.accessor !== 'id') {
                acc[coluna.accessor] = '';
            }
            return acc;
        }, {});
    };

    // O estado do formulário. Começa com os dados para edição ou com um objeto vazio para criação.
    const [formData, setFormData] = useState(dadosIniciais || criarEstadoInicialVazio());

    // Handler genérico para qualquer alteração nos inputs.
    // Ele usa o 'name' do input para saber qual parte do estado 'formData' deve atualizar.
    const handleChange = (e) => {
        const { name, value, type } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [name]: type === 'number' ? parseFloat(value) || 0 : value,
        }));
    };

    const handleSubmit = (evento) => {
        evento.preventDefault(); // Previne o recarregamento da página
        onSave(formData); // Chama a função onSave passada pelo pai, com os dados do formulário
    };

    // Determina se estamos no modo de edição para mudar o título
    const modoEdicao = dadosIniciais != null;

    return (
        <form className={styles.formEntidade} onSubmit={handleSubmit}>
            <h2>{modoEdicao ? `Editar ${nomeDaEntidade}` : `Adicionar Novo ${nomeDaEntidade}`}</h2>

            <div className={styles.fieldsGrid}>
                {colunas.map((coluna) => {
                    if (coluna.accessor === 'id') return null;
                    if (coluna.inputType === 'select') {
                        return (
                            <div className={styles.formGroup} key={coluna.accessor}>
                                <label htmlFor={coluna.accessor}>{coluna.header}</label>
                                <select
                                    id={coluna.accessor}
                                    name={coluna.accessor}
                                    value={formData[coluna.accessor] || ''}
                                    onChange={handleChange}
                                    required
                                >
                                    <option value="" disabled>Selecione uma opção</option>
                                    {coluna.options.map(option => (
                                        <option key={option.value} value={option.value}>
                                            {option.label}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        );
                    }
                    
                    return (
                        <div className={styles.formGroup} key={coluna.accessor}>
                            <label htmlFor={coluna.accessor}>{coluna.header}</label>
                            <input
                                type={coluna.type || 'text'}
                                id={coluna.accessor}
                                name={coluna.accessor}
                                value={formData[coluna.accessor] || ''}
                                onChange={handleChange}
                                required
                                step={coluna.type === 'number' ? '0.01' : undefined}
                            />
                        </div>
                    );
                })}
            </div>

            <div className={styles.formActions}>
                <button type="submit" className={styles.btnSalvar}>Salvar</button>
                <button type="button" onClick={onCancel} className={styles.btnCancelar}>Cancelar</button>
            </div>
        </form>
    );
}

export default FormularioGenerico;