package gr.hua.dit.project.real_estates.controllers;

import gr.hua.dit.project.real_estates.entities.Estate;
import gr.hua.dit.project.real_estates.service.EstateService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller for managing real estate properties
@Controller
@RequestMapping("estate")
public class EstateController {

   private EstateService estateService;

   public EstateController(EstateService estateService) {
       this.estateService = estateService;
   }

   // Display estates list with optional filtering for pending/approved status
   @RequestMapping()
   public String showEstates(@RequestParam(required = false) String filter, Model model) {
       List<Estate> estates;
       
       if ("pending".equalsIgnoreCase(filter)) {
           estates = estateService.getPendingEstates();
           model.addAttribute("filter", "pending");
       } else if ("approved".equalsIgnoreCase(filter)) {
           estates = estateService.getApprovedEstates();
           model.addAttribute("filter", "approved");
       } else {
           estates = estateService.getEstates();
       }
       
       model.addAttribute("estates", estates);
       return "estate/estates";
   }

   // Display single estate details by ID
   @GetMapping("/{id}")
   public String showEstate(@PathVariable Integer id, Model model){
       Estate estate = estateService.getEstate(id);
       model.addAttribute("estates", estate);
       return "estate/estates";
   }

   // Show form for creating new estate - only accessible by property owners
   @Secured("ROLE_OWNER")
   @GetMapping("/new")
   public String addEstate(Model model){
       Estate estate = new Estate();
       model.addAttribute("estate", estate);
       return "estate/estate";
   }

   // Handle new estate creation with validation
   @Secured("ROLE_OWNER")
   @PostMapping("/new")
   public String saveEstate(@Valid @ModelAttribute("estate") Estate estate, 
                          BindingResult bindingResult, 
                          Model model) {
       if (bindingResult.hasErrors()) {
           System.out.println("Error in estate validation: " + bindingResult.getAllErrors());
           return "estate/estate";
       }
       
       estateService.saveEstate(estate);
       model.addAttribute("estates", estateService.getEstates());
       model.addAttribute("successMessage", "Estate added successfully!");
       return "redirect:/owner/my-properties";
   }

   // Show form for updating existing estate
   @Secured("ROLE_OWNER")
   @GetMapping("/{id}/update")
   public String showUpdateEstateForm(@PathVariable int id, Model model) {
       Estate estate = estateService.getEstate(id);
       model.addAttribute("estate", estate);
       return "estate/estate";
   }

   // Handle estate update with validation and property transfer
   @Secured("ROLE_OWNER")
   @PostMapping("/{estate_id}/update")
   public String updateEstate(@PathVariable int estate_id, 
                            @Valid @ModelAttribute("estate") Estate estate, 
                            BindingResult bindingResult, 
                            Model model) {
       if (bindingResult.hasErrors()) {
           System.out.println("Error in estate update: " + bindingResult.getAllErrors());
           return "estate/estate";
       }
       
       try {
           // Get existing estate and update its properties
           Estate existingEstate = estateService.getEstate(estate_id);
           existingEstate.setTitle(estate.getTitle());
           existingEstate.setCity(estate.getCity());
           existingEstate.setAddress(estate.getAddress());
           existingEstate.setDescription(estate.getDescription());
           existingEstate.setPrice(estate.getPrice());
           existingEstate.setSize(estate.getSize());
           existingEstate.setYearOfManufacture(estate.getYearOfManufacture());
           
           estateService.updateEstate(existingEstate);
           model.addAttribute("successMessage", "Estate updated successfully!");
           return "redirect:/owner/my-properties";
       } catch (RuntimeException e) {
           model.addAttribute("errorMessage", "Estate not found or could not be updated");
           return "estate/estate";
       }
   }

   // Delete estate - only accessible by admin
   @Secured("ROLE_ADMIN")
   @PostMapping("/{id}/delete")
   public String deleteEstate(@PathVariable Integer id) {
       estateService.deleteEstate(id);
       return "redirect:/estate";
   }

   // Display pending estates - only accessible by admin
   @GetMapping("/pending")
   @Secured("ROLE_ADMIN")
   public String showPendingEstates(Model model) {
       model.addAttribute("estates", estateService.getPendingEstates());
       return "estate/pending-estates";
   }

   // Handle estate approval - only accessible by admin
   @Secured("ROLE_ADMIN")
   @PostMapping("/{id}/approve")
   public String approveEstate(@PathVariable Integer id) {
       estateService.approveEstate(id);
       return "redirect:/estate";
   }
}