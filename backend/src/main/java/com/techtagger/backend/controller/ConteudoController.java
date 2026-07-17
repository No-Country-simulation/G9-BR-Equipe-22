package com.techtagger.backend.controller;

import com.techtagger.backend.dto.request.ConteudoRequest;
import com.techtagger.backend.dto.response.ConteudoResponse;
import com.techtagger.backend.model.Conteudo;
import com.techtagger.backend.service.ConteudoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conteudo")
public class ConteudoController {

    private final ConteudoService service;

    public ConteudoController(ConteudoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConteudoResponse> processar(@Valid @RequestBody ConteudoRequest request) {
        return ResponseEntity.ok(service.processar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConteudoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Conteudo>> listarPorCategoria(
            @RequestParam(required = false) String categoria) {
        if (categoria != null) {
            return ResponseEntity.ok(service.buscarPorCategoria(categoria));
        }
        return ResponseEntity.ok(List.of());
    }
}