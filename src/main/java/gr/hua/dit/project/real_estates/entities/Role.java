package gr.hua.dit.project.real_estates.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // Primary key for the Role entity

    @Column(length = 20)
    private String name;  // Name of the role (e.g., "Admin", "User")

    // Default constructor for JPA
    public Role() {

    }

    // Constructor to initialize the role with a name
    public Role(String name) {
        this.name = name;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString method to return the role name as a string
    @Override
    public String toString() {
        return name;  // Returns the role's name as its string representation
    }
}
