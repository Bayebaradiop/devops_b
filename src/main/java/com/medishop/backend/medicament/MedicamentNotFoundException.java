package com.medishop.backend.medicament;

public class MedicamentNotFoundException extends RuntimeException {

    public MedicamentNotFoundException(Long id) {
        super("Medicament introuvable : " + id);
    }
}
