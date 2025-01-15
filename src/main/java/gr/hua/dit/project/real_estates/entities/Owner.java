package gr.hua.dit.project.real_estates.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table
public class Owner {

    // Unique identifier for the owner.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // The owner's contact phone number.
    @Column(name = "phone_number")
    private String phoneNumber;

    // The owner's first name.
    @Column(name = "first_name")
    private String firstName;

    // The owner's last name.
    @Column(name = "last_name")
    private String lastName;

    // The owner's email address.
    @Column(name = "email")
    private String email;

    // List of estates owned by this owner.
    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    private List<Estate> estates;

    // Default constructor.
    public Owner() {
    }

    // Parameterized constructor to initialize the owner's details.
    public Owner(String phoneNumber, String firstName, String lastName, String email) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public List<Estate> getEstates() {
        return estates;
    }

    public void setEstates(List<Estate> estates) {
        this.estates = estates;
    }

    // toString method to print the owner's details.
    @Override
    public String toString() {
        return "Owner{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
