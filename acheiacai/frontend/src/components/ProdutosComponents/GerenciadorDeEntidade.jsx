

import { useEffect, useState, useCallback } from "react";
import Tabela from "./Tabela";
import FormularioGenerico from "./FormularioGenerico"; 
import styles from './GerenciadorDeEntidade.module.css';


function GerenciadorDeEntidade({ servico, nomeDaEntidade, colunas }) {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [exibirFormulario, setExibirFormulario] = useState(false);
    const [itemParaEditar, setItemParaEditar] = useState(null); // Estado para controlar a edição

    // useCallback memoriza a função para evitar recriações desnecessárias
    const carregarDados = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            // Usa a função 'buscarTodos' do serviço que foi passado via props
            const dados = await servico.buscarTodos();
            setItems(dados);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, [servico]); // A função só é recriada se o 'servico' mudar

    useEffect(() => {
        carregarDados();
    }, [carregarDados]); // O useEffect agora depende da função 'carregarDados'

    const handleAdicionar = () => {
        setItemParaEditar(null); // Garante que não estamos a editar
        setExibirFormulario(true);
    };

    const handleAlterar = (item) => {
        setItemParaEditar(item); // Guarda o item a ser editado
        setExibirFormulario(true); // Abre o mesmo formulário, mas para edição
    };

    const handleExcluir = async (itemId) => {
        if (window.confirm(`Tem a certeza de que deseja excluir o ${nomeDaEntidade.toLowerCase()} com ID ${itemId}?`)) {
            try {
                await servico.deletar(itemId);
                carregarDados(); 
            } catch (err) {
                setError(`Falha ao excluir o item: ${err.message}`);
            }
        }
    };

    const handleSave = async (dadosDoFormulario) => {
        try {
            if (itemParaEditar) {
                await servico.atualizar(itemParaEditar.id, dadosDoFormulario);
            } else {
                await servico.criar(dadosDoFormulario);
            }
            setExibirFormulario(false);
            setItemParaEditar(null);
            carregarDados(); // Recarrega os dados após salvar
        } catch (err) {
            setError(`Falha ao salvar o item: ${err.message}`);
        }
    };

    if (loading) return <p>A carregar {nomeDaEntidade.toLowerCase()}...</p>;
    if (error) return <p>Ocorreu um erro: {error}</p>;

    return (
        <div className={styles.gerenciadorEntidade}>
            <div className={styles.header}>
                <h1>Gestão de {nomeDaEntidade}</h1>
                
                <div className={styles.toolbar}>
                    {!exibirFormulario && (
                        <button className={styles.botaoAdicionar} onClick={handleAdicionar}>
                            Adicionar {nomeDaEntidade}
                        </button>
                    )}
                </div>
            </div>


            {exibirFormulario ? (
                <FormularioGenerico
                    dadosIniciais={itemParaEditar}
                    onSave={handleSave}
                    onCancel={() => {
                        setExibirFormulario(false);
                        setItemParaEditar(null);
                    }}
                    colunas={colunas} // Passa a definição das colunas para o formulário se construir
                    nomeDaEntidade={nomeDaEntidade}
                />
            ) : (
                <Tabela 
                    columns={colunas}
                    data={items} 
                    onAlterar={handleAlterar}
                    onExcluir={handleExcluir}
                    nomeDaEntidade={nomeDaEntidade}
                />
            )}
        </div>
    );
}

export default GerenciadorDeEntidade;