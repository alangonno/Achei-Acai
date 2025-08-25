import { useState } from "react";
import GerenciadorDeEntidade from "../components/GerenciadorDeEntidade";

function GestaoCardapioPage() {

    const [abaAtiva, setAbaAtiva] = useState('produtos');

    return (
        <div className="gestao-cardapio-container">
            <div className="abas-de-navegacao">
                <button onClick={() => setAbaAtiva('produtos')}>Produtos</button>
                <button onClick={() => setAbaAtiva('complementos')}>Complementos</button>
                <button onClick={() => setAbaAtiva('coberturas')}>Coberturas</button>
            </div>

            <div className="conteudo-da-aba">
                {abaAtiva === 'produtos' && <GerenciadorDeEntidade tipo="produtos" aba={abaAtiva} />}
                {abaAtiva === 'complementos' && <GerenciadorDeEntidade tipo="complementos" aba={abaAtiva} />}
                {abaAtiva === 'coberturas' && <GerenciadorDeEntidade tipo="coberturas" aba={abaAtiva} />}
            </div>
        </div>
        
    )
}

export default GestaoCardapioPage;