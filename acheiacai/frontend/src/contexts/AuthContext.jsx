
import React, { createContext, useState, useContext, useEffect } from 'react';
import { apiClient } from '../services/apiClient';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(localStorage.getItem('authToken'));
    const [user, setUser] = useState(null); 
 


    useEffect(() => {
        if (token) {
            try {
                const decodedUser = jwtDecode(token);
                setUser({
                    nomeUsuario: decodedUser.sub, 
                    funcao: decodedUser.funcao   // A nossa "claim" personalizada para a função
                });
                localStorage.setItem('authToken', token);
            } catch (error) {
                // Se o token for inválido, limpa tudo
                console.error("Token inválido:", error);
                localStorage.removeItem('authToken');
                setUser(null);
            }
        } else {
            setUser(null);
        }
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

    const value = { token, user, isAuthenticated: !!user, login, logout };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);