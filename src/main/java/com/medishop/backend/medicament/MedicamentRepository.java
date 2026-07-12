package com.medishop.backend.medicament;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    List<Medicament> findByNomContainingIgnoreCase(String nom);
}
