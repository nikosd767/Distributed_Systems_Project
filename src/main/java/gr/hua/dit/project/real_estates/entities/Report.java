package gr.hua.dit.project.real_estates.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reports")
public class Report {

    // Unique identifier for the report.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;

    // Many-to-One Relationship: A Report is associated with one User
    // A User can have many Reports, but each Report is associated with exactly one User.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    // Many-to-One Relationship: A Report is associated with one Application
    // An Application can have many Reports, but each Report is associated with exactly one Application.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id")
    private Application application;

    // Title of the report.
    private String title;

    // Detailed description of the report (up to 1000 characters).
    @Column(length = 1000)
    private String description;

    // Status of the report, stored as an ordinal value.
    @Enumerated(EnumType.ORDINAL)
    private OtherStatus status = OtherStatus.Pending;

    // Date when the report was created.
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportDate;

    // Default constructor initializing the report date to the current date.
    public Report() {
        this.reportDate = new Date();
    }

    // Parameterized constructor for creating a report with specific details.
    public Report(User user, Application application, String title, String description) {
        this.user = user;
        this.application = application;
        this.title = title;
        this.description = description;
        this.reportDate = new Date();
        this.status = OtherStatus.Pending;
    }

    // Getters and setters
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OtherStatus getStatus() {
        return status;
    }

    public void setStatus(OtherStatus status) {
        this.status = status;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
}
