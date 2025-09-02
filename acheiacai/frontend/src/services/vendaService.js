
import { apiClient } from './apiClient';

const resource = '/vendas';

export const criarVenda = (dadosDaVenda) => {
    return apiClient.post(resource, dadosDaVenda);
};

export const buscarVendaPorId = (id) => {
    return apiClient.get(`${resource}/${id}`);
};

export const buscarTodasVendas = () => {
    return apiClient.get(resource);
};

export const deletarVendaPorId = (id) => {
    return apiClient.delete(`${resource}/${id}`)
}