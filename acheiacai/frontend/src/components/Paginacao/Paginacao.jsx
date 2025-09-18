
import styles from './Paginacao.module.css';

function Paginacao({ paginaAtual, totalPaginas, onPageChange }) {
    
    const handleAnterior = () => {
        // Só permite voltar se não estiver na primeira página
        if (paginaAtual > 0) {
            onPageChange(paginaAtual - 1);
        }
    };

    const handleProxima = () => {
        // Só permite avançar se não estiver na última página
        if (paginaAtual < totalPaginas - 1) {
            onPageChange(paginaAtual + 1);
        }
    };

    // Não renderiza nada se houver apenas uma página ou nenhuma
    if (totalPaginas <= 1) {
        return null;
    }

    return (
        <div className={styles.paginacaoContainer}>
            <button 
                onClick={handleAnterior} 
                disabled={paginaAtual === 0}
                className={styles.botaoPaginacao}
            >
                Anterior
            </button>
            <span className={styles.infoPagina}>
                Página {paginaAtual + 1} de {totalPaginas}
            </span>
            <button 
                onClick={handleProxima} 
                disabled={paginaAtual >= totalPaginas - 1}
                className={styles.botaoPaginacao}
            >
                Próxima
            </button>
        </div>
    );
}

export default Paginacao;
