package com.medishop.todoapp.service;

import com.medishop.todoapp.dto.TaskRequestDTO;
import com.medishop.todoapp.dto.TaskResponseDTO;
import com.medishop.todoapp.exception.TaskNotFoundException;
import com.medishop.todoapp.model.Task;
import com.medishop.todoapp.model.TaskStatus;
import com.medishop.todoapp.repository.TaskRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public TaskResponseDTO create(TaskRequestDTO request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);
        return TaskResponseDTO.depuis(repository.save(task));
    }

    @Override
    public List<TaskResponseDTO> findAll() {
        // Les plus recentes d'abord : c'est l'ordre attendu par le front
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(TaskResponseDTO::depuis)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO findById(Long id) {
        return TaskResponseDTO.depuis(chercher(id));
    }

    @Override
    @Transactional
    public TaskResponseDTO update(Long id, TaskRequestDTO request) {
        Task task = chercher(id);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        // Statut absent du corps : on conserve celui deja en base
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        // saveAndFlush et non save : @PreUpdate ne se declenche qu'au flush. Avec un simple
        // save(), le flush aurait lieu au commit, apres la construction du DTO, et la reponse
        // renverrait un updatedAt perime alors que la base contient la bonne valeur.
        return TaskResponseDTO.depuis(repository.saveAndFlush(task));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // On passe par chercher() pour renvoyer un 404 explicite plutot que
        // l'exception technique de deleteById sur un id absent
        repository.delete(chercher(id));
    }

    private Task chercher(Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}
