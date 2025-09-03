

import React from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Navigate, Outlet } from 'react-router-dom';

function ProtectedRoute() {
    const { isAuthenticated } = useAuth();

    // Se o utilizador estiver autenticado, renderiza o conteúdo da rota (Outlet).
    // Se não, redireciona para a página de login.
    return isAuthenticated ? <Outlet /> : <Navigate to="/login" />;
}

export default ProtectedRoute;