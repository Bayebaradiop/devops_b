package com.medishop.todoapp.service;

import com.medishop.todoapp.dto.TaskRequestDTO;
import com.medishop.todoapp.dto.TaskResponseDTO;

public interface TaskService {

    TaskResponseDTO create(TaskRequestDTO request);
}
