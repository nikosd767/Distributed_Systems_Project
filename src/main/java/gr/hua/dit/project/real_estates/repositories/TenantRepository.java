package gr.hua.dit.project.real_estates.repositories;

import gr.hua.dit.project.real_estates.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    // Find tenant by their email address
    Optional<Tenant> findByEmail(String email);
}
