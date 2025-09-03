
import React, { createContext, useState, useContext, useEffect } from 'react';
import { apiClient } from '../services/apiClient';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(localStorage.getItem('authToken'));
    const [isAuthenticated, setIsAuthenticated] = useState(!!token);

    useEffect(() => {
        setIsAuthenticated(!!token);
    }, [token]);

    const login = async (username, password) => {
        try {
            // Usa o apiClient, que NÃO envia token para o login
            const data = await apiClient.post('/login', { nomeUsuario: username, senha: password });
            
            if (data.token) {
                localStorage.setItem('authToken', data.token);
                setToken(data.token);
                return true;
            }
            return false;
        } catch (error) {
            console.error("Falha no login:", error);
            return false;
        }
    };

    const logout = () => {
        localStorage.removeItem('authToken');
        setToken(null);
    };

    const value = { token, isAuthenticated, login, logout };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);