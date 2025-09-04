

import React, { useState, useEffect } from 'react';
import * as usuarioService from '../services/usuarioService.js';


import pageStyles from './GestaoCardapioPage.module.css';
import formStyles from '../components/ProdutosComponents/FormularioGenerico.module.css';
import tableStyles from '../components/ProdutosComponents/Tabela.module.css';


function GestaoUsuariosPage() {
    const [usuarios, setUsuarios] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Estados para o formulário de novo utilizador
    const [nomeUsuario, setNomeUsuario] = useState('');
    const [senha, setSenha] = useState('');
    const [funcao, setFuncao] = useState('OPERADOR'); // Valor padrão para novos utilizadores

    const carregarUsuarios = async () => {
        try {
            setLoading(true);
            const data = await usuarioService.buscarTodosUsuarios();
            setUsuarios(data);
        } catch (err) {
            setError('Falha ao carregar utilizadores. Verifique se tem permissão de ADMIN.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        carregarUsuarios();
    }, []);

    const handleExcluirUsuario = async (usuarioId) => {
        // 1. Pede confirmação ao utilizador antes de uma ação destrutiva
        if (window.confirm(`Tem a certeza de que deseja excluir o utilizador com ID ${usuarioId}?`)) {
            try {
                // 2. Chama a função do serviço para apagar o utilizador na API
                await usuarioService.deletarUsuario(usuarioId);
                
                // 3. Se teve sucesso, recarrega a lista de utilizadores para atualizar a tabela
                alert('Utilizador excluído com sucesso!');
                carregarUsuarios();

            } catch (err) {
                alert('Falha ao excluir o utilizador.');
                console.error(err);
                setError('Falha ao excluir o utilizador. Verifique as permissões.');
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        if (!nomeUsuario || !senha) {
            setError("Nome de utilizador e senha são obrigatórios.");
            return;
        }
        try {
            await usuarioService.criarUsuario({ nomeUsuario, senha, funcao });
            alert('Utilizador criado com sucesso!');
            
            setNomeUsuario('');
            setSenha('');
            setFuncao('OPERADOR');
            carregarUsuarios();
        } catch (err) {
            setError('Falha ao criar utilizador. O nome de utilizador pode já existir.');
            console.error(err);
        }
    };

    return (
        <div className={pageStyles.gestaoPagina}>
            <h1>Gestão de Utilizadores</h1>

            <div className={pageStyles.formContainer}>
                <h2>Criar Novo Utilizador</h2>
                <form onSubmit={handleSubmit} className={formStyles.formEntidade}>
                    <div className={formStyles.formGroup}>
                        <label htmlFor="nomeUsuario">Nome de Utilizador</label>
                        <input
                            id="nomeUsuario"
                            type="text"
                            value={nomeUsuario}
                            onChange={(e) => setNomeUsuario(e.target.value)}
                            required
                        />
                    </div>
                    <div className={formStyles.formGroup}>
                        <label htmlFor="senha">Senha</label>
                        <input
                            id="senha"
                            type="password"
                            value={senha}
                            onChange={(e) => setSenha(e.target.value)}
                            required
                        />
                    </div>
                    <div className={formStyles.formGroup}>
                        <label htmlFor="funcao">Função</label>
                        <select
                            id="funcao"
                            value={funcao}
                            onChange={(e) => setFuncao(e.target.value)}
                        >
                            <option value="OPERADOR">Operador</option>
                            <option value="ADMIN">Administrador</option>
                        </select>
                    </div>
                    {error && <p className={pageStyles.errorMessage}>{error}</p>}
                    <div className={formStyles.formActions}>
                        <button type="submit" className={formStyles.btnSalvar}>Criar Utilizador</button>
                    </div>
                </form>
            </div>
            
            <div className={pageStyles.tableContainer}>
                <h2>Utilizadores Existentes</h2>
                {error && <p className={pageStyles.errorMessage}>{error}</p>}
                
                {loading ? (
                    <p>A carregar...</p>
                ) : (
                    <div className={tableStyles.tableWrapper}>
                        <table className={tableStyles.dataTable}>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nome de Utilizador</th>
                                    <th>Função</th>
                                    <th>Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                {usuarios.map(usuario => (
                                    <tr key={usuario.id}>
                                        <td data-label="ID">{usuario.id}</td>
                                        <td data-label="Nome de Utilizador">{usuario.nomeUsuario}</td>
                                        <td data-label="Função">{usuario.funcao}</td>
                                        <td data-label="Ações" className={tableStyles.actions}>
                                             <button
                                                className={tableStyles.deleteButton}
                                                onClick={() => handleExcluirUsuario(usuario.id)}
                                            >
                                            Excluir
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}

export default GestaoUsuariosPage;