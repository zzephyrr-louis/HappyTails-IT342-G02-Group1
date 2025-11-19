package com.happytails.backend.service;

import com.happytails.backend.dto.PetRequest;
import com.happytails.backend.model.Pet;
import com.happytails.backend.model.Shelter;
import com.happytails.backend.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PetService
 * Tests the business logic for pet management including:
 * - Creating pets (FR-6)
 * - Updating pets (FR-7)
 * - Changing pet status (FR-8)
 * - Searching and filtering pets (FR-10, FR-11)
 */
@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private Shelter testShelter;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        // Setup test shelter
        testShelter = new Shelter();
        testShelter.setShelterId(1L);
        testShelter.setName("Test Shelter");
        testShelter.setLocation("Cebu City");

        // Setup test pet
        testPet = new Pet();
        testPet.setPetId(1L);
        testPet.setName("Luna");
        testPet.setSpecies("Dog");
        testPet.setBreed("Golden Retriever");
        testPet.setAge("2 years");
        testPet.setSize(Pet.PetSize.Large);
        testPet.setGender(Pet.PetGender.Female);
        testPet.setStatus(Pet.PetStatus.Available);
        testPet.setDescription("Friendly dog");
        testPet.setTemperament("Friendly, Playful");
        testPet.setShelter(testShelter);
    }

    @Test
    void testGetAllPets() {
        // Given
        List<Pet> pets = Arrays.asList(testPet);
        when(petRepository.findAll()).thenReturn(pets);

        // When
        List<Pet> result = petService.getAllPets();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Luna", result.get(0).getName());
        verify(petRepository, times(1)).findAll();
    }

    @Test
    void testGetPetById_Success() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        // When
        Optional<Pet> result = petService.getPetById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Luna", result.get().getName());
        verify(petRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPetById_NotFound() {
        // Given
        when(petRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Pet> result = petService.getPetById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(petRepository, times(1)).findById(999L);
    }

    @Test
    void testCreatePet_Success() {
        // Given
        PetRequest request = new PetRequest();
        request.setName("Max");
        request.setSpecies("Dog");
        request.setBreed("Beagle");
        request.setAge("3 years");
        request.setSize("Medium");
        request.setGender("Male");
        request.setDescription("Friendly beagle");
        request.setTemperament("Curious, Friendly");

        Pet savedPet = new Pet();
        savedPet.setPetId(2L);
        savedPet.setName("Max");
        savedPet.setSpecies("Dog");
        savedPet.setStatus(Pet.PetStatus.Available);

        when(petRepository.save(any(Pet.class))).thenReturn(savedPet);

        // When
        Pet result = petService.createPet(request, testShelter);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getPetId());
        assertEquals("Max", result.getName());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void testCreatePet_WithDefaultSize() {
        // Given
        PetRequest request = new PetRequest();
        request.setName("Whiskers");
        request.setSpecies("Cat");
        request.setDescription("Cute cat");
        // Size is null - should default to Medium

        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pet result = petService.createPet(request, testShelter);

        // Then
        assertNotNull(result);
        assertEquals(Pet.PetSize.Medium, result.getSize());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void testUpdatePet_Success() {
        // Given
        PetRequest request = new PetRequest();
        request.setName("Luna Updated");
        request.setBreed("Golden Retriever Mix");
        request.setDescription("Updated description");

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pet result = petService.updatePet(1L, request, testShelter);

        // Then
        assertNotNull(result);
        assertEquals("Luna Updated", result.getName());
        assertEquals("Golden Retriever Mix", result.getBreed());
        assertEquals("Updated description", result.getDescription());
        verify(petRepository, times(1)).findById(1L);
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void testUpdatePet_PetNotFound() {
        // Given
        PetRequest request = new PetRequest();
        request.setName("Updated Name");

        when(petRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            petService.updatePet(999L, request, testShelter);
        });

        verify(petRepository, times(1)).findById(999L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void testUpdatePet_WrongShelter() {
        // Given
        Shelter otherShelter = new Shelter();
        otherShelter.setShelterId(2L);
        otherShelter.setName("Other Shelter");

        PetRequest request = new PetRequest();
        request.setName("Updated Name");

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            petService.updatePet(1L, request, otherShelter);
        });

        verify(petRepository, times(1)).findById(1L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void testUpdatePetStatus_Success() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pet result = petService.updatePetStatus(1L, Pet.PetStatus.Adopted, testShelter);

        // Then
        assertNotNull(result);
        assertEquals(Pet.PetStatus.Adopted, result.getStatus());
        verify(petRepository, times(1)).findById(1L);
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void testUpdatePetStatus_PetNotFound() {
        // Given
        when(petRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            petService.updatePetStatus(999L, Pet.PetStatus.Adopted, testShelter);
        });

        verify(petRepository, times(1)).findById(999L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void testUpdatePetStatus_WrongShelter() {
        // Given
        Shelter otherShelter = new Shelter();
        otherShelter.setShelterId(2L);

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            petService.updatePetStatus(1L, Pet.PetStatus.Adopted, otherShelter);
        });

        verify(petRepository, times(1)).findById(1L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void testUpdatePetStatus_AllStatuses() {
        // Test all valid status transitions
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Available → Pending
        Pet result1 = petService.updatePetStatus(1L, Pet.PetStatus.Pending, testShelter);
        assertEquals(Pet.PetStatus.Pending, result1.getStatus());

        // Pending → Adopted
        Pet result2 = petService.updatePetStatus(1L, Pet.PetStatus.Adopted, testShelter);
        assertEquals(Pet.PetStatus.Adopted, result2.getStatus());

        // Adopted → Available (e.g., adoption fell through)
        Pet result3 = petService.updatePetStatus(1L, Pet.PetStatus.Available, testShelter);
        assertEquals(Pet.PetStatus.Available, result3.getStatus());

        verify(petRepository, times(3)).save(any(Pet.class));
    }

    @Test
    void testCreatePet_WithAllOptionalFields() {
        // Given
        PetRequest request = new PetRequest();
        request.setName("Bella");
        request.setSpecies("Dog");
        request.setBreed("French Bulldog");
        request.setAge("4 years");
        request.setSize("Small");
        request.setGender("Female");
        request.setDescription("Sweet dog");
        request.setTemperament("Affectionate, Calm");
        request.setPhotosJson("[\"photo1.jpg\", \"photo2.jpg\"]");

        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pet result = petService.createPet(request, testShelter);

        // Then
        assertNotNull(result);
        assertEquals("Bella", result.getName());
        assertEquals(Pet.PetSize.Small, result.getSize());
        assertEquals(Pet.PetGender.Female, result.getGender());
        assertEquals("Affectionate, Calm", result.getTemperament());
        assertEquals("[\"photo1.jpg\", \"photo2.jpg\"]", result.getPhotosJson());
        verify(petRepository, times(1)).save(any(Pet.class));
    }
}
