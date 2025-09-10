import { apiClient } from './apiClient.js';

export const buscarRelatorioVendas = (dataInicio, dataFim) => {
    return apiClient.get(`/relatorios/vendas?dataInicio=${dataInicio}&dataFim=${dataFim}`);
};