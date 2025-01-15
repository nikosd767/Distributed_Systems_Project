package gr.hua.dit.project.real_estates.service;

import gr.hua.dit.project.real_estates.entities.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import gr.hua.dit.project.real_estates.repositories.OwnerRepository;
import gr.hua.dit.project.real_estates.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OwnerService {

    private OwnerRepository ownerRepository;
    private UserRepository userRepository;
    private EstateService estateService;

    // Constructor
    public OwnerService(OwnerRepository ownerRepository, UserRepository userRepository, EstateService estateService) {
        this.ownerRepository = ownerRepository;
        this.userRepository = userRepository;
        this.estateService = estateService;
    }

    // Get all owners
    @Transactional
    public List<Owner> getOwners(){
        return ownerRepository.findAll();
    }

    // Get owner by ID
    @Transactional
    public Owner getOwner(Integer ownerId) {
        return ownerRepository.findById(ownerId).
                orElseThrow(() -> new EntityNotFoundException("Owner not found with id: " + ownerId));
    }

    // Save new owner
    @Transactional
    public void saveOwner(Owner owner) {
        ownerRepository.save(owner);
    }

    // Find owner by email
    @Transactional
    public Owner findByEmail(String email) {
        return ownerRepository.findByEmail(email);
    }

    // Delete owner and related data
    @Transactional
    public void deleteOwner(int id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + id));

        String ownerEmail = owner.getEmail();

        List<Estate> estates = owner.getEstates();
        if (estates != null) {
            for (Estate estate : estates) {
                estateService.deleteEstate(estate.getId());
            }
        }

        ownerRepository.delete(owner);

        Optional<User> user = userRepository.findByEmail(ownerEmail);
        if (user.isPresent()) {
            user.get().getRoles().clear();
            userRepository.delete(user.get());
        }
    }

}
