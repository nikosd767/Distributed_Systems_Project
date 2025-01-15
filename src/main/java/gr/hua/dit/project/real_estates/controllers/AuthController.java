package gr.hua.dit.project.real_estates.controllers;

import gr.hua.dit.project.real_estates.entities.Role;
import gr.hua.dit.project.real_estates.repositories.RoleRepository;
import gr.hua.dit.project.real_estates.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controller handling authentication and initial role setup
@Controller
public class AuthController {
    UserRepository userRepository;
    RoleRepository roleRepository;

    public AuthController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Initialize default roles in the system
    @PostConstruct
    public void setup() {
        Role role_tenant = new Role("ROLE_TENANT");
        Role role_owner = new Role("ROLE_OWNER");
        Role role_admin = new Role("ROLE_ADMIN");
        roleRepository.updateOrInsert(role_tenant);
        roleRepository.updateOrInsert(role_owner);
        roleRepository.updateOrInsert(role_admin);
    }

    // Display login page
    @GetMapping("/login")
    public String login() {
            return "auth/login";
    }
}