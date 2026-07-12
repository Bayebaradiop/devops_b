package com.medishop.backend.medicament;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medicaments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nom;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @PositiveOrZero(message = "Le prix ne peut pas etre negatif")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @NotNull(message = "Le stock est obligatoire")
    @PositiveOrZero(message = "Le stock ne peut pas etre negatif")
    @Column(nullable = false)
    private Integer stock;

    @Column(name = "sur_ordonnance", nullable = false)
    private boolean surOrdonnance;
}
