package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

    @Autowired
    private PostagemRepository postagemRepository; // Injeção de dependencia
    @Autowired
    private TemaRepository temaRepository; // Injeção de dependencia


    @GetMapping
    public ResponseEntity<List<Postagem>> getAll() {
        return ResponseEntity.ok(postagemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Postagem> getById(@PathVariable Long id) {
/*        Optional<Postagem> buscaPostagem = postagemRepository.findById(id);
         if(buscaPostagem.isPresent())
            return ResponseEntity.ok(buscaPostagem.get());
        else return ResponseEntity.notFound().build();

        //utilizando lambda function
        return buscaPostagem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); */

        return postagemRepository.findById(id)
                .map(resposta -> ResponseEntity.ok(resposta))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo) {
        return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
    }

    @PostMapping
    public ResponseEntity<Postagem> postPostagem(@Valid @RequestBody Postagem postagem) {
        if ((temaRepository.existsById(postagem.getTema().getId())))
            return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping
    public ResponseEntity<Postagem> putPostagem(@Valid @RequestBody Postagem postagem) {
        if ((temaRepository.existsById(postagem.getTema().getId()))) {
            if (postagem.getId() == null)
                return ResponseEntity.notFound().build();
            else
                return ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem));
        } else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostagem(@PathVariable Long id) {
        try {
            postagemRepository.deleteById(id);
            return ResponseEntity.status(204).build();

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


}
