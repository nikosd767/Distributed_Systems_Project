package gr.hua.dit.project.real_estates.service;

import gr.hua.dit.project.real_estates.entities.*;

import gr.hua.dit.project.real_estates.repositories.RoleRepository;
import gr.hua.dit.project.real_estates.repositories.ReportRepository;
import gr.hua.dit.project.real_estates.repositories.UserRepository;
import gr.hua.dit.project.real_estates.repositories.OwnerRepository;
import gr.hua.dit.project.real_estates.repositories.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private ReportRepository reportRepository;
    private RoleRepository roleRepository;
    private OwnerRepository ownerRepository;
    private TenantRepository tenantRepository;
    private OwnerService ownerService;
    private TenantService tenantService;
    // Password encoder for secure password storage
    private BCryptPasswordEncoder passwordEncoder;

    // Constructor
    public UserService(UserRepository userRepository, RoleRepository roleRepository, 
                      BCryptPasswordEncoder passwordEncoder, OwnerRepository ownerRepository,
                      TenantRepository tenantRepository, OwnerService ownerService,
                      TenantService tenantService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.ownerRepository = ownerRepository;
        this.tenantRepository = tenantRepository;
        this.ownerService = ownerService;
        this.tenantService = tenantService;
    }

    // Check if username or email already exists.
    public void checkUserExists(String username, String email) {
        StringBuilder errors = new StringBuilder();
        
        if (userRepository.existsByUsername(username)) {
            errors.append("Username already exists. ");
        }
        if (userRepository.existsByEmail(email)) {
            errors.append("Email already exists.");
        }
        
        if (errors.length() > 0) {
            throw new IllegalArgumentException(errors.toString().trim());
        }
    }

    //Save new user with encoded password.
    @Transactional
    public Integer saveUser(User user) {
        try {
            String passwd = user.getPassword();
            String encodedPassword = passwordEncoder.encode(passwd);
            user.setPassword(encodedPassword);

            System.out.println("UserService: Saving user with email: " + user.getEmail());
            user = userRepository.save(user);
            System.out.println("UserService: User saved with ID: " + user.getUserId());
            return user.getUserId();
        } catch (Exception e) {
            System.err.println("UserService: Error saving user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Update existing user.
    @Transactional
    public Integer updateUser(User user) {
        user = userRepository.save(user);
        return user.getUserId();
    }

    // Load user by username
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opt = userRepository.findByUsername(username);

        if(opt.isEmpty())
            throw new UsernameNotFoundException("User with email: " + username +" not found !");
        else {
            User user = opt.get();
            if (user.getStatus() == OtherStatus.Pending) {
                throw new BadCredentialsException("Your account is pending administrator approval. Please wait for confirmation.");
            }
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.getRoles()
                            .stream()
                            .map(role-> new SimpleGrantedAuthority(role.toString()))
                            .collect(Collectors.toSet())
            );
        }
    }

    // Get all users.
    @Transactional
    public Object getUsers() {
        return userRepository.findAll();
    }

    // Get user by ID.
    public Object getUser(Long userId) {
        return userRepository.findById(userId).get();
    }

    // Update or insert role.
    @Transactional
    public void updateOrInsertRole(Role role) {
        roleRepository.updateOrInsert(role);
    }

    // Get users with pending status.
    @Transactional
    public List<User> getPendingUsers() {
        return userRepository.findByStatus(OtherStatus.Pending);
    }

    // Approve pending user.
    @Transactional
    public void approveUser(long userId) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RuntimeException("Estate not found"));
        user.setStatus(OtherStatus.Approved);
        userRepository.save(user);
    }

    // Get all reports.
    public List<Report> getReports() {
        return reportRepository.findAll();
    }

    // Delete user by ID.
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    // Find user by username.
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Find user by email.
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Delete pending user and related data.
    @Transactional
    public void deletePendingUser(long id) {
        User user = (User) getUser(id);
        if (user != null) {
            if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_OWNER"))) {
                Owner owner = ownerRepository.findByEmail(user.getEmail());
                if (owner != null) {
                    ownerService.deleteOwner(owner.getId());
                }
            } else if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_TENANT"))) {
                Optional<Tenant> tenantOpt = tenantRepository.findByEmail(user.getEmail());
                if (tenantOpt.isPresent()) {
                    tenantService.deleteTenant(tenantOpt.get().getId());
                }
            }
        }
    }
}