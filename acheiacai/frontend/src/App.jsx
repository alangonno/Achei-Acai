import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link, Navigate } from 'react-router-dom';

import { AuthProvider, useAuth } from './contexts/AuthContext';
import ProtectedRoute from './components/LoginComponents/ProtectedRoute';
import { setupApiClient } from './services/apiClient'; // Import setupApiClient

import GestaoUsuariosPage from './pages/GestaoUsuariosPage';
import GestaoCardapioPage from './pages/GestaoCardapioPage';
import PontoDeVendaPage from './pages/PontoDeVendaPage';
import { CartProvider } from './contexts/CartContext';
import HistoricoVendasPage from './pages/HistoricoVendasPage';
import LoginPage from './pages/LoginPage';

import './App.css';
import RelatorioPage from './pages/RelatorioPage';

const AppHeader = () => {
    const { user, logout } = useAuth();
    const [isMenuOpen, setIsMenuOpen] = useState(false);

    useEffect(() => {
        setupApiClient(logout);
    }, [logout]); // Dependency array includes logout to re-run if logout function changes (rare but good practice)

    const handleLinkClick = () => {
        setIsMenuOpen(false);
    };

       return (
        <header className="app-header">
            <h1>Achei Açaí</h1>
            
            
            {user && (
                 <button 
                    className="menu-toggle" 
                    onClick={() => setIsMenuOpen(!isMenuOpen)}
                    aria-label="Abrir menu"
                >
                    <svg width="24" height="24" viewBox="0 0 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M4 6H20M4 12H20M4 18H20" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                </button>
            )}

            <nav className={isMenuOpen ? 'nav-menu active' : 'nav-menu'}>
                {user ? (
                    <>
                        <Link to="/pdv" onClick={handleLinkClick}>Ponto de Venda</Link>
                        <Link to="/gestao" onClick={handleLinkClick}>Gestão Cardápio</Link>
                        <Link to="/historico" onClick={handleLinkClick}>Histórico</Link>
                        <Link to="/relatorio" onClick={handleLinkClick}>Relatorio Vendas</Link>
                        

                        {user.funcao === 'ADMIN' && (
                            <Link to="/gestao-usuarios" onClick={handleLinkClick}>
                                Gestão de Utilizadores
                            </Link>
                        )}
                        
                        <button onClick={() => { handleLinkClick(); logout(); }} className="logout-button">Sair</button>
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
                                    <Route path='/relatorio' element={<RelatorioPage />}></Route>
                                    <Route path="/pdv" element={<PontoDeVendaPage />} />
                                    <Route path="/gestao" element={<GestaoCardapioPage />} />
                                    <Route path="/historico" element={<HistoricoVendasPage />} />
                                    <Route path="/gestao-usuarios" element={<GestaoUsuariosPage />} />
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