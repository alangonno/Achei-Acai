import { apiClient } from './apiClient';

const resource = '/coberturas'; 

export const buscarTodos = () => {
    return apiClient.get(resource);
};

export const buscarPorId = (id) => {
    return apiClient.get(`${resource}/${id}`);
};

export const criar = (data) => {
    return apiClient.post(resource, data);
};

export const atualizar = (id, data) => {
    return apiClient.put(`${resource}/${id}`, data);
};

export const deletar = (id) => {
    return apiClient.delete(`${resource}/${id}`);
};