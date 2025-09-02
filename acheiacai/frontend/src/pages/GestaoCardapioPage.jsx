import { useState } from "react";
import GerenciadorDeEntidade from "../components/ProdutosComponents/GerenciadorDeEntidade";
import * as produtoService from '../services/produtoService';
import * as coberturasService from '../services/coberturasService';
import * as complementosService from '../services/complementosService';
import styles from './GestaoCardapioPage.module.css';

function GestaoCardapioPage() {

    const [abaAtiva, setAbaAtiva] = useState('produtos');

    const colunasProdutos = [
    { header: 'ID', accessor: 'id' },
    { header: 'Nome', accessor: 'nome', type: 'text' },
    { 
        header: 'Tipo', 
        accessor: 'tipo', 
        inputType: 'select',
        options: [         
            { value: 'ACAI', label: 'Açaí' },
            { value: 'SORVETE', label: 'Sorvete' },
            { value: 'SANDUICHE', label: 'Sanduíche' },
            { value: 'SUCO', label: 'Suco' },
            { value: 'WHEY', label: 'Whey' },
            { value: 'BEBIDA', label: 'Bebida' },
        ]
    },
    { header: 'Variação', accessor: 'variacao', type: 'text' },
    { header: 'Tamanho', accessor: 'tamanho', type: 'text' },
    { header: 'Preço (R$)', accessor: 'preco', type: 'number' } ]

    const colunasComplementos = [
    { header: 'ID', accessor: 'id' },
    { header: 'Nome', accessor: 'nome', type: 'text' },
    { header: 'Preço Adicional (R$)', accessor: 'preco', type: 'number' }];

    const colunasCoberturas = [
    { header: 'ID', accessor: 'id' },
    { header: 'Nome', accessor: 'nome', type: 'text' },
    { header: 'Preço Adicional (R$)', accessor: 'preco', type: 'number' }];

    return (
        <div className={styles.gestaoCardapioContainer}>
            <div className={styles.abasDeNavegacao}>
                <button 
                    className={abaAtiva === 'produtos' ? 'aba-ativa' : ''} 
                    onClick={() => setAbaAtiva('produtos')}
                >Produtos</button>
                <button 
                    className={abaAtiva === 'complementos' ? 'aba-ativa' : ''}
                    onClick={() => setAbaAtiva('complementos')}
                >Complementos</button>
                <button 
                    className={abaAtiva === 'coberturas' ? 'aba-ativa' : ''}
                    onClick={() => setAbaAtiva('coberturas')}
                >Coberturas</button>
            </div>

            <div className={styles.conteudoDaAba}>
                {abaAtiva === 'produtos' && <GerenciadorDeEntidade servico={produtoService} nomeDaEntidade="produtos" colunas={colunasProdutos} aba={abaAtiva} />}

                {abaAtiva === 'complementos' && <GerenciadorDeEntidade servico={complementosService} nomeDaEntidade="complementos" colunas={colunasComplementos} aba={abaAtiva} />}

                {abaAtiva === 'coberturas' && <GerenciadorDeEntidade servico={coberturasService} nomeDaEntidade="coberturas" colunas={colunasCoberturas} aba={abaAtiva} />}
            </div>
        </div>
        
    )
}

export default GestaoCardapioPage;