// frontend/src/pages/RelatorioPage.jsx
import React, { useState } from 'react';
import { buscarRelatorioVendas } from '../services/relatorioService.js';
import pageStyles from './RelatorioPage.module.css';
import formStyles from '../components/ProdutosComponents/FormularioGenerico.module.css';

function RelatorioPage() {
    const [dataInicio, setDataInicio] = useState('');
    const [dataFim, setDataFim] = useState('');
    const [relatorio, setRelatorio] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleGerarRelatorio = async () => {
        if (!dataInicio || !dataFim) {
            setError('Por favor, selecione as duas datas.');
            return;
        }
        setLoading(true);
        setError('');
        setRelatorio(null);
        try {
            const dados = await buscarRelatorioVendas(dataInicio, dataFim);
            setRelatorio(dados);
        } catch (err) {
            setError('Falha ao buscar o relatório.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };



    return (
        <div className={pageStyles.relatorioPagina}>
            <h1>Relatório de Vendas</h1>
            <div className={pageStyles.filtroContainer}>
                <div className={formStyles.formGroup}>
                    <label htmlFor="data-inicio">Data de Início:</label>
                    <input type="date" id="data-inicio" value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} />
                </div>
                <div className={formStyles.formGroup}>
                    <label htmlFor="data-fim">Data de Fim:</label>
                    <input type="date" id="data-fim" value={dataFim} onChange={(e) => setDataFim(e.target.value)} />
                </div>
                <button className={formStyles.btnAcao} onClick={handleGerarRelatorio} disabled={loading}>
                    {loading ? 'A gerar...' : 'Gerar Relatório'}
                </button>
            </div>
            
            {error && <p className={pageStyles.errorMessage}>{error}</p>}

            {relatorio && (
                <div className={pageStyles.relatorioResultados}>
                    <div className={pageStyles.resumoFinanceiro}>
                        <h2>Resumo Financeiro</h2>
                        <p className={pageStyles.totalGeral}>Total Geral Vendido: <strong>R$ {relatorio.totalGeralVendas.toFixed(2)}</strong></p>
                        <ul>
                            {relatorio.totalPorPagamentos.map(item => (
                                <li key={item.formaPagamento}>{item.formaPagamento}: <span>R$ {item.total.toFixed(2)}</span></li>
                            ))}
                        </ul>
                    </div>

                    <div className={pageStyles.resultadoBloco}>
                        <h2>Volume Vendido (Litros)</h2>
                        <p><strong>Total Açaí:</strong> {relatorio.volumeTotal.totalLitrosAcai.toFixed(2)} L</p>
                        <p><strong>Total Sorvete:</strong> {relatorio.volumeTotal.totalLitrosSorvete.toFixed(2)} L</p>
                        <p><strong>Total Suco:</strong> {relatorio.volumeTotal.totalLitrosSuco.toFixed(2)} L</p>
                        <p><strong>Total Suco:</strong> {relatorio.volumeTotal.totalLitrosMousse.toFixed(2)} L</p>
                        <details>
                            <summary>Ver Detalhes por Variação</summary>
                            <ul>
                                {relatorio.litrosProduto.map(item => (
                                    <li key={`${item.tipo}-${item.variacao}`}>{item.tipo} - {item.variacao}: <span>{item.totalLitros.toFixed(2)} L</span></li>
                                ))}
                            </ul>
                        </details>
                    </div>

                    <div className={pageStyles.resultadoBloco}>
                        <h2>Total de Complementos Vendidos</h2>
                        <ul>
                            {relatorio.totalComplementos.map(item => (
                                <li key={item.nome}>{item.nome}: <span>{item.quantidadeTotal}</span></li>
                            ))}
                        </ul>
                    </div>
                    
                    <div className={pageStyles.resultadoBloco}>
                        <h2>Total de Coberturas Vendidas</h2>
                         <ul>
                            {relatorio.totalCoberturas.map(item => (
                                <li key={item.nome}>{item.nome}: <span>{item.quantidadeTotal}</span></li>
                            ))}
                        </ul>
                    </div>

                    <div className={pageStyles.resultadoBloco}>
                        <h2>Outros Itens Vendidos</h2>
                         <ul>
                            {relatorio.totalOutrosProdutos.map(item => (
                                <li key={item.nome}>{item.nome}: <span>{item.quantidadeTotal}</span></li>
                            ))}
                        </ul>
                    </div>
                </div>
            )}
        </div>
    );
}

export default RelatorioPage;