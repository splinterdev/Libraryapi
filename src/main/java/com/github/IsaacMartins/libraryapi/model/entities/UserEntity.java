package com.github.IsaacMartins.libraryapi.model.entities;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_user")
@Data
public class UserEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String login;

    @Column
    private String password;

    @Column
    private String email;

    // Conversão do tipo list para o tipo array de varchar (varchar[]) do banco
    @Type(ListArrayType.class)
    @Column(columnDefinition = "varchar[]")
    private List<String> roles;
}
