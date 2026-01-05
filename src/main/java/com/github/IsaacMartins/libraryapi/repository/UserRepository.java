package com.github.IsaacMartins.libraryapi.repository;

import com.github.IsaacMartins.libraryapi.model.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    UserEntity findByLogin(String login);
}
