


import React, { createContext, useState, useContext, useEffect } from 'react';

import { apiClient } from '../services/apiClient';

import { jwtDecode } from 'jwt-decode';



const AuthContext = createContext();



export const AuthProvider = ({ children }) => {

    const [token, setToken] = useState(null);

    const [user, setUser] = useState(null);

    const [isLoading, setIsLoading] = useState(true);



    useEffect(() => {

        const storedToken = localStorage.getItem('authToken');

        if (storedToken) {

            try {

                const decodedUser = jwtDecode(storedToken);

                if (decodedUser.exp * 1000 > Date.now()) {

                    setToken(storedToken);

                    setUser({

                        nomeUsuario: decodedUser.sub,

                        funcao: decodedUser.funcao

                    });

                } else {

                    localStorage.removeItem('authToken');

                }

            } catch (error) {

                localStorage.removeItem('authToken');

            }

        }

        setIsLoading(false);

    }, []);



    const login = async (username, password) => {

        try {

            const data = await apiClient.post('/login', { nomeUsuario: username, senha: password });

            

            if (data.token) {

                setToken(data.token);

                localStorage.setItem('authToken', data.token);

                const decodedUser = jwtDecode(data.token);

                setUser({

                    nomeUsuario: decodedUser.sub, 

                    funcao: decodedUser.funcao

                });

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

        setUser(null);

    };



    const value = { token, user, isAuthenticated: !!user, isLoading, login, logout };



    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;

};



export const useAuth = () => useContext(AuthContext);
