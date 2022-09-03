package com.example.todo.bl;

import com.example.todo.dao.TodoEntity;
import com.example.todo.dao.repository.TodoRepository;
import com.example.todo.dto.TodoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
@Service
public class TodoBl {
    private Logger LOGGER = LoggerFactory.getLogger(TodoBl.class);
    private TodoRepository todoRepository;

    @Autowired
    public TodoBl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoEntity> getTodos() {
        LOGGER.info("BUSINESS-LOGIC: Iniciando consulta para obtener el listado de todo's");
        List<TodoEntity> todoList = this.todoRepository.findAll();
        LOGGER.info("BUSINESS-LOGIC: La consulta para obtener el listado de todo's retorno: {}", todoList);
        return todoList;
    }
    public TodoEntity getTodo(Integer id) {
        LOGGER.info("BUSINESS-LOGIC: Iniciando consulta para obtener un todo con id: {}", id);
        TodoEntity todo = this.todoRepository.findById(id).orElseThrow();
        LOGGER.info("BUSINESS-LOGIC: La consulta para obtener un todo con id: {} retorno: {}", todo);
        return todo;
    }

    @Transactional
    public TodoEntity saveTodo(TodoDto todoDto) {
        LOGGER.info("BUSINESS-LOGIC: Iniciando consulta para registrar un todo's");
        TodoEntity todoEntity = new TodoEntity(todoDto.getDescription(),
                new Date(), null, 1);
        TodoEntity todo = this.todoRepository.saveAndFlush(todoEntity);
        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
//        throw new RuntimeException("ERROR");
        LOGGER.info("BUSINESS-LOGIC: La consulta para registrar un todo's retorno: {}", todo);
        return todo;
    }

    public TodoEntity updateTodo(TodoDto todoDto) {
        LOGGER.info("BUSINESS-LOGIC: Iniciando consulta para actualizar un todo's");
        TodoEntity todoEntity = this.todoRepository.findById(todoDto.getId()).orElseThrow();
        todoEntity.setDescription(todoDto.getDescription());
        todoEntity.setUpdatedAt(new Date());
        TodoEntity todo = this.todoRepository.save(todoEntity);
        LOGGER.info("BUSINESS-LOGIC: La consulta para actualizar un todo retorno: {}", todo);
        return todo;
    }
    public String deleteTodo(Integer id) {
        LOGGER.info("BUSINESS-LOGIC: Iniciando consulta para eliminar un todo's");
        TodoEntity todo = this.todoRepository.findById(id).orElseThrow();
        this.todoRepository.delete(todo);
        return "OK";
    }
    public String getToken(String username){
        String secret ="TODO";
        List<GrantedAuthority> authorities=AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");

        String jwt= Jwts.builder()
        .setId("BACKENDTODO")
        .setSubject(username)
        .claim("authorities", authorities.stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis()+600000))
        .signWith(SignatureAlgorithm.HS512, secret.getBytes())
		.compact();

        return jwt;
    }

}
