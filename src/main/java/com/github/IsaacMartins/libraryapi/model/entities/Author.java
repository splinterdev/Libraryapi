package com.github.IsaacMartins.libraryapi.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Data
@ToString(exclude = "books")
@EntityListeners(AuditingEntityListener.class) // Crio um auditor (observador) da entidade autor passando a classe AuditingEntityListener do JPA. Permite que as annotations de data sejam preenchidas automaticamente
public class Author {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "birth_date", length = 50, nullable = false)
    private LocalDate birthDate;

    @Column(length = 50, nullable = false)
    private String nationality;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Book> books;

    @CreatedDate //JPA registra automaticamente quando for criado uma entidade autor
    @Column(name = "register_date")
    private LocalDateTime registerDate;

    @LastModifiedDate //JPA registra automaticamente quando for alterada uma entidade autor
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "user_id")
    private UUID userId;
}
