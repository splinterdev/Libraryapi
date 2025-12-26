package com.github.IsaacMartins.libraryapi.controller.dto.authorDTOs;

import java.time.LocalDate;
import java.util.UUID;

public record ResponseAuthorDTO(UUID id, String name, LocalDate birthDate, String nationality) {

}
