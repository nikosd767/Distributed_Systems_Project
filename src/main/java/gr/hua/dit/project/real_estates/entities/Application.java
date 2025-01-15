package gr.hua.dit.project.real_estates.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

// This class represents an application for renting an estate and is mapped to a database table.
@Entity
@Table
public class Application {

    // Primary key for the Application table.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // Many-to-one relationship with the Tenant entity.
    // Specifies that each application is associated with exactly one tenant.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    @NotNull // Ensures the tenant cannot be null.
    private Tenant tenant;

    // Many-to-one relationship with the Estate entity.
    // Specifies that each application is linked to exactly one estate.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estate_id")
    @NotNull
    private Estate estate;

    // Date when the tenant plans to arrive at the estate.
    @NotNull
    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    // Date when the tenant plans to leave the estate.
    @Column(name = "departure_date")
    private LocalDate departureDate;

    // Status of the application (e.g., PENDING, APPROVED).
    @NotNull
    @Column(name = "status")
    private String status = "PENDING"; // Default status is set to "PENDING".

    // Date when the application was created.
    @NotNull
    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now(); // Automatically sets to the current date.

    // Default constructor
    public Application() {}

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Estate getEstate() {
        return estate;
    }

    public void setEstate(Estate estate) {
        this.estate = estate;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
