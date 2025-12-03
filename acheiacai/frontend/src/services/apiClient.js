
// 1. Armazenamento da função de logout no escopo do módulo
let authLogout = () => {};

// 2. Função de configuração para injeção de dependência
export function setupApiClient(logoutHandler) {
    if (logoutHandler) {
        authLogout = logoutHandler;
    }
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/achei-acai-api';

const handleResponse = async (response) => {
    if (response.status === 401) {
        authLogout(); // Chama a função de logout injetada
        throw new Error('Sessão expirada. Você foi desconectado.');
    }

    if (!response.ok) {
        const errorData = await response.text();
        console.error("Erro da API:", errorData);
        throw new Error(`Erro na API: ${response.status} ${response.statusText}`);
    }
    return response.status === 204 ? null : response.json();
};

const request = async (endpoint, options = {}) => {
    
    const token = localStorage.getItem('authToken');

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            headers,
        });
        return handleResponse(response);
    } catch (error) {
        console.error(`Falha na requisição para ${endpoint}:`, error);
        // Não re-chama authLogout aqui, pois o handleResponse já trata o 401
        // Outros erros (ex: rede) não devem necessariamente deslogar.
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
