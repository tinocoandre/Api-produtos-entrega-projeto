
package br.edu.pos.apiprodutos.controller;

import br.edu.pos.apiprodutos.model.Produto;
import br.edu.pos.apiprodutos.service.ProdutoService;
import br.edu.pos.apiprodutos.dto.MovimentacaoEstoque;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.http.HttpStatus;

import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiResponse;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiParam;

import java.util.NoSuchElementException;

public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    public void registrarRotas(Javalin app) {

        app.get("/produtos", this::listarProdutos);

        app.get("/produtos/estoque-baixo", this::listarEstoqueBaixo);

        app.get("/produtos/{id}", this::buscarProduto);

        app.post("/produtos", this::cadastrarProduto);

        app.post("/produtos/{id}/estoque/entrada", this::adicionarEstoque);

        app.post("/produtos/{id}/estoque/saida", this::retirarEstoque);

        app.put("/produtos/{id}", this::atualizarProduto);

        app.delete("/produtos/{id}", this::excluirProduto);

        app.exception(NoSuchElementException.class, (erro, ctx) -> {
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.json(new MensagemErro(erro.getMessage()));
        });


        app.exception(IllegalArgumentException.class, (erro, ctx) -> {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(new MensagemErro(erro.getMessage()));
        });
    }

    @OpenApi(
            path = "/produtos",
            methods = {HttpMethod.GET},
            summary = "Listar todos os produtos",
            tags = {"Produtos"},
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            content = {
                                    @OpenApiContent(from = Produto[].class)
                            }
                    )
            }
    )
    public void listarProdutos(Context ctx) {
        ctx.json(service.listar());
    }
    

        @OpenApi(
                path = "/produtos/estoque-baixo",
                methods = {HttpMethod.GET},
                summary = "Listar produtos com estoque baixo",
                tags = {"Estoque"},
                queryParams = {
                        @OpenApiParam(
                                name = "limite",
                                type = Integer.class,
                                description = "Quantidade máxima de estoque",
                                required = true
                        )
                },
                responses = {
                        @OpenApiResponse(
                                status = "200",
                                content = {
                                        @OpenApiContent(from = Produto[].class)
                                }
                        ),
                        @OpenApiResponse(status = "400")
                }
        )
        public void listarEstoqueBaixo(Context ctx) {

        int limite = Integer.parseInt(ctx.queryParam("limite"));

        ctx.json(service.listarEstoqueBaixo(limite));
    }


    @OpenApi(
            path = "/produtos/{id}",
            methods = {HttpMethod.GET},
            summary = "Buscar produto por ID",
            tags = {"Produtos"},
            pathParams = {
                    @OpenApiParam(
                            name = "id",
                            type = Long.class,
                            description = "Identificador do produto",
                            required = true
                    )
            },
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            content = {
                                    @OpenApiContent(from = Produto.class)
                            }
                    ),
                    @OpenApiResponse(status = "404")
            }
    )
    public void buscarProduto(Context ctx) {

        Long id = Long.parseLong(ctx.pathParam("id"));

        Produto produto = service.buscarPorId(id);

        ctx.json(produto);
    }


    @OpenApi(
            path = "/produtos",
            methods = {HttpMethod.POST},
            summary = "Cadastrar um novo produto",
            tags = {"Produtos"},
            requestBody = @OpenApiRequestBody(
                    content = {
                            @OpenApiContent(from = Produto.class)
                    }
            ),
            responses = {
                    @OpenApiResponse(
                            status = "201",
                            content = {
                                    @OpenApiContent(from = Produto.class)
                            }
                    ),
                    @OpenApiResponse(status = "400")
            }
    )
    public void cadastrarProduto(Context ctx) {

        Produto dados = ctx.bodyAsClass(Produto.class);

        Produto produto = service.cadastrar(
                dados.getNome(),
                dados.getPreco(),
                dados.getQuantidadeEstoque()
        );

        ctx.status(HttpStatus.CREATED);
        ctx.json(produto);
    }
    

        @OpenApi(
        path = "/produtos/{id}/estoque/entrada",
        methods = {HttpMethod.POST},
        summary = "Adicionar unidades ao estoque",
        tags = {"Estoque"},
        pathParams = {
                @OpenApiParam(
                name = "id",
                type = Long.class,
                description = "Identificador do produto",
                required = true
                )
        },
        requestBody = @OpenApiRequestBody(
                content = {
                @OpenApiContent(from = MovimentacaoEstoque.class)
                }
        ),
        responses = {
                @OpenApiResponse(
                status = "200",
                content = {
                        @OpenApiContent(from = Produto.class)
                }
                ),
                @OpenApiResponse(status = "400"),
                @OpenApiResponse(status = "404")
        }
        )
        public void adicionarEstoque(Context ctx) {

        Long id = Long.parseLong(ctx.pathParam("id"));

        MovimentacaoEstoque dados =
                ctx.bodyAsClass(MovimentacaoEstoque.class);

        Produto produto = service.adicionarEstoque(
                id,
                dados.getQuantidade()
        );

        ctx.json(produto);
    } 


        @OpenApi(
                path = "/produtos/{id}/estoque/saida",
                methods = {HttpMethod.POST},
                summary = "Retirar unidades do estoque",
                tags = {"Estoque"},
                pathParams = {
                        @OpenApiParam(
                                name = "id",
                                type = Long.class,
                                description = "Identificador do produto",
                                required = true
                        )
                },
                requestBody = @OpenApiRequestBody(
                        content = {
                                @OpenApiContent(from = MovimentacaoEstoque.class)
                        }
                ),
                responses = {
                        @OpenApiResponse(
                                status = "200",
                                content = {
                                        @OpenApiContent(from = Produto.class)
                                }
                        ),
                        @OpenApiResponse(status = "400"),
                        @OpenApiResponse(status = "404")
                }
        )
        public void retirarEstoque(Context ctx) {

        Long id = Long.parseLong(ctx.pathParam("id"));

        MovimentacaoEstoque dados =
                ctx.bodyAsClass(MovimentacaoEstoque.class);

        Produto produto = service.retirarEstoque(
                id,
                dados.getQuantidade()
        );

        ctx.json(produto);
    }


    @OpenApi(
            path = "/produtos/{id}",
            methods = {HttpMethod.PUT},
            summary = "Atualizar um produto existente",
            tags = {"Produtos"},
            pathParams = {
                    @OpenApiParam(
                            name = "id",
                            type = Long.class,
                            description = "Identificador do produto",
                            required = true
                    )
            },
            requestBody = @OpenApiRequestBody(
                    content = {
                            @OpenApiContent(from = Produto.class)
                    }
            ),
            responses = {
                    @OpenApiResponse(
                            status = "200",
                            content = {
                                    @OpenApiContent(from = Produto.class)
                            }
                    ),
                    @OpenApiResponse(status = "400"),
                    @OpenApiResponse(status = "404")
            }
    )
    public void atualizarProduto(Context ctx) {

        Long id = Long.parseLong(ctx.pathParam("id"));

        Produto dados = ctx.bodyAsClass(Produto.class);

        Produto atualizado = service.atualizar(
                id,
                dados.getNome(),
                dados.getPreco()
        );

        ctx.json(atualizado);
    }


    @OpenApi(
            path = "/produtos/{id}",
            methods = {HttpMethod.DELETE},
            summary = "Excluir um produto",
            tags = {"Produtos"},
            pathParams = {
                    @OpenApiParam(
                            name = "id",
                            type = Long.class,
                            description = "Identificador do produto",
                            required = true
                    )
            },
            responses = {
                    @OpenApiResponse(status = "204"),
                    @OpenApiResponse(status = "404")
            }
    )
    public void excluirProduto(Context ctx) {

        Long id = Long.parseLong(ctx.pathParam("id"));

        service.excluir(id);

        ctx.status(HttpStatus.NO_CONTENT);
    }

    
    public record MensagemErro(String mensagem) {
    }
}
