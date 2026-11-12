
package br.edu.pos.apiprodutos.repository;

import br.edu.pos.apiprodutos.model.Produto;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {

    void salvar(Produto produto);

    List<Produto> listarTodos();

    Optional<Produto> buscarPorId(Long id);

    void excluir(Long id);

    // Operações de estoque

    int adicionarEstoque(Long id, int quantidade);

    int retirarEstoque(Long id, int quantidade);

    List<Produto> listarEstoqueBaixo(int limite);
}
