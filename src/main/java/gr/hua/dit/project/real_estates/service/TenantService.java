package gr.hua.dit.project.real_estates.service;

import gr.hua.dit.project.real_estates.entities.*;
import gr.hua.dit.project.real_estates.repositories.EstateRepository;
import gr.hua.dit.project.real_estates.repositories.TenantRepository;
import gr.hua.dit.project.real_estates.repositories.ApplicationRepository;
import gr.hua.dit.project.real_estates.repositories.ReportRepository;
import gr.hua.dit.project.real_estates.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TenantService {

    private TenantRepository tenantRepository;
    private EstateRepository estateRepository;
    private ApplicationRepository applicationRepository;
    private ReportRepository reportRepository;
    private UserRepository userRepository;

    // Constructor
    public TenantService(TenantRepository tenantRepository, EstateRepository estateRepository,
                        ApplicationRepository applicationRepository, ReportRepository reportRepository,
                        UserRepository userRepository) {
        this.tenantRepository = tenantRepository;
        this.estateRepository = estateRepository;
        this.applicationRepository = applicationRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    // Get all tenants
    @Transactional
    public List<Tenant> getTenants(){
        return tenantRepository.findAll();
    }

    // Get tenant by ID
    @Transactional
    public Tenant getTenant(Integer tenantId) {
        return tenantRepository.findById(tenantId).get();
    }

    // Save new tenant
    @Transactional
    public void saveTenant(Tenant tenant) {
        tenantRepository.save(tenant);
    }

    // Approve tenant for estate
    @Transactional
    public boolean approveTenant(Integer tenantId, Integer estateId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found."));

        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new RuntimeException("Estate not found."));

        if(estate.getEstateStatusForAdmin() != EstateStatusForAdmin.Approved) {
            return false;
        }

        Map<String,Object> application = new HashMap<>();
        application.put("tenantId", tenant.getId());
        application.put("estateId", estate.getId());
        application.put("status", EstateStatusForTenant.Occupied);
        application.put("submissionDate", new Date());

        estate.addTenant(tenant);
        estateRepository.save(estate);

        return true;

    }

    // Find tenant by email
    @Transactional
    public Tenant getTenantByEmail(String email) {
        return tenantRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
    }

    // Search estates with filters
    @Transactional
    public List<Estate> searchEstates(Double minPrice, Double maxPrice, String location,
                                    Integer yearOfManufacture, Double minSize, Double maxSize,
                                    EstateStatusForTenant occupancyStatus) {
        List<Estate> estates = estateRepository.findAll();

        return estates.stream()
                .filter(estate -> estate.getEstateStatusForAdmin() != EstateStatusForAdmin.Pending)
                .filter(estate -> {
                    boolean matches = true;
                    
                    if (minPrice != null) {
                        matches &= estate.getPrice() >= minPrice;
                    }
                    if (maxPrice != null) {
                        matches &= estate.getPrice() <= maxPrice;
                    }
                    
                    if (minSize != null) {
                        matches &= estate.getSize() >= minSize;
                    }
                    if (maxSize != null) {
                        matches &= estate.getSize() <= maxSize;
                    }
                    
                    if (yearOfManufacture != null) {
                        matches &= estate.getYearOfManufacture() == yearOfManufacture;
                    }
                    
                    if (location != null && !location.isEmpty()) {
                        String locationLower = location.toLowerCase();
                        matches &= (estate.getCity().toLowerCase().contains(locationLower) || 
                                  estate.getAddress().toLowerCase().contains(locationLower));
                    }

                    if (occupancyStatus != null) {
                        matches &= estate.getEstateStatusForTenant() == occupancyStatus;
                    }
                    
                    return matches;
                })
                .toList();
    }

    // View estate details
    @Transactional
    public Estate viewEstate(Integer estateId) {
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new RuntimeException("Estate not found"));
        
        if (estate.getEstateStatusForAdmin() == EstateStatusForAdmin.Pending) {
            throw new RuntimeException("This estate is pending approval and cannot be viewed");
        }
        
        return estate;
    }

    // View tenant's applications
    @Transactional
    public List<Map<String, Object>> viewApplications(Integer tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        List<Map<String, Object>> applications = new ArrayList<>();

        tenant.getEstates().forEach(estate -> {
            Map<String, Object> application = new HashMap<>();
            application.put("estate", estate);
            application.put("status", estate.getEstateStatusForAdmin());
            application.put("submissionDate", new Date());
            applications.add(application);
        });

        return applications;
    }

    // Submit new application for estate
    @Transactional
    public boolean submitApplication(Integer tenantId, Integer estateId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found."));

        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new RuntimeException("Estate not found."));

        if(estate.getEstateStatusForAdmin() != EstateStatusForAdmin.Pending) {
            return false;
        }

        if(estate.getTenants().contains(tenant)) {
            return false;
        }

        Map<String,Object> application = new HashMap<>();
        application.put("tenantId", tenant.getId());
        application.put("estateId", estate.getId());
        application.put("status", EstateStatusForAdmin.Pending);
        application.put("submissionDate", new Date());

        estate.addTenant(tenant);
        estateRepository.save(estate);

        return true;
    }

    // Find tenant by ID
    @Transactional
    public Optional<Tenant> findTenantById(Integer id) {
        return tenantRepository.findById(id);
    }

    // Delete tenant and related data
    @Transactional
    public void deleteTenant(int id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + id));

        String tenantEmail = tenant.getEmail();

        List<Application> applications = applicationRepository.findByTenant(tenant);
        if (applications != null) {
            for (Application application : applications) {
                List<Report> reports = reportRepository.findByApplication(application);
                reportRepository.deleteAll(reports);
                
                applicationRepository.delete(application);
            }
        }

        tenantRepository.delete(tenant);

        Optional<User> user = userRepository.findByEmail(tenantEmail);
        if (user.isPresent()) {
            user.get().getRoles().clear();
            userRepository.delete(user.get());
        }
    }
}
