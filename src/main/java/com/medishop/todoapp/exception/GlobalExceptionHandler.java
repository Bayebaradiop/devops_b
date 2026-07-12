package com.medishop.todoapp.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404 : la tache demandee n'existe pas. */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(TaskNotFoundException ex, HttpServletRequest requete) {
        ApiError erreur = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                requete.getRequestURI(),
                null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erreur);
    }

    /** 400 : le DTO recu ne respecte pas les contraintes de validation. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest requete) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(champ -> champ.getField() + " : " + champ.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError erreur = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Donnees invalides",
                requete.getRequestURI(),
                details);
        return ResponseEntity.badRequest().body(erreur);
    }

    /** 400 : JSON illisible ou valeur d'enum inconnue (status inexistant). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest requete) {
        ApiError erreur = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Corps de requete illisible ou valeur de statut invalide (attendu : TODO, IN_PROGRESS ou DONE)",
                requete.getRequestURI(),
                null);
        return ResponseEntity.badRequest().body(erreur);
    }

    /**
     * 404 : URL inconnue. Sans ce handler, le filet de securite Exception.class
     * ci-dessous transformerait ces requetes en 500.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException ex, HttpServletRequest requete) {
        ApiError erreur = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Ressource inexistante",
                requete.getRequestURI(),
                null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erreur);
    }

    /** 500 : filet de securite. Le detail reste dans les logs, jamais expose au client. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest requete) {
        log.error("Erreur inattendue sur {}", requete.getRequestURI(), ex);

        ApiError erreur = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Une erreur interne est survenue",
                requete.getRequestURI(),
                null);
        return ResponseEntity.internalServerError().body(erreur);
    }
}
