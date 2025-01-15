package gr.hua.dit.project.real_estates.controllers;

import gr.hua.dit.project.real_estates.entities.Estate;
import gr.hua.dit.project.real_estates.entities.Owner;
import gr.hua.dit.project.real_estates.entities.Tenant;
import gr.hua.dit.project.real_estates.entities.Application;
import gr.hua.dit.project.real_estates.service.OwnerService;
import gr.hua.dit.project.real_estates.service.TenantService;
import gr.hua.dit.project.real_estates.service.ApplicationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controller handling owner operations and property management
@Controller
@RequestMapping("owner")
public class OwnerController {

   private OwnerService ownerService;
   private TenantService tenantService;
   private ApplicationService applicationService;

   public OwnerController(OwnerService ownerService, ApplicationService applicationService) {
       this.ownerService = ownerService;
       this.applicationService = applicationService;
   }

   // Display list of all owners
   @RequestMapping()
   public String showOwners(Model model) {
       model.addAttribute("owners", ownerService.getOwners());
       return "owner/owners";
   }

   // Display single owner details
   @GetMapping("/{id}")
   public String showOwner(@PathVariable Integer id, Model model){
       Owner owner = ownerService.getOwner(id);
       model.addAttribute("owners", owner);
       return "owner/owners";
   }

   // Handle owner creation
   @PostMapping("/new")
   public String saveOwner(@ModelAttribute("owner") Owner owner, Model model) {
       ownerService.saveOwner(owner);
       model.addAttribute("owners", ownerService.getOwners());
       return "owner/owners";
   }

   // Display estates belonging to specific owner
   @GetMapping("/{id}/estates")
   public String showEstates(@PathVariable("id") Integer id, Model model){
       Owner owner = ownerService.getOwner(id);
       model.addAttribute("estates", owner.getEstates());
       return "estate/estates";
   }

   // Handle tenant approval for estate application
   @PostMapping("/estates/{estateId}/apply")
   public String approveTenant(@PathVariable Integer estateId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
       try {
           Tenant currentTenant = tenantService.getTenantByEmail(userDetails.getUsername());

           boolean success = tenantService.approveTenant(currentTenant.getId(), estateId);
           if (success) {
               model.addAttribute("message", "Application approved");
           } else {
               model.addAttribute("error", "Failed to approve application");
           }
           return "redirect:/tenant/estates/" + estateId + "?success=" + success;
       } catch (Exception e) {
           return "redirect:/tenant/estates/" + estateId + "?error=true";
       }
   }

   // Display logged-in owner's properties
   @GetMapping("/my-properties")
   public String showMyProperties(Principal principal, Model model) {
       if (principal == null) {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
       }

       Owner owner = ownerService.findByEmail(principal.getName());
       model.addAttribute("estates", owner.getEstates());
       return "owner/my-properties";
   }

   // Display all applications for owner's properties grouped by estate
   @GetMapping("/applications")
   public String viewApplications(Principal principal, Model model) {
       if (principal == null) {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
       }

       Owner owner = ownerService.findByEmail(principal.getName());
       if (owner != null && owner.getEstates() != null) {
           // Create map to store applications for each estate
           Map<Integer, List<Application>> applicationsByEstate = new HashMap<>();

           for (Estate estate : owner.getEstates()) {
               applicationsByEstate.put(estate.getId(), new ArrayList<>());
           }

           for (Estate estate : owner.getEstates()) {
               applicationsByEstate.put(estate.getId(),
                       applicationService.getApplicationsByEstate(estate));
           }

           model.addAttribute("estates", owner.getEstates());
           model.addAttribute("applications", applicationsByEstate);
           return "owner/manage-applications";
       }
       throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found");
   }

   // Display only pending applications for owner's properties
   @GetMapping("/applications/pending")
   public String viewPendingApplications(Principal principal, Model model) {
       if (principal == null) {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
       }

       Owner owner = ownerService.findByEmail(principal.getName());
       if (owner != null && owner.getEstates() != null) {
           Map<Integer, List<Application>> applicationsByEstate = new HashMap<>();
           
           for (Estate estate : owner.getEstates()) {
               applicationsByEstate.put(estate.getId(), new ArrayList<>());
           }
           
           for (Estate estate : owner.getEstates()) {
               applicationsByEstate.put(estate.getId(),
                       applicationService.getPendingApplicationsByEstate(estate));
           }

           model.addAttribute("estates", owner.getEstates());
           model.addAttribute("applications", applicationsByEstate);
           return "owner/manage-applications";
       }
       throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found");
   }

   // Handle application status updates with owner verification
   @PostMapping("/applications/{id}/status")
   public String updateApplicationStatus(
           @PathVariable int id,
           @RequestParam String status,
           Principal principal,
           RedirectAttributes redirectAttributes) {
       try {
           if (principal == null) {
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
           }

           Owner owner = ownerService.findByEmail(principal.getName());
           if (owner == null) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found");
           }

           Application application = applicationService.getApplicationById(id);
           if (application == null) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
           }

           // Verify owner owns the estate related to application
           boolean ownsEstate = owner.getEstates().stream()
                   .anyMatch(estate -> estate.getId() == application.getEstate().getId());

           if (!ownsEstate) {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to update this application");
           }

           applicationService.updateApplicationStatus(id, status);
           redirectAttributes.addFlashAttribute("successMessage", "Application status updated successfully");

       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("errorMessage", "Failed to update application status: " + e.getMessage());
       }

       return "redirect:/owner/applications";
   }

   // Handle application deletion
   @PostMapping("/applications/{id}/delete")
   public String deleteApplication(
           @PathVariable int id,
           Principal principal,
           RedirectAttributes redirectAttributes) {
       try {
           if (principal == null) {
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
           }

           Owner owner = ownerService.findByEmail(principal.getName());
           if (owner == null) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found");
           }

           Application application = applicationService.getApplicationById(id);
           if (application == null) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
           }

           // Verify owner owns the estate related to application
           boolean ownsEstate = owner.getEstates().stream()
                   .anyMatch(estate -> estate.getId() == application.getEstate().getId());

           if (!ownsEstate) {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this application");
           }

           applicationService.deleteApplication(id);
           redirectAttributes.addFlashAttribute("successMessage", "Application deleted successfully");

       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete application: " + e.getMessage());
       }

       return "redirect:/owner/applications";
   }

   // Handle owner deletion
   @PostMapping("/{id}/delete")
   public String deleteOwner(@PathVariable int id, RedirectAttributes redirectAttributes) {
       try {
           ownerService.deleteOwner(id);
           redirectAttributes.addFlashAttribute("success", "Owner deleted successfully");
       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("error", "Error deleting owner: " + e.getMessage());
       }
       return "redirect:/owner";
   }
}