import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import GestaoCardapioPage from './pages/GestaoCardapioPage';
import PontoDeVendaPage from './pages/PontoDeVendaPage';
import { CartProvider } from './contexts/CartContext';
import HistoricoVendasPage from './pages/HistoricoVendasPage';

import './App.css';

function App() {
  return (
    <CartProvider>
      <BrowserRouter>
        <div className="app-container">
          <header className="app-header">
            <h1>Achei Açaí</h1>
            <nav>
              <Link to="/gdc">Gestão do Cardápio</Link>
              <Link to="/">Ponto de Venda</Link>
              <Link to="/hdv">Historico de Venda</Link>
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
