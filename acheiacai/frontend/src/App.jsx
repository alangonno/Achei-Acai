import { useState } from 'react';
import { BrowserRouter, Routes, Route, Link, Navigate } from 'react-router-dom';

import { AuthProvider, useAuth } from './contexts/AuthContext';
import ProtectedRoute from './components/LoginComponents/ProtectedRoute';

import GestaoCardapioPage from './pages/GestaoCardapioPage';
import PontoDeVendaPage from './pages/PontoDeVendaPage';
import { CartProvider } from './contexts/CartContext';
import HistoricoVendasPage from './pages/HistoricoVendasPage';
import LoginPage from './pages/LoginPage';

import './App.css';

const AppHeader = () => {
    const { isAuthenticated, logout } = useAuth();
    const [isMenuOpen, setIsMenuOpen] = useState(false);

    const handleLinkClick = () => {
        setIsMenuOpen(false);
    };

    return (
        <header className="app-header">
            <h1>Achei Açaí</h1>
            {isAuthenticated && (
                 <button 
                    className="menu-toggle" 
                    onClick={() => setIsMenuOpen(!isMenuOpen)}
                    aria-label="Abrir menu"
                >
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M4 6H20M4 12H20M4 18H20" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                </button>
            )}
            <nav className={isMenuOpen ? 'nav-menu active' : 'nav-menu'}>
                {isAuthenticated ? (
                    <>
                        <Link to="/pdv" onClick={handleLinkClick}>Ponto de Venda</Link>
                        <Link to="/gestao" onClick={handleLinkClick}>Gestão</Link>
                        <Link to="/historico" onClick={handleLinkClick}>Histórico</Link>
                        <button onClick={logout} className="logout-button">Sair</button>
                    </>
                ) : (
                    <Link to="/login" onClick={handleLinkClick}>Login</Link>
                )}
            </nav>
        </header>
    );
};

function App() {
    return (
        // Aninhar os Provedores. O AuthProvider é o mais externo.
        <AuthProvider>
            <CartProvider>
                <BrowserRouter>
                    <div className="app-container">
                        <AppHeader />
                        <main>
                            <Routes>
                                {/* Rota Pública */}
                                <Route path="/login" element={<LoginPage />} />

                                {/* Rotas Protegidas */}
                                <Route element={<ProtectedRoute />}>
                                    <Route path="/pdv" element={<PontoDeVendaPage />} />
                                    <Route path="/gestao" element={<GestaoCardapioPage />} />
                                    <Route path="/historico" element={<HistoricoVendasPage />} />
                                    <Route path="/" element={<Navigate to="/pdv" />} /> 
                                </Route>
                            </Routes>
                        </main>
                    </div>
                </BrowserRouter>
            </CartProvider>
        </AuthProvider>
    );
}

export default App;