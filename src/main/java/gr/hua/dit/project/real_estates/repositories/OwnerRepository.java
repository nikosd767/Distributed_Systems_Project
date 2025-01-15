package gr.hua.dit.project.real_estates.repositories;

import gr.hua.dit.project.real_estates.entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {
    // Find owner by their email address
    Owner findByEmail(String email);
}
