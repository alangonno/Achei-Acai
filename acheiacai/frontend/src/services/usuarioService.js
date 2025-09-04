import { apiClient } from './apiClient.js';

const resource = '/usuarios';

/**
 * Cria um novo utilizador. Requer permissão de ADMIN.
 * @param {object} dadosUsuario - Objeto com nomeUsuario, senha e funcao.
 * @returns {Promise<object>} O utilizador recém-criado (sem a senha).
 */
export const criarUsuario = (dadosUsuario) => {
    return apiClient.post(resource, dadosUsuario);
};

/**
 * Busca todos os utilizadores. Requer permissão de ADMIN.
 * @returns {Promise<Array>} A lista de todos os utilizadores.
 */
export const buscarTodosUsuarios = () => {
    return apiClient.get(resource);
};

export const deletarUsuario = (id) => {
    return apiClient.delete(`${resource}/${id}`);
}

