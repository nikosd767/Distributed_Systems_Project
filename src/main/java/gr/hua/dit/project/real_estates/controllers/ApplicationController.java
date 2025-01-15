package gr.hua.dit.project.real_estates.controllers;

import gr.hua.dit.project.real_estates.entities.Application;
import gr.hua.dit.project.real_estates.entities.Estate;
import gr.hua.dit.project.real_estates.repositories.TenantRepository;
import gr.hua.dit.project.real_estates.service.ApplicationService;
import gr.hua.dit.project.real_estates.service.EstateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// Controller for handling rental applications
@Controller
@RequestMapping("/applications")
public class ApplicationController {

   // Service for managing application operations
   @Autowired
   private ApplicationService applicationService;

   // Service for managing estate operations
   @Autowired 
   private EstateService estateService;

   // Repository for accessing tenant data
   @Autowired
   private TenantRepository tenantRepository;

   // Display application form for a specific estate
   // Takes estate ID and adds estate details and empty application to the model
   @GetMapping("/create/{estateId}") 
   public String showApplicationForm(@PathVariable int estateId, Model model) {
       try {
           Estate estate = estateService.getEstate(estateId);
           model.addAttribute("estate", estate);
           model.addAttribute("application", new Application());
           return "tenant/application-form";
       } catch (Exception e) {
           return "redirect:/error";
       }
   }

   // Handle application submission with validation
   // Validates authentication, dates and creates new application
   // Returns appropriate error messages if validation fails
   @PostMapping("/submit")
   public String submitApplication(@ModelAttribute Application application,
                                   @RequestParam int estateId,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
       try {
           // Check if user is authenticated
           if (authentication == null) {
               redirectAttributes.addFlashAttribute("error", "You must be logged in to submit an application");
               return "redirect:/login";
           }

           String email = authentication.getName();
           System.out.println("Debug: Processing application for email: " + email);
           
           Estate estate = estateService.getEstate(estateId);

           // Validate that both dates are provided
           if (application.getArrivalDate() == null || application.getDepartureDate() == null) {
               redirectAttributes.addFlashAttribute("error", "Please select both arrival and departure dates");
               return "redirect:/applications/create/" + estateId;
           }

           // Validate arrival date is before departure date
           if (application.getArrivalDate().isAfter(application.getDepartureDate())) {
               redirectAttributes.addFlashAttribute("error", "Arrival date must be before departure date");
               return "redirect:/applications/create/" + estateId;
           }

           // Set estate and create application
           application.setEstate(estate);
           applicationService.createApplication(application, email);
           
           redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
           return "redirect:/tenants/estates/search";

       } catch (IllegalStateException e) {
           redirectAttributes.addFlashAttribute("error", e.getMessage());
           return "redirect:/applications/create/" + estateId;
       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("error", e.getMessage());
           return "redirect:/applications/create/" + estateId;
       }
   }

   // Display list of applications for logged-in tenant
   // Retrieves tenant by email and fetches their applications
   @GetMapping("/my-applications")
   public String manageApplications(Model model, Authentication authentication) {
       try {
           String email = authentication.getName();
           // Find tenant by email or throw exception if not found
           List<Application> applications = applicationService.getApplicationsByTenant(
               tenantRepository.findByEmail(email).orElseThrow(() -> 
                   new RuntimeException("Tenant not found"))
           );
           model.addAttribute("applications", applications);
           return "tenant/my-applications";
       } catch (Exception e) {
           return "redirect:/error";
       }
   }

   // Update application status endpoint
   @PostMapping("/{id}/update-status")
   @ResponseBody
   public String updateApplicationStatus(@PathVariable int id, @RequestParam String status) {
       try {
           applicationService.updateApplicationStatus(id, status);
           return "success";
       } catch (Exception e) {
           return "error: " + e.getMessage();
       }
   }
}