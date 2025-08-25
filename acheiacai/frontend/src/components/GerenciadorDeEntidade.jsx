import { useEffect, useState } from "react";
import Tabela from "./Tabela";
import FormularioProduto from "../components/FormularioProduto"; // 1. Importe o novo formulário

// Vamos assumir que você tem um ficheiro de serviço para a API


function GerenciadorDeEntidade({tipo, aba}) {

    const [items, setItems] = useState([]); 
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null); 

     const [exibirFormulario, setExibirFormulario] = useState(false);

     const colunasProdutos = [
        { header: 'ID', accessor: 'id' },
        { header: 'Nome', accessor: 'nome' },
        { header: 'Variação', accessor: 'variacao' },
        { header: 'Tamanho', accessor: 'tamanho' },
        {
            header: 'Preço (R$)',
            accessor: 'preco',
        }
    ];

    const colunasComplementos = [
        { header: 'ID', accessor: 'id' },
        { header: 'Nome', accessor: 'nome' },
        { header: 'Preço Adicional (R$)', accessor: 'preco_adicional' }
    ];

    const handleAlterar = (item) => {
        alert(`A alterar o item: ${item.nome} (ID: ${item.id})`);
        // No futuro, aqui é onde você abriria um modal ou formulário de edição
    };

    const handleSave = (item) => {
        
    }

    const handleExcluir = (itemId) => {
  
        if (window.confirm(`Tem a certeza de que deseja excluir o item com ID ${itemId}?`)) {
            console.log(`A excluir o item com ID: ${itemId}`);
        }
    };


    useEffect(() => {
        const fetchData = async () => {
           
            if (!tipo) {
                setLoading(false);
                return;
            }

            try {
                
                const apiUrl = `http://localhost:8080/achei-acai-api/${tipo}`;
                console.log(`Buscando dados de: ${apiUrl}`);

                const response = await fetch(apiUrl);

                if (!response.ok) {
                    throw new Error(`Erro Http! status: ${response.status}`);
                }

                const data = await response.json();
                setItems(data);

            } catch (error) {
                console.error("Erro ao buscar dados", error);
                setError(error.message); 
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [tipo]); // O useEffect irá rodar novamente se a prop 'tipo' mudar

    if (loading) {
        return <p>A carregar {aba}...</p>;
    }

    if (error) {
        return <p>Ocorreu um erro: {error}</p>;
    }

    return (
        <div className="gerenciador-entidade">
            <h1>Gestão de {aba}</h1>
            
            {/* 2. Adicione o botão de "Adicionar Novo" aqui, acima da tabela */}
            <div className="toolbar">
                <button onClick={() => setExibirFormulario(true)}>
                    Adicionar Novo {aba}
                </button>
            </div>

            {/* 3. Adicione a lógica de renderização condicional */}
            {/* Se 'exibirFormulario' for true, mostramos o formulário. Se for false, mostramos a tabela. */}
            {exibirFormulario ? (
                <FormularioProduto 
                    onSave={handleSave}
                    onCancel={() => setExibirFormulario(false)} 
                />
            ) : (
                // Se o formulário não estiver a ser exibido, mostramos a tabela
                <Tabela
                    columns={colunasProdutos} 
                    data={items} 
                    onAlterar={handleAlterar}
                    onExcluir={handleExcluir}
                />
            )}
        </div>
    );
}

export default GerenciadorDeEntidade;