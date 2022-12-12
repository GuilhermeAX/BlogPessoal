package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

        @Autowired
        private TestRestTemplate testRestTemplate;

        @Autowired
        private UsuarioService usuarioService;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @BeforeAll
        void start() {
                usuarioRepository.deleteAll();

                usuarioService.cadastrarUsuario(new Usuario(0L,
                                "Root", "root@root.com", "rootroot", " "));
        }

        @Test
        @DisplayName("Cadastrar um Usuario")
        public void deveCriarUmUsuario() {
                HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
                                "Paulo Antunes", "paulo.a@gmail.com", "12345678", " "));

                ResponseEntity<Usuario> corpoResposta = testRestTemplate
                                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

                assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
                assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
                assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
        }

        @Test
        @DisplayName("Listar todos os usuários")
        public void deveListarTodosUsuarios() {
                usuarioService.cadastrarUsuario(new Usuario(0L,
                                "Sabrina", "sabrina@gmail.com", "sa123456", " "));
                usuarioService.cadastrarUsuario(new Usuario(0L,
                                "Roberto", "Roberto@gmail.com", "ro123456", " "));

                ResponseEntity<String> resposta = testRestTemplate
                                .withBasicAuth("root@root.com", "rootroot")
                                .exchange("/usuarios/listartodos", HttpMethod.GET, null, String.class);

                assertEquals(HttpStatus.OK, resposta.getStatusCode());
        }

        @Test
        @DisplayName("Atualizar um Usuário")
        public void deveAtualizarUmUsuario() {

                Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
                                "Juliana Andrews", "juliana_andrews1@email.com.br", "juliana123",
                                "https://i.imgur.com/yDRVeK7.jpg"));

                System.out.println("\nId: " + usuarioCadastrado.get().getId());

                Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
                                "Juliana Andrews Ramos", "juliana_ramos1@email.com.br", "juliana123",
                                "https://i.imgur.com/yDRVeK7.jpg");

                HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

                ResponseEntity<Usuario> corpoResposta = testRestTemplate
                                .withBasicAuth("root@root.com", "rootroot")
                                .exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

                assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
                assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
                assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
        }

        @Test
        @DisplayName("Não deve permitir duplicação de Usuário")
        public void naoDeveDuplicarUsuario() {
                usuarioService.cadastrarUsuario(new Usuario(0L,
                                "Maria da Silva", "maria_silva@gmail.com", "12345678", ""));

                HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
                                "Maria da Silva", "maria_silva@gmail.com", "12345678", ""));

                ResponseEntity<Usuario> corpoResposta = testRestTemplate
                                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

                assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
        }

        @Test
        @DisplayName("Buscar Usuário pelo ID")
        public void deveBuscarUsuarioPorId() {
                usuarioService.cadastrarUsuario(new Usuario(0L,
                                "Juliana Andrews", "juliana_andrews99@email.com.br", "juliana123", ""));

                ResponseEntity<String> resposta = testRestTemplate
                                .withBasicAuth("root@root.com", "rootroot")
                                .exchange("/usuarios/buscarporid/1", HttpMethod.GET, null, String.class);

                assertEquals(HttpStatus.OK, resposta.getStatusCode());
        }

        @Test
        @DisplayName("Fazer login")
        public void fazerLogin() {

                usuarioService.cadastrarUsuario(new Usuario(0L,
                                "Admin", "admin@root.com", "rootroot", " "));

                Optional<UsuarioLogin> usuarioLogin = Optional.of(new UsuarioLogin("admim@root.com", "rootroot"));
                usuarioService.autenticarUsuario(usuarioLogin);

                HttpEntity<UsuarioLogin> corpoRequisicao = new HttpEntity<UsuarioLogin>(
                                new UsuarioLogin("root@root.com",
                                                "rootroot"));

                ResponseEntity<UsuarioLogin> resposta = testRestTemplate.withBasicAuth("admin@root.com", "rootroot")
                                .exchange("/usuarios/logar", HttpMethod.POST, corpoRequisicao, UsuarioLogin.class);

                assertEquals(HttpStatus.OK, resposta.getStatusCode());
        }
}
