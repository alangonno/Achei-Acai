import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import GestaoCardapioPage from './pages/GestaoCardapioPage';
import PontoDeVendaPage from './pages/PontodeVendaPage';
import { CartProvider } from './contexts/CartContext';

import './App.css';

function App() {
  return (
    // 1. O CartProvider "abraça" toda a aplicação,
    // tornando o estado do carrinho acessível em qualquer página.
    <CartProvider>
      <BrowserRouter>
        <div className="app-container">
          <header className="app-header">
            <h1>Achei Açaí</h1>
            <nav>
              <Link to="/">Gestão do Cardápio</Link>
              <Link to="/pdv">Ponto de Venda</Link>
            </nav>
          </header>

          <main>
            <Routes>
              <Route path="/" element={<GestaoCardapioPage />} />
              <Route path="/pdv" element={<PontoDeVendaPage />} />
              {/* Adicione outras rotas aqui no futuro */}
            </Routes>
          </main>
        </div>
      </BrowserRouter>
    </CartProvider>
  );
}

export default App;
