package com.medishop.todoapp.service;

import com.medishop.todoapp.dto.TaskRequestDTO;
import com.medishop.todoapp.dto.TaskResponseDTO;
import java.util.List;

public interface TaskService {

    TaskResponseDTO create(TaskRequestDTO request);

    List<TaskResponseDTO> findAll();

    TaskResponseDTO findById(Long id);
}
