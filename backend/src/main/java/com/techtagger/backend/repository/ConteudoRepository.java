package com.techtagger.backend.repository;


import com.techtagger.backend.model.Conteudo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {

    Page<Conteudo> findByCategoria(String categoria, Pageable pageable);
}
