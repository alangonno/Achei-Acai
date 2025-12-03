
import React from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Navigate, Outlet } from 'react-router-dom';
import styles from '../../pages/Loading.module.css';

function ProtectedRoute() {
    const { isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        // Enquanto o estado de autenticação está a ser verificado, mostramos um loader.
        return (
            <div className={styles.loadingContainer}>
                <div className={styles.spinner}></div>
                <p>Carregando...</p>
            </div>
        );
    }

    // Após a verificação, se o utilizador estiver autenticado, renderiza o conteúdo da rota.
    // Se não, redireciona para a página de login.
    return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
}

export default ProtectedRoute;