package gr.hua.dit.project.real_estates.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

// This class represents an estate entity mapped to a database table.
@Entity
@Table
public class Estate {

    // Primary key for the Estate table, auto-generated.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // City where the estate is located. Cannot be blank.
    @NotBlank
    @Column(name = "city")
    private String city;

    // Address of the estate. Cannot be blank.
    @NotBlank
    @Column(name = "address")
    private String address;

    // Optional description of the estate.
    @Column(name = "description")
    private String description;

    // Price of the estate. Cannot be null.
    @NotNull
    @Column(name = "price")
    private double price;

    // Size of the estate in square meters. Cannot be null.
    @NotNull
    @Column(name = "size")
    private int size;

    // Status of the estate from a tenant's perspective (e.g., occupied or not).
    @Column(name = "status_for_tenant")
    @Enumerated
    private EstateStatusForTenant estateStatusForTenant = EstateStatusForTenant.Not_occupied;

    // Status of the estate from an admin's perspective (e.g., pending or approved).
    @Column(name = "status_for_admin")
    @Enumerated
    private EstateStatusForAdmin estateStatusForAdmin = EstateStatusForAdmin.Pending;

    // Year the estate was built.
    @Column(name = "year_of_manufacture")
    private int yearOfManufacture;

    // Title of the estate listing. Must be between 3 and 50 characters.
    @NotBlank
    @Column(name = "title")
    @NotEmpty(message = "Title is required")
    @Size(min = 3, max = 50)
    private String title;

    // Many-to-one relationship with the Owner entity.
    // Each estate is owned by one owner.
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name="estate_owner",
            joinColumns = @JoinColumn(name="estate_id"),
            inverseJoinColumns = @JoinColumn(name="owner_id")
    )
    private Owner owner;

    // Many-to-many relationship with the Tenant entity.
    // An estate can have multiple tenants, and a tenant can rent multiple estates.
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name="estate_tenant",
            joinColumns = @JoinColumn(name="estate_id"),
            inverseJoinColumns = @JoinColumn(name="tenant_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"estate_id", "tenant_id"})
    )
    private List<Tenant> tenants;


    // Constructor to initialize an estate with specific details.
    public Estate(String address, String description, double price, int size, 
    int yearOfManufacture, String title, Owner owner) {
        this.address = address;
        this.description = description;
        this.price = price;
        this.size = size;
        this.yearOfManufacture = yearOfManufacture;
        this.title = title;
    }
    

    // Default constructor
    public Estate() {}

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Owner getOwner() {
        return owner;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    // Adds a single tenant to the tenants list.
    public void addTenant(Tenant tenant) {
        tenants.add(tenant);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public EstateStatusForTenant getEstateStatusForTenant() {
        return estateStatusForTenant;
    }

    public void setEstateStatusForTenant(EstateStatusForTenant estateStatusForTenant) {
        this.estateStatusForTenant = estateStatusForTenant;
    }

    public EstateStatusForAdmin getEstateStatusForAdmin() {
        return estateStatusForAdmin;
    }

    public void setEstateStatusForAdmin(EstateStatusForAdmin estateStatusForAdmin) {
        this.estateStatusForAdmin = estateStatusForAdmin;
    }

    public int getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(int yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }


    // toString method to print the estate details.
    @Override
    public String toString() {
        return "Estate{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", size=" + size +
                ", estateStatusForTenant=" + estateStatusForTenant +
                ", estateStatusForAdmin=" + estateStatusForAdmin +
                ", yearOfManufacture=" + yearOfManufacture +
                ", title='" + title + '\'' +
                '}';
    }
}
