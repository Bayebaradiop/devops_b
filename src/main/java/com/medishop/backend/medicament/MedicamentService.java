package com.medishop.backend.medicament;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MedicamentService {

    private final MedicamentRepository repository;

    public MedicamentService(MedicamentRepository repository) {
        this.repository = repository;
    }

    public List<Medicament> lister(String nom) {
        if (nom == null || nom.isBlank()) {
            return repository.findAll();
        }
        return repository.findByNomContainingIgnoreCase(nom);
    }

    public Medicament parId(Long id) {
        return repository.findById(id).orElseThrow(() -> new MedicamentNotFoundException(id));
    }

    @Transactional
    public Medicament creer(Medicament medicament) {
        medicament.setId(null);
        return repository.save(medicament);
    }

    @Transactional
    public Medicament modifier(Long id, Medicament data) {
        Medicament existant = parId(id);
        existant.setNom(data.getNom());
        existant.setDescription(data.getDescription());
        existant.setPrix(data.getPrix());
        existant.setStock(data.getStock());
        existant.setSurOrdonnance(data.isSurOrdonnance());
        return repository.save(existant);
    }

    @Transactional
    public void supprimer(Long id) {
        if (!repository.existsById(id)) {
            throw new MedicamentNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
