
const API_BASE_URL = 'http://api.acheiacai.com.br/';

const handleResponse = async (response) => {

    if (!response.ok) {
        const errorData = await response.text();
        console.error("Erro da API:", errorData);
        throw new Error(`Erro na API: ${response.status} ${response.statusText}`);
    }
    return response.status === 204 ? null : response.json();
};

const request = async (endpoint, options = {}) => {
    
    const token = localStorage.getItem('authToken');

    // 2. Define os cabeçalhos padrão
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers, // Permite que outros cabeçalhos sejam passados, se necessário
    };

    // 3. Se um token existir, adiciona-o ao cabeçalho de Authorization
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