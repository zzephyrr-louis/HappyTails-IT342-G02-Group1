package com.happytails.backend.specification;

import com.happytails.backend.model.Pet;
import com.happytails.backend.model.Shelter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PetSpecification {

    public static Specification<Pet> filterPets(
            String species,
            String gender,
            String breed,
            String size,
            String temperament,
            String shelterLocation,
            Integer minAge,
            Integer maxAge
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by species
            if (species != null && !species.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("species")),
                        species.toLowerCase()
                ));
            }

            // Filter by gender
            if (gender != null && !gender.isEmpty()) {
                try {
                    Pet.PetGender genderEnum = Pet.PetGender.valueOf(gender);
                    predicates.add(criteriaBuilder.equal(root.get("gender"), genderEnum));
                } catch (IllegalArgumentException e) {
                    // Invalid gender, skip this filter
                }
            }

            // Filter by breed (partial match)
            if (breed != null && !breed.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("breed")),
                        "%" + breed.toLowerCase() + "%"
                ));
            }

            // Filter by size
            if (size != null && !size.isEmpty()) {
                try {
                    Pet.PetSize sizeEnum = Pet.PetSize.valueOf(size);
                    predicates.add(criteriaBuilder.equal(root.get("size"), sizeEnum));
                } catch (IllegalArgumentException e) {
                    // Invalid size, skip this filter
                }
            }

            // Filter by temperament (partial match)
            if (temperament != null && !temperament.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("temperament")),
                        "%" + temperament.toLowerCase() + "%"
                ));
            }

            // Filter by shelter location (Cebu area only as per FR-10)
            if (shelterLocation != null && !shelterLocation.isEmpty()) {
                Join<Pet, Shelter> shelterJoin = root.join("shelter");
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(shelterJoin.get("location")),
                        "%" + shelterLocation.toLowerCase() + "%"
                ));
            }

            // Filter by age range
            // Note: Age is stored as a string in the database, so we'll attempt numeric parsing
            // For better implementation, consider storing age as an integer
            if (minAge != null || maxAge != null) {
                // This is a simplified approach - for production, consider storing age as integer
                // For now, we'll skip complex string-to-number age filtering
                // You can enhance this later based on your age format (e.g., "2 years", "6 months")
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
