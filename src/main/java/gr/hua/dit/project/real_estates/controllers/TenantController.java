package gr.hua.dit.project.real_estates.controllers;

import gr.hua.dit.project.real_estates.entities.Estate;
import gr.hua.dit.project.real_estates.entities.EstateStatusForTenant;
import gr.hua.dit.project.real_estates.entities.Tenant;
import gr.hua.dit.project.real_estates.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("tenants")
public class TenantController {

    private final TenantService tenantService;

    // Constructor
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    // Retrieves and displays all tenants
    @GetMapping("")
    public String showTenants(Model model){
        model.addAttribute("tenants", tenantService.getTenants());
        return "tenant/tenants";
    }

    // Gets a specific tenant by their ID
    @GetMapping("/{id}")
    public String showTenant(@PathVariable Integer id, Model model) {
        try {
            model.addAttribute("tenant", tenantService.getTenant(id));
            return "tenant/tenant";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found", e);
        }
    }

    // Saves a new tenant to the system
    @PostMapping("/new")
    public String saveTenant(@ModelAttribute("tenant") Tenant tenant, Model model) {
        tenantService.saveTenant(tenant);
        model.addAttribute("tenants", tenantService.getTenants());
        return "tenant/tenants";
    }

    // Updates tenant information in the database
    @PostMapping("/{id}")
    public String updateTenant(@PathVariable Integer id, @ModelAttribute("tenant") Tenant tenant, Model model) {
        try {
            tenant.setId(id);
            tenantService.saveTenant(tenant);
            model.addAttribute("tenants", tenantService.getTenants());
            return "tenant/tenants";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating tenant", e);
        }
    }

    @GetMapping("/view/{id}")
    public String viewTenantProfile(@PathVariable Integer id, Model model) {
        Optional<Tenant> tenantOpt = tenantService.findTenantById(id);
        if (!tenantOpt.isPresent()) {
            model.addAttribute("error", "Tenant not found");
            return "error/error";
        }

        Tenant tenant = tenantOpt.get();
        model.addAttribute("tenant", tenant);
        return "tenant/view-tenant";
    }

    // Removes a tenant from the system - Admin only
    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/delete")
    public String deleteTenant(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            tenantService.deleteTenant(id);
            redirectAttributes.addFlashAttribute("success", "Tenant deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting tenant: " + e.getMessage());
        }
        return "redirect:/tenants";
    }

    // Searches estates based on user-defined filters
    @GetMapping("/estates/search")
    public String searchEstates(@RequestParam(required = false) Double minPrice,
                              @RequestParam(required = false) Double maxPrice,
                              @RequestParam(required = false) String location,
                              @RequestParam(required = false) Integer yearOfManufacture,
                              @RequestParam(required = false) Double minSize,
                              @RequestParam(required = false) Double maxSize,
                              @RequestParam(required = false) String occupancyStatus,
                              Model model) {
        
        EstateStatusForTenant statusEnum = null;
        if (occupancyStatus != null && !occupancyStatus.isEmpty()) {
            try {
                statusEnum = EstateStatusForTenant.valueOf(occupancyStatus);
            } catch (IllegalArgumentException e) {
            }
        }
        
        List<Estate> estates = tenantService.searchEstates(minPrice, maxPrice, location, 
                yearOfManufacture, minSize, maxSize, statusEnum);
        model.addAttribute("estates", estates);
        return "tenant/estate-search";
    }

    // Shows estate details to authenticated tenant
    @GetMapping("/estates/{estateId}")
    public String viewEstate(@PathVariable Integer estateId, @AuthenticationPrincipal UserDetails userDetails, Model model) {

        Tenant tenant = tenantService.getTenantByEmail(userDetails.getUsername());

        Estate estate = tenantService.viewEstate(estateId);
        model.addAttribute("estate", estate);
        model.addAttribute("tenantId", tenant.getId());
        return "estate/estate-details";
    }

    // Shows all applications made by a tenant
    public String viewApplications(@PathVariable Integer tenantId, Model model) {
        List<Map<String, Object>> applications = tenantService.viewApplications(tenantId);
        model.addAttribute("applications", applications);
        return "tenant/applications";
    }

    // Creates new application for an estate
    @PostMapping("/tenants/estates/{estateId}/apply")
    public String submitApplication(@PathVariable Integer estateId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        try {
            Tenant currentTenant = tenantService.getTenantByEmail(userDetails.getUsername());
            boolean success = tenantService.submitApplication(currentTenant.getId(), estateId);
            
            if (success) {
                model.addAttribute("message", "Application submitted successfully");
                return "redirect:/tenants/estates/" + estateId + "?success=true";
            } else {
                model.addAttribute("error", "Failed to submit application - Estate might not be available or you may have already applied");
                return "redirect:/tenants/estates/" + estateId + "?error=application_failed";
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/tenants/estates/" + estateId + "?error=system_error";
        }
    }
}