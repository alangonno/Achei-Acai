
const API_BASE_URL = 'http://localhost:8080/achei-acai-api';

const handleResponse = async (response) => {

    if (!response.ok) {
        const errorData = await response.text();
        console.error("Erro da API:", errorData);
        throw new Error(`Erro na API: ${response.status} ${response.statusText}`);
    }
    return response.status === 204 ? null : response.json();
};

const request = async (endpoint, options) => {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        return handleResponse(response);
    } catch (error) {
        console.error(`Falha na requisição para ${endpoint}:`, error);
        throw error;
    }
};

export const apiClient = {
    get: (endpoint) => request(endpoint, { method: 'GET' }),
    post: (endpoint, data) => request(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
    }),
    put: (endpoint, data) => request(endpoint, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
    }),
    delete: (endpoint) => request(endpoint, { method: 'DELETE' }),
};