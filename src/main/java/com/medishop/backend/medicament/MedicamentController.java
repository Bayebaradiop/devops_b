package com.medishop.backend.medicament;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicaments")
public class MedicamentController {

    private final MedicamentService service;

    public MedicamentController(MedicamentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Medicament> lister(@RequestParam(required = false) String nom) {
        return service.lister(nom);
    }

    @GetMapping("/{id}")
    public Medicament parId(@PathVariable Long id) {
        return service.parId(id);
    }

    @PostMapping
    public ResponseEntity<Medicament> creer(@Valid @RequestBody Medicament medicament) {
        Medicament cree = service.creer(medicament);
        return ResponseEntity.created(URI.create("/api/medicaments/" + cree.getId())).body(cree);
    }

    @PutMapping("/{id}")
    public Medicament modifier(@PathVariable Long id, @Valid @RequestBody Medicament medicament) {
        return service.modifier(id, medicament);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        service.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
