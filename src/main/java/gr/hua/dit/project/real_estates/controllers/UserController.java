package gr.hua.dit.project.real_estates.controllers;

import gr.hua.dit.project.real_estates.entities.Role;
import gr.hua.dit.project.real_estates.entities.User;
import gr.hua.dit.project.real_estates.entities.Owner;
import gr.hua.dit.project.real_estates.entities.Tenant;
import gr.hua.dit.project.real_estates.repositories.RoleRepository;
import gr.hua.dit.project.real_estates.repositories.OwnerRepository;
import gr.hua.dit.project.real_estates.repositories.TenantRepository;
import gr.hua.dit.project.real_estates.service.UserService;
import gr.hua.dit.project.real_estates.service.EstateService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

@Controller
public class UserController {

    // Required services and repositories for user operations
    private UserService userService;
    private EstateService estateService;
    private RoleRepository roleRepository;
    private OwnerRepository ownerRepository;
    private TenantRepository tenantRepository;

    // Constructor
    public UserController(UserService userService, RoleRepository roleRepository,
                          OwnerRepository ownerRepository, TenantRepository tenantRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.ownerRepository = ownerRepository;
        this.tenantRepository = tenantRepository;
    }

    // Shows registration page
    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "auth/register";
    }

    // Processes user registration and creates Owner or Tenant based on role
    @PostMapping("/saveUser")
    @Transactional
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                           @RequestParam("role") String role,
                           @RequestParam("phoneNumber") String phoneNumber,
                           @RequestParam("firstName") String firstName,
                           @RequestParam("lastName") String lastName,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {

            Role userRole;
            if (role.equals("OWNER")) {
                userRole = roleRepository.findByName("ROLE_OWNER")
                        .orElseThrow(() -> new RuntimeException("Error: Role OWNER is not found."));


                Owner owner = new Owner();
                owner.setFirstName(firstName);
                owner.setLastName(lastName);
                owner.setEmail(user.getEmail());
                owner.setPhoneNumber(phoneNumber);
                ownerRepository.save(owner);
            } else {
                userRole = roleRepository.findByName("ROLE_TENANT")
                        .orElseThrow(() -> new RuntimeException("Error: Role TENANT is not found."));

                Tenant tenant = new Tenant();
                tenant.setFirstName(firstName);
                tenant.setLastName(lastName);
                tenant.setEmail(user.getEmail());
                tenant.setPhoneNumber(phoneNumber);
                System.out.println("Saving tenant: " + tenant.getEmail());
                Tenant savedTenant = tenantRepository.save(tenant);
                System.out.println("Tenant saved with ID: " + savedTenant.getId());
            }


            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);

            // Save user to database
            System.out.println("Saving user: " + user.getEmail());
            Integer userId = userService.saveUser(user);
            System.out.println("User saved with ID: " + userId);

            if (userId == null) {
                throw new RuntimeException("Failed to save user");
            }

            return "redirect:/login?registered";
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred during registration: " + e.getMessage());
            return "auth/register";
        }
    }

    // Lists all registered users
    @GetMapping("/users")
    public String showUsers(Model model){
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "users/users";
    }

    // Shows details of specific user
    @GetMapping("/user/{user_id}")
    public String showUser(@PathVariable Long user_id, Model model){
        model.addAttribute("user", userService.getUser(user_id));
        return "users/user";
    }

    // Save tenant details
    @PostMapping("/user/{user_id}")
    public String saveTenant(@PathVariable Long user_id, @ModelAttribute("user") User user, BindingResult bindingResult,Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println("error");
            return "/users/users";
        } else {
            User the_user = (User) userService.getUser(user_id);
            the_user.setEmail(user.getEmail());
            the_user.setUsername(user.getUsername());
            userService.updateUser(the_user);
            model.addAttribute("users", userService.getUsers());
            return "users/users";
        }
    }

    // Assigns new role to user
    @GetMapping("/user/role/add/{user_id}/{role_id}")
    public String addRoletoUser(@PathVariable Long user_id, @PathVariable Integer role_id, Model model){
        User user = (User) userService.getUser(user_id);
        Role role = roleRepository.findById(role_id).get();
        user.getRoles().add(role);
        System.out.println("Roles: "+user.getRoles());
        userService.updateUser(user);
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "users/users";

    }

    // Shows users awaiting approval
    @GetMapping("/pending-users")
    public String showPendingUsers(Model model) {
        model.addAttribute("users", userService.getPendingUsers());
        return "users/pending-users";
    }

    // Approves user registration
    @PostMapping("/users/{id}/approve")
    public String approveUser(@PathVariable int id) {
        userService.approveUser(id);
        return "redirect:/pending-users";
    }

    // Removes user from system
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return "redirect:/pending-users";
    }

    // Removes pending user registration
    @PostMapping("/pending-users/{id}/delete")
    public String deletePendingUser(@PathVariable long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deletePendingUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/pending-users";
    }

    // Removes estate from system
    @DeleteMapping("/estates/{id}")
    public String deleteEstate(@PathVariable int id) {
        estateService.deleteEstate(id);
        return "redirect:/estate/estates";
    }
}