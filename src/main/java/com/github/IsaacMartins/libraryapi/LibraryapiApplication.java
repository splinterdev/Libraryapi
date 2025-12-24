package com.github.IsaacMartins.libraryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //Permite que as anotation de resgistro automático possam ser usadas. A anotation @EnableJpaAuditing também pode ser adicionada em uma classe configuration
public class LibraryapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryapiApplication.class, args);
	}

}
