package com.medishop.todoapp.controller;

import com.medishop.todoapp.dto.TaskRequestDTO;
import com.medishop.todoapp.dto.TaskResponseDTO;
import com.medishop.todoapp.service.TaskService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@Valid @RequestBody TaskRequestDTO request) {
        TaskResponseDTO creee = service.create(request);
        return ResponseEntity.created(URI.create("/api/tasks/" + creee.getId())).body(creee);
    }

    @GetMapping
    public List<TaskResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TaskResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public TaskResponseDTO update(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
