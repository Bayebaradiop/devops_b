package com.medishop.backend.medicament;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MedicamentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listeLesMedicamentsDuJeuDeDonnees() throws Exception {
        mockMvc.perform(get("/api/medicaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))));
    }

    @Test
    void filtreParNom() throws Exception {
        mockMvc.perform(get("/api/medicaments").param("nom", "parace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom").value("Paracetamol 500mg"));
    }

    @Test
    void creeUnMedicamentPuisLeSupprime() throws Exception {
        String json = """
                {"nom":"Doliprane 1000mg","description":"Antalgique","prix":2200.00,"stock":50,"surOrdonnance":false}
                """;

        String location = mockMvc.perform(post("/api/medicaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nom").value("Doliprane 1000mg"))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(delete(location)).andExpect(status().isNoContent());
        mockMvc.perform(get(location)).andExpect(status().isNotFound());
    }

    @Test
    void refuseUnMedicamentInvalide() throws Exception {
        String json = """
                {"nom":"","prix":-5,"stock":10}
                """;

        mockMvc.perform(post("/api/medicaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void renvoie404SurIdInconnu() throws Exception {
        mockMvc.perform(get("/api/medicaments/999999")).andExpect(status().isNotFound());
    }
}
