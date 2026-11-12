
package br.edu.pos.apiprodutos.service;

import br.edu.pos.apiprodutos.model.Produto;
import br.edu.pos.apiprodutos.repository.ProdutoRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class ProdutoService {

    private final ProdutoRepository repository;

    // Injeção de dependência pelo construtor
    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    // Cadastro de produto com estoque inicial
    public Produto cadastrar(String nome, double preco) {
        return cadastrar(nome, preco, 0);
    }

    public Produto cadastrar(
            String nome,
            double preco,
            int quantidadeEstoque
    ) {

        validarProduto(nome, preco);
        validarQuantidadeEstoque(quantidadeEstoque);

        Produto produto = new Produto(
                null,
                nome,
                preco,
                quantidadeEstoque
        );

        repository.salvar(produto);

        return produto;
    }

    public List<Produto> listar() {
        return repository.listarTodos();
    }

    public Produto buscarPorId(Long id) {

        return repository.buscarPorId(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Produto não encontrado: " + id
                        )
                );
    }

    public Produto atualizar(Long id, String nome, double preco) {

        validarProduto(nome, preco);

        Produto produto = buscarPorId(id);

        produto.setNome(nome);
        produto.setPreco(preco);

        repository.salvar(produto);

        return produto;
    }

    public void excluir(Long id) {

        buscarPorId(id);

        repository.excluir(id);
    }

    // Entrada de estoque
    public Produto adicionarEstoque(Long id, int quantidade) {

        validarQuantidadeMovimentacao(quantidade);

        int resultado = repository.adicionarEstoque(id, quantidade);

        if (resultado == 0) {
            buscarPorId(id);
        }

        return buscarPorId(id);
    }

    // Saída de estoque
    public Produto retirarEstoque(Long id, int quantidade) {

        validarQuantidadeMovimentacao(quantidade);

        int resultado = repository.retirarEstoque(id, quantidade);

        if (resultado == 0) {

            // Verifica se o produto realmente existe.
            Produto produto = buscarPorId(id);

            // Se existe, mas não conseguiu retirar,
            // significa que o estoque é insuficiente.
            throw new IllegalArgumentException(
                    "Estoque insuficiente. Disponível: "
                            + produto.getQuantidadeEstoque()
            );
        }

        return buscarPorId(id);
    }

    // Consulta de produtos com estoque baixo
    public List<Produto> listarEstoqueBaixo(int limite) {

        if (limite < 0) {
            throw new IllegalArgumentException(
                    "O limite de estoque não pode ser negativo."
            );
        }

        return repository.listarEstoqueBaixo(limite);
    }

    private void validarQuantidadeMovimentacao(int quantidade) {

        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade da movimentação deve ser maior que zero."
            );
        }
    }

    private void validarQuantidadeEstoque(int quantidadeEstoque) {

        if (quantidadeEstoque < 0) {
            throw new IllegalArgumentException(
                    "O estoque inicial não pode ser negativo."
            );
        }
    }

    private void validarProduto(String nome, double preco) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do produto é obrigatório."
            );
        }

        if (!Double.isFinite(preco) || preco <= 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser maior que zero."
            );
        }
    }
}
