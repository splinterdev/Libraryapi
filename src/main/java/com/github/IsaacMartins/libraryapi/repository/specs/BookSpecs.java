package com.github.IsaacMartins.libraryapi.repository.specs;

import com.github.IsaacMartins.libraryapi.model.entities.Book;
import com.github.IsaacMartins.libraryapi.model.entities.BookGenre;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecs {

    public static Specification<Book> isbnEqual(String isbn) {
        return (root, query, cb) -> cb.equal(root.get("isbn"), isbn);
    }

    public static Specification<Book> titleLike(String title) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"); // like %title%
    }

public static Specification<Book> authorNameLike(String name) {
    return (root, query, cb) -> {
        // Existem duas maneiras de fazer a busca de um nome do autor do livro com like:

        // Maneira 1: criando um novo Root (representação / projeção da tabela do banco de dados) de um join das duas tabelas,
        // onde o join pode ser definido em código como um enum, sendo ideal para querys onde precisam de um join específico:
        Join<Object, Object> authorJoin = root.join("author", JoinType.INNER);
        return cb.like(cb.lower(authorJoin.get("name")), "%" +  name.toLowerCase() + "%");

        //Maneira 2: criando uma consulta a partir do mesmo root da tabela livros, onde o hibernate irá fazer um inner join por padrão.
        // Ideal para situações onde o tipo de join não precisa ser específico:

        // return cb.like(cb.lower(root.get("author").get("name")), "%" +  name.toLowerCase() + "%"));

        // root presenta a tabela do banco de dados, tratando os dados como a entidade mapeada,
        // ou seja - considera apenas os atributos definidos no mapeamento. A partir disso, fazemos o .get("author"),
        // que é a composição da entidade autor em livro, com um .get("name") para acessar o nome da entidade dentro do join com livro.
    };
}

    public static Specification<Book> genreEqual(BookGenre genre) {
        return (root, query, cb) -> cb.equal(root.get("genre"), genre);
    }

    public static Specification<Book> releaseYearEqual(Integer releaseYear) {
//      and to_char(release_date, 'YYYY') from book;
        return (root, query, cb) ->
                cb.equal( cb.function("to_char", String.class, root.get("releaseDate"), cb.literal("YYYY")), releaseYear.toString());
    }
}
