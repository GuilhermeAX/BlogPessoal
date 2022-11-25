package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.model.Tema;
import com.generation.blogpessoal.repository.TemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tema")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TemaController  {
    @Autowired
    private TemaRepository temaRepository; // Injeção de dependencia


    @GetMapping
    public ResponseEntity<List<Tema>> getAll() {
        return ResponseEntity.ok(temaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tema> getById(@PathVariable Long id) {

        return temaRepository.findById(id)
                .map(resposta -> ResponseEntity.ok(resposta))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/descricao/{descricao}")
    public ResponseEntity<List<Tema>> getByDescricao(@PathVariable String descricao) {
        return ResponseEntity.ok(temaRepository.findAllByDescricaoContainingIgnoreCase(descricao));
    }

    @PostMapping
    public ResponseEntity<Tema> postTema(@Valid @RequestBody Tema tema) {
        return ResponseEntity.status(HttpStatus.CREATED).body(temaRepository.save(tema));
    }

    @PutMapping
    public ResponseEntity<Tema> putTema(@Valid @RequestBody Tema tema) {
        if (tema.getId() == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.status(HttpStatus.OK).body(temaRepository.save(tema));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTema(@PathVariable Long id) {
        try {
            temaRepository.deleteById(id);
            return ResponseEntity.status(204).build();

        }catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
