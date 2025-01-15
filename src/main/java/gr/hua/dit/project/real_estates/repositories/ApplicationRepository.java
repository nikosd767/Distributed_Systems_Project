package gr.hua.dit.project.real_estates.repositories;

import gr.hua.dit.project.real_estates.entities.Application;
import gr.hua.dit.project.real_estates.entities.Estate;
import gr.hua.dit.project.real_estates.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    // Find all applications for a specific tenant
    List<Application> findByTenant(Tenant tenant);

    // Find all applications for a specific estate
    List<Application> findByEstate(Estate estate);

    // Find applications for an estate with specific status
    List<Application> findByEstateAndStatus(Estate estate, String status);

    // Find applications for a tenant with specific status
    List<Application> findByTenantAndStatus(Tenant tenant, String status);

    // Find all applications with specific status
    List<Application> findAllByStatus(String status);
}
