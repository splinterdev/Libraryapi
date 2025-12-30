package com.github.IsaacMartins.libraryapi.controller.mappers;

import com.github.IsaacMartins.libraryapi.controller.dto.authorDTOs.AuthorWithoutIdDTO;
import com.github.IsaacMartins.libraryapi.controller.dto.authorDTOs.AuthorWithIdDTO;
import com.github.IsaacMartins.libraryapi.model.entities.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") //componentModel faz com que a interface de mapeamento seja um component gerenciado pelo spring
public interface AuthorMapper {

    Author toEntity(AuthorWithoutIdDTO authorDTO);
    AuthorWithoutIdDTO toAuthorWithoutIdDTO(Author author);
    AuthorWithIdDTO toAuthorWithIdDTO(Author author);
}
