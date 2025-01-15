package gr.hua.dit.project.real_estates.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table
public class Tenant {

    // Primary key for the Tenant entity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // Tenant's first name
    @Column(name = "first_name")
    private String firstName;

    // Tenant's last name
    @Column(name = "last_name")
    private String lastName;

    // Tenant's email address
    @Column(name = "email")
    private String email;

    // Tenant's phone number
    @Column(name = "phone_number")
    private String phoneNumber;

    // Many-to-Many relationship between Tenant and Estate (A tenant can have multiple estates)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name="estate_tenant",
            joinColumns = @JoinColumn(name="tenant_id"),
            inverseJoinColumns = @JoinColumn(name="estate_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "estate_id"})
    )

    // List of estates associated with the tenant
    private List<Estate> estates; 

    // Default constructor
    public Tenant() {}

     // Constructor with parameters to initialize the tenant
     public Tenant(String firstName, String lastName, String email, String phoneNumber) {
         this.firstName = firstName;
         this.lastName = lastName;
         this.email = email;
         this.phoneNumber = phoneNumber;
     }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Estate> getEstates() {
        return estates;
    }

    public void setEstates(List<Estate> estates) {
        this.estates = estates;
    }

    // toString method to print the tenant's details
    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
