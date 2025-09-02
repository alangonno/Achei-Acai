import { useState } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import GestaoCardapioPage from './pages/GestaoCardapioPage';
import PontoDeVendaPage from './pages/PontoDeVendaPage';
import { CartProvider } from './contexts/CartContext';
import HistoricoVendasPage from './pages/HistoricoVendasPage';

import './App.css';

function App() {

  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <CartProvider>
      <BrowserRouter>
        <div className="app-container">
          <header className="app-header">
            <h1>Achei Açaí</h1>
            <button 
              className="menu-toggle" 
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              aria-label="Abrir menu"
            >
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 6H20M4 12H20M4 18H20" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </button>
            <nav className={isMenuOpen ? 'nav-menu active' : 'nav-menu'}>
              <Link to="/gdc" onClick={() => setIsMenuOpen(false)}>Gestão do Cardápio</Link>
              <Link to="/" onClick={() => setIsMenuOpen(false)}>Ponto de Venda</Link>
              <Link to="/hdv" onClick={() => setIsMenuOpen(false)}>Histórico de Venda</Link>
            </nav>
          </header>

          <main>
            <Routes>
              <Route path="/gdc" element={<GestaoCardapioPage />} />
              <Route path="/" element={<PontoDeVendaPage />} />
              <Route path="/hdv" element={<HistoricoVendasPage />} />
            </Routes>
          </main>
        </div>
      </BrowserRouter>
    </CartProvider>
  );
}

export default App;
