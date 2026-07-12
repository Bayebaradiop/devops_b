package com.medishop.todoapp.controller;

import com.medishop.todoapp.dto.TaskRequestDTO;
import com.medishop.todoapp.dto.TaskResponseDTO;
import com.medishop.todoapp.service.TaskService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
