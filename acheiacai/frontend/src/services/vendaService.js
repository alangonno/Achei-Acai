
import { apiClient } from './apiClient';

const resource = '/vendas';

/**
 * @param {object} dadosDaVenda 
 * @returns {Promise<object>} Uma promessa que resolve para o objeto da venda recém-criada, retornado pela API.
 */
export const criarVenda = (dadosDaVenda) => {
    return apiClient.post(resource, dadosDaVenda);
};


// --- Funções Futuras (placeholders) ---
// Quando você for construir o histórico de vendas, irá implementar estas funções.

/**
 * Busca o histórico de todas as vendas.
 * @returns {Promise<Array>} Uma promessa que resolve para a lista de todas as vendas.
 */
/*
export const buscarTodasVendas = () => {
    return apiClient.get(resource);
};
*/

/**
 * Busca os detalhes completos de uma venda específica pelo seu ID.
 * @param {number | string} id - O ID da venda a ser buscada.
 * @returns {Promise<object>} Uma promessa que resolve para o objeto da venda detalhada.
 */
/*
export const buscarVendaPorId = (id) => {
    return apiClient.get(`${resource}/${id}`);
};
*/
