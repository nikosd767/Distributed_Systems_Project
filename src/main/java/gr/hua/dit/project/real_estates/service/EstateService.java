package gr.hua.dit.project.real_estates.service;

import gr.hua.dit.project.real_estates.entities.*;
import gr.hua.dit.project.real_estates.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstateService {

    private EstateRepository estateRepository;
    private OwnerRepository ownerRepository;
    private ApplicationRepository applicationRepository;
    private ReportRepository reportRepository;

    // Constructor
    public EstateService(EstateRepository estateRepository, 
                        OwnerRepository ownerRepository,
                        ApplicationRepository applicationRepository,
                        ReportRepository reportRepository) {
        this.estateRepository = estateRepository;
        this.ownerRepository = ownerRepository;
        this.applicationRepository = applicationRepository;
        this.reportRepository = reportRepository;
    }

    // Get all estates
    @Transactional
    public List<Estate> getEstates(){
        return estateRepository.findAll();
    }

    // Save new estate and assign to current owner
    @Transactional
    public Integer saveEstate(Estate estate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Owner owner = ownerRepository.findByEmail(authentication.getName());
            estate.setOwner(owner);

            estate = estateRepository.save(estate);
            return estate.getId();
        }

    // Get estate by ID
    @Transactional
    public Estate getEstate(Integer estateId) {
        return estateRepository.findById(estateId)
                .orElseThrow(() -> new RuntimeException("Estate not found with id: " + estateId));
    }

    // Get estates pending admin approval
    @Transactional
    public List<Estate> getPendingEstates() {
        return estateRepository.findByEstateStatusForAdmin(EstateStatusForAdmin.Pending);
    }

    // Get admin-approved estates
    @Transactional
    public List<Estate> getApprovedEstates() {
        return estateRepository.findByEstateStatusForAdmin(EstateStatusForAdmin.Approved);
    }

    // Approve estate
    @Transactional
    public void approveEstate(int estateId) {
        Estate estate = estateRepository.findById(estateId).
                orElseThrow(() -> new RuntimeException("Estate not found"));
        estate.setEstateStatusForAdmin(EstateStatusForAdmin.Approved);
        estateRepository.save(estate);
    }

    // Update estate details
    @Transactional
    public Integer updateEstate(Estate estate) {
        estate = estateRepository.save(estate);
        return estate.getId();
    }

    // Update estate occupancy status
    @Transactional
    public void updateEstateTenantStatus(Estate estate, EstateStatusForTenant status) {
        estate.setEstateStatusForTenant(status);
        estateRepository.save(estate);
    }

    // Auto-update estate occupancy based on current date
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateEstateOccupancyStatus() {
        LocalDate currentDate = LocalDate.now();
        List<Estate> allEstates = estateRepository.findAll();
        
        for (Estate estate : allEstates) {
            List<Application> approvedApplications = applicationRepository.findByEstateAndStatus(estate, "APPROVED");
            boolean isCurrentlyOccupied = false;
            
            for (Application app : approvedApplications) {
                if (!currentDate.isBefore(app.getArrivalDate()) && !currentDate.isAfter(app.getDepartureDate())) {
                    isCurrentlyOccupied = true;
                    break;
                }
            }
            
            EstateStatusForTenant newStatus = isCurrentlyOccupied ? EstateStatusForTenant.Occupied : EstateStatusForTenant.Not_occupied;
            if (!estate.getEstateStatusForTenant().equals(newStatus)) {
                updateEstateTenantStatus(estate, newStatus);
            }
        }
    }

    // Search estates with multiple filters
    @Transactional
    public List<Estate> searchEstates(Double minPrice, Double maxPrice, String location,
                                    Integer yearOfManufacture, Double minSize, Double maxSize,
                                    EstateStatusForTenant occupancyStatus) {
        List<Estate> estates = estateRepository.findAll();
        
        return estates.stream()
                .filter(estate -> minPrice == null || estate.getPrice() >= minPrice)
                .filter(estate -> maxPrice == null || estate.getPrice() <= maxPrice)
                .filter(estate -> location == null || location.isEmpty() ||
                        estate.getCity().toLowerCase().contains(location.toLowerCase()) ||
                        estate.getAddress().toLowerCase().contains(location.toLowerCase()))
                .filter(estate -> yearOfManufacture == null || 
                        yearOfManufacture.equals(estate.getYearOfManufacture()))
                .filter(estate -> minSize == null || estate.getSize() >= minSize)
                .filter(estate -> maxSize == null || estate.getSize() <= maxSize)
                .filter(estate -> occupancyStatus == null || estate.getEstateStatusForTenant() == occupancyStatus)
                .collect(Collectors.toList());
    }

    // Delete estate and related data
    @Transactional
    public void deleteEstate(int id) {
        Estate estate = estateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estate not found with id: " + id));

        List<Application> applications = applicationRepository.findByEstate(estate);
        for (Application application : applications) {
            List<Report> applicationReports = reportRepository.findByApplication(application);
            reportRepository.deleteAll(applicationReports);
            

            applicationRepository.delete(application);
        }

        if (estate.getTenants() != null) {
            estate.getTenants().clear();
            estateRepository.save(estate);
        }

        if (estate.getOwner() != null) {
            estate.setOwner(null);
            estateRepository.save(estate);
        }

        estateRepository.deleteById(id);
    }
}