import React, { useState, useEffect } from 'react';
import * as usuarioService from '../services/usuarioService';

// (No futuro, você pode criar um FormularioUsuario.jsx reutilizável)

function GestaoUsuariosPage() {
    const [usuarios, setUsuarios] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    
    const [nomeUsuario, setNomeUsuario] = useState('');
    const [senha, setSenha] = useState('');
    const [funcao, setFuncao] = useState('OPERADOR');

    const carregarUsuarios = async () => {
        try {
            const data = await usuarioService.buscarTodosUsuarios();
            setUsuarios(data);
        } catch (err) {
            setError('Falha ao carregar utilizadores. Você tem permissão de ADMIN?');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        carregarUsuarios();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await usuarioService.criarUsuario({ nomeUsuario, senha, funcao });
            alert('Utilizador criado com sucesso!');
            // Limpa o formulário e recarrega a lista
            setNomeUsuario('');
            setSenha('');
            setFuncao('OPERADOR');
            carregarUsuarios();
        } catch (err) {
            alert('Falha ao criar utilizador.');
            console.error(err);
        }
    };

    if (loading) return <p>A carregar utilizadores...</p>;

    return (
        <div className="gestao-pagina">
            <h1>Gestão de Utilizadores</h1>

            <div className="form-container">
                <h2>Criar Novo Utilizador</h2>
                <form onSubmit={handleSubmit}>
                    {/* ... campos do formulário para nomeUsuario, senha, funcao ... */}
                    <button type="submit">Criar Utilizador</button>
                </form>
            </div>
            
            <div className="table-container">
                <h2>Utilizadores Existentes</h2>
                {error && <p className="error-message">{error}</p>}
                <table>
                    
                </table>
            </div>
        </div>
    );
}

export default GestaoUsuariosPage;