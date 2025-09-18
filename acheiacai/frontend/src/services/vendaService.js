
import { apiClient } from './apiClient';

const resource = '/vendas';

export const criarVenda = (dadosDaVenda) => {
    return apiClient.post(resource, dadosDaVenda);
};

export const buscarVendaPorId = (id) => {
    return apiClient.get(`${resource}/${id}`);
};

export const buscarTodasVendas = (page = 0, size = 10) => {
    return apiClient.get(`${resource}/?page=${page}&size=${size}`);
};

export const deletarVendaPorId = (id) => {
    return apiClient.delete(`${resource}/${id}`)
}