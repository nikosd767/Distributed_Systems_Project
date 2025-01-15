package gr.hua.dit.project.real_estates.service;

import gr.hua.dit.project.real_estates.entities.Application;
import gr.hua.dit.project.real_estates.entities.Estate;
import gr.hua.dit.project.real_estates.entities.Tenant;
import gr.hua.dit.project.real_estates.repositories.ApplicationRepository;
import gr.hua.dit.project.real_estates.repositories.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final TenantRepository tenantRepository;

    // Application status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";

    // Constructor
    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository, TenantRepository tenantRepository) {
        this.applicationRepository = applicationRepository;
        this.tenantRepository = tenantRepository;
    }

    // Create new application for a tenant
    @Transactional
    public Application createApplication(Application application, String email) {
        Optional<Tenant> tenantOpt = tenantRepository.findByEmail(email);
        Tenant tenant = tenantOpt.orElseGet(() -> {
            Tenant newTenant = new Tenant();
            newTenant.setEmail(email);
            return tenantRepository.save(newTenant);
        });
        
        if (hasOverlappingApprovedApplications(application.getEstate(), 
                application.getArrivalDate(), 
                application.getDepartureDate())) {
            throw new IllegalStateException("There is already an approved application for this estate during the selected dates");
        }
        
        application.setTenant(tenant);
        application.setStatus(STATUS_PENDING);
        return applicationRepository.save(application);
    }

    // Get all applications for a tenant
    @Transactional
    public List<Application> getApplicationsByTenant(Tenant tenant) {
        return applicationRepository.findByTenant(tenant);
    }

    // Get all applications for an estate
    @Transactional
    public List<Application> getApplicationsByEstate(Estate estate) {
        return applicationRepository.findByEstate(estate);
    }

    // Get pending applications for an estate
    @Transactional
    public List<Application> getPendingApplicationsByEstate(Estate estate) {
        return applicationRepository.findByEstateAndStatus(estate, STATUS_PENDING);
    }

    // Get pending applications for a tenant
    @Transactional
    public List<Application> getPendingApplicationsByTenant(Tenant tenant) {
        return applicationRepository.findByTenantAndStatus(tenant, STATUS_PENDING);
    }

    // Get approved applications for a tenant
    @Transactional
    public List<Application> getApprovedApplicationsByTenant(Tenant tenant) {
        return applicationRepository.findByTenantAndStatus(tenant, STATUS_APPROVED);
    }

    // Get approved applications by tenant email
    @Transactional
    public List<Application> getApprovedApplicationsByEmail(String email) {
        Optional<Tenant> tenant = tenantRepository.findByEmail(email);
        return tenant.map(value -> applicationRepository.findByTenantAndStatus(value, STATUS_APPROVED))
                    .orElse(Collections.emptyList());
    }

    // Get application by ID
    @Transactional
    public Application getApplicationById(int id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));
    }

    // Update application status (PENDING/APPROVED)
    @Transactional
    public Application updateApplicationStatus(int id, String status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        
        status = status.toUpperCase();
        
        if (!status.equals(STATUS_PENDING) && 
            !status.equals(STATUS_APPROVED)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        
        application.setStatus(status);
        return applicationRepository.save(application);
    }

    // Delete application by ID
    @Transactional
    public void deleteApplication(int id) {
        applicationRepository.deleteById(id);
    }

    // Get all approved applications
    @Transactional
    public List<Application> getAllApprovedApplications() {
        return applicationRepository.findAllByStatus(STATUS_APPROVED);
    }

    // Get unique estates from tenant's approved applications
    @Transactional
    public List<Estate> getUniqueEstatesFromApprovedApplications(String email) {
        Optional<Tenant> tenant = tenantRepository.findByEmail(email);
        if (tenant.isEmpty()) {
            return Collections.emptyList();
        }

        List<Application> approvedApplications = applicationRepository.findByTenantAndStatus(tenant.get(), STATUS_APPROVED);
        
        return approvedApplications.stream()
                .map(Application::getEstate)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    // Check for overlapping approved applications for an estate
    @Transactional
    public boolean hasOverlappingApprovedApplications(Estate estate, LocalDate arrivalDate, LocalDate departureDate) {
        List<Application> approvedApplications = applicationRepository.findByEstateAndStatus(estate, STATUS_APPROVED);
        
        return approvedApplications.stream().anyMatch(app -> {
            boolean noOverlap = departureDate.isBefore(app.getArrivalDate()) || arrivalDate.isAfter(app.getDepartureDate());
            return !noOverlap;
        });
    }
}
