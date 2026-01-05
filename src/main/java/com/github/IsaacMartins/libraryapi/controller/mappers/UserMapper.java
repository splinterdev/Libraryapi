package com.github.IsaacMartins.libraryapi.controller.mappers;

import com.github.IsaacMartins.libraryapi.controller.dto.userDTOs.UserDTO;
import com.github.IsaacMartins.libraryapi.model.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserDTO userDTO);
}
