package com.happytails.backend.service;

import com.happytails.backend.dto.PetRequest;
import com.happytails.backend.model.Pet;
import com.happytails.backend.model.Shelter;
import com.happytails.backend.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    // --- NEW: Create Pet Logic ---
    public Pet createPet(PetRequest req, Shelter shelter) {
        Pet pet = new Pet();
        pet.setName(req.getName());
        pet.setSpecies(req.getSpecies());
        pet.setBreed(req.getBreed());
        pet.setAge(req.getAge());
        pet.setDescription(req.getDescription());
        pet.setShelter(shelter); // Automatically link to the staff's shelter

        // Set Defaults
        pet.setStatus(Pet.PetStatus.Available);

        // Handle Enums safely (defaulting to Medium if invalid or missing)
        try {
            if (req.getSize() != null) {
                pet.setSize(Pet.PetSize.valueOf(req.getSize()));
            } else {
                pet.setSize(Pet.PetSize.Medium);
            }
        } catch (IllegalArgumentException e) {
            pet.setSize(Pet.PetSize.Medium);
        }

        // You can add logic for Gender/Photos here later if your model supports it

        return petRepository.save(pet);
    }
}