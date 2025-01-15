// Package declaration for the Real Estates project
package gr.hua.dit.project.real_estates.entities;

// Required imports for JPA, validation, and utilities
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

// Entity class representing the Users table with unique constraints on username and email
@Entity
@Table(	name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {
    // Primary key field with auto-increment strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userID;

    // Username field
    @NotBlank
    @Size(max = 20)
    private String username;

    // Password field
    @NotBlank
    @Size(max = 120)
    private String password;

    // Email field
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    // Status field using an enum type, defaults to Pending
    @Enumerated
    private OtherStatus status = OtherStatus.Pending;

    // Many-to-Many relationship with Role entity
    //Each User can have multiple Roles
    //Each Role can be assigned to multiple Users
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Default constructor
    public User() {}

    // Constructor
    public User(String username, String email, String password, OtherStatus status) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userID;
    }

    public void setUserId(Integer id) {
        this.userID = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public OtherStatus getStatus() {
        return status;
    }

    public void setStatus(OtherStatus status) {
        this.status = status;
    }

    // Override toString method to return username
    @Override
    public String toString() {
        return username;
    }
}