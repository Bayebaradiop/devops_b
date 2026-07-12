package com.medishop.todoapp.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Tache introuvable : " + id);
    }
}
