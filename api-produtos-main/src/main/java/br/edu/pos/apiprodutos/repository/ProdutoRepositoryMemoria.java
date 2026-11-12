package br.edu.pos.apiprodutos.repository;

import br.edu.pos.apiprodutos.model.Produto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoRepositoryMemoria implements ProdutoRepository {

    private final List<Produto> produtos = new ArrayList<>();

    @Override
    public void salvar(Produto produto) {

        produtos.removeIf(p -> p.getId().equals(produto.getId()));

        produtos.add(produto);
    }

    @Override
    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {

        return produtos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public void excluir(Long id) {

        produtos.removeIf(p -> p.getId().equals(id));
    }

    @Override
    public int adicionarEstoque(Long id, int quantidade) {

    Optional<Produto> encontrado = buscarPorId(id);

    if (encontrado.isEmpty()) {
        return 0;
    }

    Produto produto = encontrado.get();

    produto.setQuantidadeEstoque(
            produto.getQuantidadeEstoque() + quantidade
    );

    return 1;
    }

    @Override
    public int retirarEstoque(Long id, int quantidade) {

    Optional<Produto> encontrado = buscarPorId(id);

    if (encontrado.isEmpty()) {
        return 0;
    }

    Produto produto = encontrado.get();

    if (produto.getQuantidadeEstoque() < quantidade) {
        return 0;
    }

    produto.setQuantidadeEstoque(
            produto.getQuantidadeEstoque() - quantidade
    );

        return 1;
    }

    @Override
    public List<Produto> listarEstoqueBaixo(int limite) {

    return produtos.stream()
            .filter(p -> p.getQuantidadeEstoque() <= limite)
            .toList();
    }
    
}