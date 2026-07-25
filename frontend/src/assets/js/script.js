
        // Simulação de Banco de Dados Local (Array em memória)
        let conteudos = [
            { id: 1, titulo: "Análise de Logs de Servidor", categoria: "Relatório", descricao: "Processamento de logs de acesso do mês de Maio.", data: "2026-06-01" },
            { id: 2, titulo: "Base de Clientes Ativos", categoria: "Dataset", descricao: "Higienização e normalização de CPFs e e-mails.", data: "2026-06-05" }
        ];

        // Elementos do DOM
        const formCadastro = document.getElementById('formCadastro');
        const inputBusca = document.getElementById('inputBusca');
        const tabelaCorpo = document.getElementById('tabelaCorpo');
        const estadoVazio = document.getElementById('estadoVazio');

        // Função para renderizar a tabela
        function renderizarTabela(dados) {
            tabelaCorpo.innerHTML = '';
            
            if (dados.length === 0) {
                estadoVazio.style.display = 'block';
                return;
            } else {
                estadoVazio.style.display = 'none';
            }

            dados.forEach(item => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>#${item.id}</td>
                    <td><strong>${item.titulo}</strong></td>
                    <td><span class="badge">${item.categoria}</span></td>
                    <td>${item.descricao || 'Sem descrição'}</td>
                    <td>${item.data}</td>
                `;
                tabelaCorpo.appendChild(tr);
            });
        }

        // Evento de Cadastro
        formCadastro.addEventListener('submit', function(e) {
            e.preventDefault();

            const titulo = document.getElementById('titulo').value;
            const categoria = document.getElementById('categoria').value;
            const descricao = document.getElementById('descricao').value;
            const dataAtual = new Date().toISOString().split('T')[0];

            const novoItem = {
                id: conteudos.length > 0 ? conteudos[conteudos.length - 1].id + 1 : 1,
                titulo,
                categoria,
                descricao,
                data: dataAtual
            };

            conteudos.push(novoItem);
            formCadastro.reset();
            renderizarTabela(conteudos);
        });

        // Evento de Busca / Filtro em tempo real
        inputBusca.addEventListener('input', function(e) {
            const termo = e.target.value.toLowerCase();
            
            const filtrados = conteudos.filter(item => 
                item.titulo.toLowerCase().includes(termo) || 
                item.categoria.toLowerCase().includes(termo)
            );

            renderizarTabela(filtrados);
        });

        // Inicialização da página
        renderizarTabela(conteudos);
    