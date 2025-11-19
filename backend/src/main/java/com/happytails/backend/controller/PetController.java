package com.happytails.backend.controller;

import com.happytails.backend.dto.PetRequest; // Import this!
import com.happytails.backend.model.Pet;
import com.happytails.backend.model.ShelterStaff; // Import this!
import com.happytails.backend.repository.ShelterStaffRepository; // Import this!
import com.happytails.backend.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder; // Import this!
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private ShelterStaffRepository shelterStaffRepository;

    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        Optional<Pet> pet = petService.getPetById(id);
        return pet.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // --- THIS WAS MISSING ---
    @PostMapping
    public ResponseEntity<?> createPet(@RequestBody PetRequest petRequest) {
        // --- DEBUG PRINT ---
        System.out.println("DEBUG: Received Pet Request!");
        System.out.println("DEBUG: Name: " + petRequest.getName());
        System.out.println("DEBUG: Desc: " + petRequest.getDescription());
        // -------------------

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<ShelterStaff> staffOpt = shelterStaffRepository.findByEmail(email);

        if (staffOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Shelter Staff can add pets.");
        }

        Pet newPet = petService.createPet(petRequest, staffOpt.get().getShelter());
        return ResponseEntity.ok(newPet);
    }
}