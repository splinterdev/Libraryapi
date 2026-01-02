package com.github.IsaacMartins.libraryapi.controller.mappers;

import com.github.IsaacMartins.libraryapi.controller.dto.bookDTOs.BookWithoutIdDTO;
import com.github.IsaacMartins.libraryapi.controller.dto.bookDTOs.BookWithIdDTO;
import com.github.IsaacMartins.libraryapi.model.entities.Book;
import com.github.IsaacMartins.libraryapi.repository.AuthorRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class BookMapper {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    AuthorMapper authorMapper;

    @Mapping(target = "author", expression = "java(authorRepository.findById(bookDTO.authorId()).orElse(null))")
    public abstract Book toEntity(BookWithoutIdDTO bookDTO);

    @Mapping(target = "author", expression = "java(authorMapper.toAuthorWithoutIdDTO(book.getAuthor()))")
    public abstract BookWithIdDTO toBookWithIdDTO(Book book);
}
